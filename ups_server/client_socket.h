/* 
 * Hlavičkový soubor client_socket definuje strukturu uchovávající informace
 * o připojení klienta, a dále deklaruje funkce pro uzamykání a odemykání
 * přístupu k socketu klienta.
 * 
 * Author: Petr Kozler
 */

#ifndef CLIENT_SOCKET_H
#define CLIENT_SOCKET_H

#include <stdbool.h>
#include <pthread.h>

/**
 * Struktura pro uchování informací o připojení klienta.
 */
typedef struct {
    pthread_mutex_t lock; // zámek klienta pro socket
    int sock; // deskriptor socketu klienta
    bool connected; // příznak připojení klienta
} client_socket_t;

void lock_socket(client_socket_t *socket);
void unlock_socket(client_socket_t *socket);

#endif /* CLIENT_SOCKET_H */
