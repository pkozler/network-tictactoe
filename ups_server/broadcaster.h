/* 
 * Author: Petr Kozler
 */

#ifndef BROADCASTER_H
#define BROADCASTER_H

#include "message.h"
#include <stdint.h>

typedef struct {
    message_t *head;
    int32_t msgc;
    message_t **msgv;
} message_list_t;

message_list_t *create_message_list(message_t *head, int32_t msgc);
void delete_message_list(message_list_t *msg_list);
bool put_message_into_list(message_list_t *msg_list, message_t *msg);
void send_to_all_clients(message_list_t *messages);

#endif /* BROADCASTER_H */

