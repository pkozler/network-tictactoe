/* 
 * Author: Petr Kozler
 * 
 * Hlavičkový soubor client.h definuje strukturu s daty klienta a obsahuje
 * deklarace funkcí pro zpracování jeho požadavků.
 */

#ifndef CLIENT_H
#define CLIENT_H

#include <stdbool.h>
#include <stdint.h>
#include <pthread.h>

/*
 * Struktura s daty klienta.
 */
typedef struct {
    int32_t id; // ID
    char *login; // login
    int32_t messages_left; // čítač požadavků
    bool connected; // příznak aktivního klienta
    int sock; // deskriptor socketu
    pthread_t thread; // obslužné vlákno
} client_t;

client_t *create_client(int32_t id, int sock);

void *run_client(void *arg);

void delete_client(client_t *client);

#endif /* CLIENT_H */

