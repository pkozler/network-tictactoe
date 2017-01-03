/* 
 * Author: Petr Kozler
 */

#ifndef MESSAGE_LIST_H
#define MESSAGE_LIST_H

#include "message.h"
#include <stdint.h>

typedef struct {
    message_t *head;
    int32_t msgc;
    message_t **msgv;
} message_list_t;

message_list_t *create_message_list(message_t *head, int32_t msgc);
void delete_message_list(message_list_t *msg_list);
void send_message_list(message_list_t *messages, player_t *client);

#endif /* MESSAGE_LIST_H */
