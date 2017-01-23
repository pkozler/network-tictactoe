/* 
 * Hlavičkový soubor message_list obsahuje definici struktury seznamu zpráv.
 * 
 * Author: Petr Kozler
 */

#ifndef MESSAGE_LIST_H
#define MESSAGE_LIST_H

#include "message.h"
#include "client_socket.h"
#include <stdint.h>

/**
 * Struktura seznamu zpráv.
 */
typedef struct {
    message_t *head; // zpráva představující hlavičku seznamu
    int32_t msgc; // počet zpráv představujících položky
    message_t **msgv; // zprávy představující položky seznamu
} message_list_t;

message_list_t *create_message_list(message_t *head, int32_t msgc);
void delete_message_list(message_list_t *msg_list);
void send_message_list(message_list_t *messages, client_socket_t *socket);

#endif /* MESSAGE_LIST_H */
