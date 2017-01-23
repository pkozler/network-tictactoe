/* 
 * Author: Petr Kozler
 */

#ifndef CLIENT_SOCKET_H
#define CLIENT_SOCKET_H

#include <stdbool.h>
#include <pthread.h>

typedef struct {
    pthread_mutex_t lock; // zámek klienta pro socket
    int sock; // deskriptor socketu klienta
    bool connected; // příznak připojení klienta
} client_socket_t;

void lock_socket(client_socket_t *socket);
void unlock_socket(client_socket_t *socket);

#endif /* CLIENT_SOCKET_H */

