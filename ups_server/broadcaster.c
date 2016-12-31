/* 
 * Author: Petr Kozler
 */

#include "broadcaster.h"
#include "global.h"
#include "linked_list.h"
#include "player.h"
#include "message.h"
#include <stdlib.h>

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

void send_messages(void *p, void *msgs) {
    player_t *player = (player_t *) p;
    message_list_t *messages = (message_list_t *msgs);
    
    lock_player(player);

    if (player->active) {
        send_message(messages->head, player->sock);
        
        int32_t i;
        for (i = 0; i < messages->msgc; i++) {
            send_message(messages->msgv[i], player->sock);
        }
    }

    unlock_player(player);
}

void send_to_all_clients(message_list_t *messages) {
    do_foreach_element(g_client_list, send_messages, messages);
    delete_message_list(messages);
}
