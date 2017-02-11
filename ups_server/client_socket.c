/* 
 * Modul client_socket definuje funkce pro uzamykání a odemykání
 * přístupu k socketu klienta.
 * 
 * Author: Petr Kozler
 */

#include "client_socket.h"

/**
 * Uzamkne socket klienta.
 * 
 * @param socket socket klienta
 */
void lock_socket(client_socket_t *socket) {
    pthread_mutex_lock(&(socket->lock));
}

/**
 * Odemkne socket klienta.
 * 
 * @param socket socket klienta
 */
void unlock_socket(client_socket_t *socket) {
    pthread_mutex_unlock(&(socket->lock));
}
