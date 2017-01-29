/* 
 * Modul communicator definuje funkce pro komunikace serveru s klienty.
 * 
 * Author: Petr Kozler
 */

#include "communicator.h"
#include "config.h"
#include "protocol.h"
#include "logger.h"
#include "server_stats.h"
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
 * @param socket socket klienta - odesílatele
 * @return zpráva v textové formě (standardní řetězec, délka smí být nulová) nebo NULL
 */
char *read_from_socket(client_socket_t *socket) {
    int32_t str_len;
    
    int n = read_bytes(socket->sock, &str_len, sizeof(int32_t));
    if (n < 1) {
        socket->connected = false;
        
        if (n < 0) {
            print_recv(NULL, socket->sock, false);
        }
        
        return NULL;
    }
    
    inc_stats_bytes_transferred((int32_t) n);
    n = 0;
    
    // převod pořadí bajtů čísla délky
    str_len = ntohl(str_len);
    
    if (str_len < 0 || str_len > MAX_MESSAGE_LENGTH) {
        socket->connected = false;
        print_recv("Neplatná délka řetězce zprávy.", socket->sock, false);
        
        return NULL;
    }
    
    char *msg_str = (char *) malloc(sizeof(char) * (str_len + 1));
    
    if (str_len == 0) {
        msg_str[0] = '\0';
        print_recv(msg_str, socket->sock, true);
        
        return msg_str;
    }
        
    n = read_bytes(socket->sock, msg_str, str_len);

    if (n < 1) {
        socket->connected = false;
        
        if (n < 0) {
            print_recv(NULL, socket->sock, false);
        }
        
        return NULL;
    }

    msg_str[str_len] = '\0';
    print_recv(msg_str, socket->sock, true);
    inc_stats_bytes_transferred((int32_t) n);
    
    return msg_str;
}

/**
 * Zjistí délku řetězce zprávy určené k odeslání klientovi. Klientovi poté zapíše
 * do socketu tuto hodnotu, následovanou jednotlivými znaky zprávy. V případě, že
 * zadaný řetězec obsahuje pouze nulový znak, odešle klientovi pouze délku zprávy
 * s hodnotou 0 (testování odezvy).
 * 
 * @param msg_str zpráva v textové formě (standardní řetězec, délka smí být nulová)
 * @param socket socket klienta - příjemce
 * @return true, pokud byl přenos úspěšný, jinak false
 */
bool write_to_socket(char *msg_str, client_socket_t *socket) {
    if (msg_str == NULL) {
        print_send("Předána neplatná struktura zprávy.", socket->sock, false);
        
        return false;
    }
    
    int32_t str_len = (int32_t) strlen(msg_str);
    //printf("N = %d\n", str_len);
    // převod pořadí bajtů čísla délky
    str_len = htonl(str_len);
    int n = write_bytes(socket->sock, &str_len, sizeof(int32_t));
    
    if (n < 0) {
        print_send(NULL, socket->sock, false);
        
        return false;
    }
    
    str_len = ntohl(str_len);
    inc_stats_bytes_transferred((int32_t) n);
    n = 0;
    
    if (str_len < 0 || str_len > MAX_MESSAGE_LENGTH) {
        print_send("Neplatná délka řetězce zprávy.", socket->sock, false);
        
        return false;
    }
    
    if (str_len == 0) {
        print_send(msg_str, socket->sock, true);
        
        return true;
    }
    
    n = write_bytes(socket->sock, msg_str, str_len);
    
    if (n < 0) {
        print_send(NULL, socket->sock, false);
        
        return false;
    }
    
    print_send(msg_str, socket->sock, true);
    inc_stats_bytes_transferred((int32_t) n);
    
    return true;
}

/**
 * Přijme zprávu od klienta se zadaným číslem socketu a převede ji z textové formy
 * do formy struktury určené pro další zpracování v programu, kterou předá
 * volající funkci v návratové hodnotě. V případě zjištění neplatné zprávy vrací
 * hodnotu NULL.
 * 
 * @param socket socket klienta - odesílatele
 * @return přijatá platná zpráva ve formě struktury nebo NULL
 */
message_t *receive_message(client_socket_t *socket) {
    char *msg_str = read_from_socket(socket);
    
    // došlo k chybě při přenosu řetězce zprávy
    if (msg_str == NULL) {
        inc_stats_transfers_failed();
        
        return NULL;
    }
    
    inc_stats_messages_transferred();
    
    // řetezec má nulovou délku (test odezvy)
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
    
    // nepodařilo se načíst argument z řetězce - neplatný formát zprávy
    if (pch == NULL || strlen(pch) == 0) {
        return NULL;
    }
    
    message_t *message = create_message(pch, delim_cnt);
    
    while (pch = __strtok_r(msg_str, SEPARATOR, &saveptr)) {
        // prázdný argument - neplatný formát zprávy
        if (strlen(pch) == 0) {
            return NULL;
        }
        
        message->argv[message->counter++] = copy_string(pch);
        msg_str = saveptr;
    }
    
    // zpráva byla úspěšně přijata
    message->argc = message->counter;
    
    return message;
}

/**
 * Převede zadanou zprávu z formy struktury do textové formy určené k přenosu
 * po síti a odešle ji v této podobě klientovi se zadaným číslem socketu.
 * 
 * @param msg odesílaná zpráva ve formě struktury
 * @param socket socket klienta - příjemce
 * @return true v případě úspěchu, false v případě chyby
 */
bool send_message(message_t *msg, client_socket_t *socket) {
    char *msg_str;
    
    // zpráva má typ
    if (msg->type != NULL) {
        int32_t str_len = strlen(msg->type);
        
        int32_t i;
        for (i = 0; i < msg->argc; i++) {
            str_len += (1 + strlen(msg->argv[i]));
        }
        str_len++;
        
        msg_str = (char *) malloc(sizeof(char) * (str_len));
        msg_str[0] = '\0';
        strncat(msg_str, msg->type, str_len);
        
        for (i = 0; i < msg->argc; i++) {
            strncat(msg_str, SEPARATOR, str_len);
            strncat(msg_str, msg->argv[i], str_len);
        }
    }
    // zpráva nemá typ ani argumenty (odpověď na test odezvy)
    else if (msg->type == NULL && msg->argc == 0) {
        msg_str = (char *) malloc(sizeof(char));
        msg_str[0] = '\0';
    }
    // zpráva nemá typ, ale má argumenty - neplatná zpráva
    else {
        msg_str = NULL;
    }
    
    lock_socket(socket);
    bool success = write_to_socket(msg_str, socket);
    unlock_socket(socket);
    
    if (msg_str != NULL) {
        free(msg_str);
    }
    
    // došlo k chybě při přenosu řetězce zprávy
    if (!success) {
        inc_stats_transfers_failed();
        
        return false;
    }
    
    // zpráva byla úspěšně odeslána
    inc_stats_messages_transferred();
    
    return true;
}