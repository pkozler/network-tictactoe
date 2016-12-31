/* 
 * Author: Petr Kozler
 */

#include "broadcaster.h"
#include "global.h"
#include "linked_list.h"
#include "linked_list_iterator.h"
#include "player.h"
#include "tcp_communicator.h"
#include <stdlib.h>

void send_messages_to_client(message_list_t *messages, player_t *client) {
    lock_player(client);
    send_message(messages->head, client->sock);

    int32_t i;
    for (i = 0; i < messages->msgc; i++) {
        send_message(messages->msgv[i], client->sock);
    }

    unlock_player(client);
}

void send_to_all_clients(message_list_t *messages) {
    linked_list_iterator_t *iterator = create_iterator(g_client_list);
    
    while (has_next_element(iterator)) {
        player_t *client = (player_t *) get_next_element(iterator);
        send_messages_to_client(messages, client);
    }
    
    delete_message_list(messages);
}

void send_to_selected_clients(message_list_t *messages,
        player_t **clients, int32_t client_count) {
    int32_t i;
    for (i = 0; i < client_count; i++) {
        if (clients[i] != NULL) {
            send_messages_to_client(messages, clients[i]);
        }
    }
    
    delete_message_list(messages);
}
