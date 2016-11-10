/* 
 * Author: Petr Kozler
 * 
 * Modul message.c definuje funkce pro operace se zprávami.
 */

#include "message.h"

#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>

/**
 * Provádí postupné čtení ze socketu, dokud nejsou načteny všechny bajty
 * přijímané zprávy. V případě chyby (např. přerušení spojení) vrací
 * chybovou hodnotu.
 * 
 * @param sock deskriptor socketu příjemce
 * @param buf blok paměti s bajty určenými k zápisu
 * @param len počet bajtů určených k zápisu
 * 
 * @return 1 v případě úspěchu, menší hodnota v případě chyby
 */
int read_bytes(int sock, void *buf, unsigned int len) {
    int bytes_read = 0;
    int read_result = 0;

    while (bytes_read < len) {
        read_result = read(sock, buf + bytes_read, len - bytes_read);
        
        if (read_result < 1) {
            return read_result;
        }

        bytes_read += read_result;
    }
    
    return 1;
}

/**
 * Provádí postupný zápis do socketu, dokud nejsou zapsány všechny bajty
 * odesílané zprávy. V případě chyby (např. přerušení spojení) vrací
 * chybovou hodnotu.
 * 
 * @param sock deskriptor socketu příjemce
 * @param buf blok paměti s bajty určenými k zápisu
 * @param len počet bajtů určených k zápisu
 * 
 * @return 0 v případě úspěchu, menší hodnota v případě chyby
 */
int write_bytes(int sock, void *buf, unsigned int len) {
    int bytes_wrote = 0;
    int write_result = 0;

    while (bytes_wrote < len) {
        write_result = write(sock, buf + bytes_wrote, len - bytes_wrote);

        if (write_result < 0) {
            return write_result;
        }

        bytes_wrote += write_result;
    }
    
    return 0;
}

/**
 * Vytvoří strukturu uchovávající prázdný řetězec zprávy o předané délce
 * a alokuje odpovídající množství paměti pro tento řetězec.
 * 
 * @param length délka zprávy
 * 
 * @return struktura zprávy, pokud vytvoření proběhlo v pořádku, jinak null
 */
message_t *create_message(int32_t length) {
    message_t *message = (message_t *) malloc(sizeof(message_t));
    
    if (message == NULL) {
        return NULL;
    }
    
    message->length = length;
    message->bytes = (char *) calloc(length + 1, sizeof(char));
    
    if (message->bytes == NULL) {
        free(message);
        
        return NULL;
    }
    
    return message;
}

/**
 * Odešle předanou zprávu klientovi s předaným deskriptorem socketu.
 * 
 * @param message struktura zprávy
 * @param sock deskriptor socketu klienta
 * 
 * @return 1, pokud odeslání proběhlo pořádku, jinak záporná hodnota
 */
int send_message(message_t *message, int sock) {
    int32_t str_len = htonl(message->length);

    int write_result = write_bytes(sock, &str_len, sizeof(int32_t));
    
    if (write_result < 0) {
        return write_result;
    }
    
    write_result = write_bytes(sock, message->bytes, message->length);
            
    if (write_result < 0) {
        return write_result;
    }
    
    return 1;
}

/**
 * Přijme zprávu od klienta s předaným deskriptorem socketu.
 * 
 * @param message ukazatel pro uchování struktury přijaté zprávy
 * @param sock deskriptor socketu klienta
 * 
 * @return 1, pokud příjem proběhl v pořádku, jinak menší hodnota
 */
int receive_message(message_t **message, int sock) {
    int32_t str_len;

    int read_result = read_bytes(sock, &str_len, sizeof(int32_t));
    
    if (read_result < 1) {
        *message = NULL;
        
        return read_result;
    }
    
    message_t *received = create_message(ntohl(str_len));
    
    if (received == NULL) {
        *message = NULL;
        
        return 1;
    }
    
    read_result = read_bytes(sock, received->bytes, received->length);
    
    if (read_result < 1) {
        delete_message(received);
        *message = NULL;
        
        return read_result;
    }
    
    *message = received;
    
    return 1;
}

/**
 * Uvolní paměť předané struktury zprávy včetně uchovávaného řetězce.
 * 
 * @param message struktura zprávy
 * 
 * @return true, pokud odstranění proběhlo v pořádku, jinak false
 */
bool delete_message(message_t *message) {
    if (message == NULL) {
        return false;
    }
    
    free(message->bytes);
    free(message);
    
    return true;
}
