/* 
 * Author: Petr Kozler
 */

#include "message.h"
#include "config.h"
#include <stdbool.h>
#include <sys/socket.h>
#include <string.h>

/**
 * Provádí postupné čtení ze socketu, dokud nejsou načteny všechny bajty
 * přijímané zprávy. V případě chyby (např. přerušení spojení) vrací
 * chybovou hodnotu.
 * 
 * @param sock deskriptor socketu příjemce
 * @param buf blok paměti s bajty určenými k zápisu
 * @param len počet bajtů určených k zápisu
 * @return 0 v případě úspěchu, záporná hodnota v případě chyby
 */
int read_bytes(int sock, void *buf, unsigned int len) {
    int bytes_read = 0;
    int read_result = 0;

    while (bytes_read < len) {
        read_result = recv(sock, buf + bytes_read, len - bytes_read, 0);

        if (read_result < 0) {
            // chyba - spojení přerušeno
            return read_result;
        }

        bytes_read += read_result;
    }
    
    return 0;
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
    
    return 0;
}

/**
 * Načte délku řetězce přijaté zprávy od klienta se zadaným číslem socketu
 * a následně načte jednotlivé znaky zprávy ze socketu, které upraví do podoby
 * standardního řetězce zakončeného nulovým znakem. Tento řetězec poté předá
 * volající funkci v návratové hodnotě. V případě, že délka zprávy má hodnotu 0
 * (testování odezvy) vrací pouze nulový znak. V případě chyby vrací hodnotu NULL.
 * 
 * @param sock deskriptor socketu klienta - odesílatele
 * @return zpráva v textové formě (standardní řetězec, délka smí být nulová) nebo NULL
 */
char *read_from_socket(int sock) {
    int32_t str_len;

    if (read_bytes(sock, &str_len, sizeof(int32_t)) < 0) {
        return NULL;
    }
    
    // převod pořadí bajtů čísla délky
    str_len = ntohl(str_len);
    
    char *msg_str = (char *) malloc(sizeof(char) * str_len + 1);
    
    if (read_bytes(sock, msg_str, str_len) < 0) {
        return NULL;
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

    if (write_bytes(sock, &str_len, sizeof(int32_t)) < 0) {
        return false;
    }
    
    // převod pořadí bajtů čísla délky
    str_len = htonl(str_len);
    
    if (write_bytes(sock, msg_str, str_len) < 0) {
        return false;
    }
    
    return true;
}

/**
 * Vytvoří novou strukturu zprávy zadaného typu se zadaným počtem parametrů
 * určenou k odeslání klientovi.
 * 
 * @param msg_type typ zprávy
 * @param msg_argc počet parametrů
 * @return alokovaná struktura zprávy
 */
message_t *create_message(char *msg_type, int32_t msg_argc) {
    message_t *message = (message_t *) malloc(sizeof(message_t));
    
    message->type = msg_type;
    message->argc = msg_argc;
    
    if (message->argc > 0) {
        message->argv = (char **) malloc(sizeof(char *) * msg_argc);
    }
    
    return message;
}

/**
 * Odstraní vytvořenou strukturu zprávy.
 * 
 * @param msg alokovaná struktura zprávy
 */
void delete_message(message_t *msg) {
    int32_t i;
    
    for (i = 0; i < msg->argc; i++) {
        free(msg->argv[i]);
    }
    
    if (msg->argc > 0) {
        free(msg->argv);
    }
    
    free(msg);
}

/**
 * Přijme zprávu od klienta se zadaným číslem socketu a převede ji z textové formy
 * do formy struktury určené pro další zpracování v programu, kterou předá
 * volající funkci v návratové hodnotě. V případě zjištění neplatné zprávy vrací
 * hodnotu NULL.
 * 
 * @param sock deskriptor socketu klienta - odesílatele
 * @return přijatá platná zpráva ve formě struktury nebo NULL
 */
message_t *receive_message(int sock) {
    char *msg_str = read_from_socket(sock);
    
    if (msg_str == NULL) {
        return NULL;
    }
    
    if (strlen(msg_str) == 0) {
        free(msg_str);
        
        return create_message(NULL, 0);
    }
    
    int32_t delim_cnt = 0;
    
    char *pch = strpbrk(msg_str, DELIMITER);
    
    while (pch != NULL) {
        delim_cnt++;
        pch = strpbrk(pch + 1, DELIMITER);
    }
    
    pch = strtok(msg_str, DELIMITER);
    
    if (pch == NULL || strlen(pch) == 0) {
        return NULL;
    }
    
    message_t *message = create_message(pch, delim_cnt);
    
    int32_t i = 0;
    pch = strtok(NULL, DELIMITER);
    
    while (pch != NULL) {
        if (strlen(pch) == 0) {
            return NULL;
        }
        
        message->argv[i] = pch;
        i++;
        pch = strtok(NULL, DELIMITER);
    }
    
    free(msg_str);
    
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
            strcat(msg_str, DELIMITER);
            strcat(msg_str, msg->argv[i]);
        }
    }
    else if (msg->type == NULL && msg->argc = 0) {
        msg_str = (char *) malloc(sizeof(char));
        msg_str[0] = '\0';
    }
    else {
        return false;
    }
    
    return write_to_socket(msg_str, sock);
}