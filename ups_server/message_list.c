/* 
 * Author: Petr Kozler
 */

#include "message_list.h"

message_list_t *create_message_list(message_t *head, int32_t msgc) {
    message_list_t *msg_list = (message_list_t *) malloc(sizeof(message_list_t));
    msg_list->head = head;
    msg_list->msgc = msgc;
    msg_list->msgv = (message_t **) malloc(sizeof(message_t *) * msgc);
    
    return msg_list;
}

void delete_message_list(message_list_t *msg_list) {
    free(msg_list->head);
    
    int32_t i;
    for (i = 0; i < msg_list->msgc; i++) {
        free(msg_list->msgv[i]);
    }
    
    if (msg_list->msgc > 0) {
        free(msg_list->msgv);
    }
    
    free(msg_list);
}

void send_message_list(message_list_t *messages, player_t *client) {
    lock_player(client);
    send_message(messages->head, client->sock);

    int32_t i;
    for (i = 0; i < messages->msgc; i++) {
        send_message(messages->msgv[i], client->sock);
    }

    unlock_player(client);
}
