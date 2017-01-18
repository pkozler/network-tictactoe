/* 
 * Modul tcp_communicator definuje funkce pro komunikace serveru s klienty.
 * 
 * Author: Petr Kozler
 */

#include "tcp_communicator.h"
#include "config.h"
#include "protocol.h"
#include "logger.h"
#include "com_stats.h"
#include "string_utils.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <sys/socket.h>
#include <arpa/inet.h>

/**
 * Provádí postupné čtení ze socketu, dokud nejsou načteny všechny bajty
 * přijímané zprávy. V případě chyby (např. přerušení spojení) vrací
 * chybovou hodnotu.
 * 
 * @param sock deskriptor socketu příjemce
 * @param buf blok paměti s bajty určenými k zápisu
 * @param len počet bajtů určených k zápisu
 * @return 1 v případě úspěchu, nekladná hodnota v případě chyby
 */
int read_bytes(int sock, void *buf, unsigned int len) {
    int bytes_read = 0;
    int read_result = 0;

    while (bytes_read < len) {
        read_result = recv(sock, buf + bytes_read, len - bytes_read, 0);

        if (read_result < 1) {
            // chyba - spojení přerušeno
            return read_result;
        }

        bytes_read += read_result;
    }
    
    return bytes_read;
}

/**
 * Provádí postupný zápis do socketu, dokud nejsou zapsány všechny bajty
 * odesílané zprávy. V případě chyby (např. přerušení spojení) vrací
 * chybovou hodnotu.
 * 
 * @param sock deskriptor socketu příjemce
 * @param buf blok paměti s bajty určenými k zápisu
 * @param len počet bajtů určených k zápisu
 * @return 0 v případě úspěchu, záporná hodnota v případě chyby
 */
int write_bytes(int sock, void *buf, unsigned int len) {
    int bytes_wrote = 0;
    int write_result = 0;

    while (bytes_wrote < len) {
        write_result = send(sock, buf + bytes_wrote, len - bytes_wrote, 0);

        if (write_result < 0) {
            // chyba - spojení přerušeno
            return write_result;
        }

        bytes_wrote += write_result;
    }
    
    return bytes_wrote;
}

/**
 * Načte délku řetězce přijaté zprávy od klienta se zadaným číslem socketu
 * a následně načte jednotlivé znaky zprávy ze socketu, které upraví do podoby
 * standardního řetězce zakončeného nulovým znakem. Tento řetězec poté předá
 * volající funkci v návratové hodnotě. V případě, že délka zprávy má hodnotu 0
 * (testování odezvy) vrací pouze nulový znak. V případě chyby vrací hodnotu NULL.
 * 
 * @param sock deskriptor socketu klienta - odesílatele
 * @param příznak průběhu přenosu (uloží se true, je-li spojení v pořádku, jinak true)
 * @return zpráva v textové formě (standardní řetězec, délka smí být nulová) nebo NULL
 */
char *read_from_socket(int sock, bool *success) {
    int32_t str_len;
    
    int n = read_bytes(sock, &str_len, sizeof(int32_t));
    if (n < 1) {
        *success = false;
        
        return NULL;
    }
    
    inc_stats_bytes_transferred((int32_t) n);
    n = 0;
    
    // převod pořadí bajtů čísla délky
    str_len = ntohl(str_len);
    
    if (str_len < 0 || str_len > MAX_MESSAGE_LENGTH) {
        *success = false;
        
        return NULL;
    }
    
    char *msg_str = (char *) malloc(sizeof(char) * (str_len + 1));
    
    if (str_len > 0) {
        n = read_bytes(sock, msg_str, str_len);
        
        if (n < 1) {
            *success = false;
            
            return NULL;
        }
        
        inc_stats_bytes_transferred((int32_t) n);
    }
    
    msg_str[str_len] = '\0';
    
    return msg_str;
}

/**
 * Zjistí délku řetězce zprávy určené k odeslání klientovi. Klientovi poté zapíše
 * do socketu tuto hodnotu, následovanou jednotlivými znaky zprávy. V případě, že
 * zadaný řetězec obsahuje pouze nulový znak, odešle klientovi pouze délku zprávy
 * s hodnotou 0 (testování odezvy).
 * 
 * @param msg_str zpráva v textové formě (standardní řetězec, délka smí být nulová)
 * @param sock deskriptor socketu klienta - příjemce
 * @return true v případě úspěchu, false v případě chyby
 */
bool write_to_socket(char *msg_str, int sock) {
    int32_t str_len = (int32_t) strlen(msg_str); 
    // převod pořadí bajtů čísla délky
    str_len = htonl(str_len);
    int n = write_bytes(sock, &str_len, sizeof(int32_t));
    
    if (n < 0) {
        return false;
    }
    
    str_len = ntohl(str_len);
    inc_stats_bytes_transferred((int32_t) n);
    n = 0;
    
    if (str_len < 0) {
        return false;
    }
    
    if (str_len == 0) {
        return true;
    }
    
    n = write_bytes(sock, msg_str, str_len);
    
    if (n < 0) {
        return false;
    }
    
    inc_stats_bytes_transferred((int32_t) n);
    
    return true;
}

/**
 * Přijme zprávu od klienta se zadaným číslem socketu a převede ji z textové formy
 * do formy struktury určené pro další zpracování v programu, kterou předá
 * volající funkci v návratové hodnotě. V případě zjištění neplatné zprávy vrací
 * hodnotu NULL.
 * 
 * @param sock deskriptor socketu klienta - odesílatele
 * @param příznak průběhu přenosu (uloží se true, je-li spojení v pořádku, jinak true)
 * @return přijatá platná zpráva ve formě struktury nebo NULL
 */
message_t *receive_message(int sock, bool *success) {
    *success = true;
    char *msg_str = read_from_socket(sock, success);
    
    if (msg_str == NULL) {
        inc_stats_transfers_failed();
        //append_log("Chyba při příjmu zprávy od klienta s číslem socketu %d: \"%s\"", sock, msg_str);
        
        return NULL;
    }
    
    inc_stats_messages_transferred();
    //append_log("Přijata zpráva od klienta s číslem socketu %d: \"%s\"", sock, msg_str);
    
    if (strlen(msg_str) == 0) {
        free(msg_str);
        
        return create_message(NULL, 0);
    }
    
    int32_t delim_cnt = 0;
    
    char *pch = strpbrk(msg_str, SEPARATOR);
    
    while (pch != NULL) {
        delim_cnt++;
        pch = strpbrk(pch + 1, SEPARATOR);
    }
    
    char *saveptr;
    
    pch = __strtok_r(msg_str, SEPARATOR, &saveptr);
    msg_str = saveptr;
    
    if (pch == NULL || strlen(pch) == 0) {
        return NULL;
    }
    
    message_t *message = create_message(pch, delim_cnt);
    
    while (pch = __strtok_r(msg_str, SEPARATOR, &saveptr)) {
        if (strlen(pch) == 0) {
            return NULL;
        }
        
        message->argv[message->counter++] = copy_string(pch);
        msg_str = saveptr;
    }
    
    message->argc = message->counter;
    
    return message;
}

/**
 * Převede zadanou zprávu z formy struktury do textové formy určené k přenosu
 * po síti a odešle ji v této podobě klientovi se zadaným číslem socketu.
 * 
 * @param msg odesílaná zpráva ve formě struktury
 * @param sock deskriptor socketu klienta - příjemce
 * @return true v případě úspěchu, false v případě chyby
 */
bool send_message(message_t *msg, int sock) {
    char *msg_str;
    
    if (msg->type != NULL) {
        int32_t str_len = strlen(msg->type) + msg->argc;
        
        int32_t i;
        for (i = 0; i < msg->argc; i++) {
            str_len += strlen(msg->argv[i]);
        }
        
        msg_str = (char *) malloc(sizeof(char) * (str_len + 1));
        msg_str[0] = '\0';
        strcat(msg_str, msg->type);
        
        for (i = 0; i < msg->argc; i++) {
            strcat(msg_str, SEPARATOR);
            strcat(msg_str, msg->argv[i]);
            
        }
        
        printf("%s\n", msg_str);
    }
    else if (msg->type == NULL && msg->argc == 0) {
        msg_str = (char *) malloc(sizeof(char));
        msg_str[0] = '\0';
    }
    else {
        return false;
    }
    
    bool result = write_to_socket(msg_str, sock);
    
    if (result) {
        inc_stats_messages_transferred();
        //append_log("Odeslána zpráva klientovi s číslem socketu %d: \"%s\"", sock, msg_str);
    }
    else {
        inc_stats_transfers_failed();
        //append_log("Chyba při odesílání zprávy klientovi s číslem socketu %d: \"%s\"", sock, msg_str);
    }
    
    free(msg_str);
    
    return result;
}