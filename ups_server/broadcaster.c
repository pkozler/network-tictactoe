/* 
 * Author: Petr Kozler
 */

#include "broadcaster.h"
#include "global.h"
#include "linked_list.h"
#include "linked_list_iterator.h"
#include <stdlib.h>

void send_message_to_all(message_t *message) {
    linked_list_iterator_t *iterator = create_iterator(g_client_list);
    
    while (has_next_element(iterator)) {
        player_t *client = (player_t *) get_next_element(iterator);
        send_message(message, client->sock);
    }
    
    delete_message(message);
}

void send_message_to_selected(message_t *message,
        player_t **clients, int32_t client_count) {
    int32_t i;
    for (i = 0; i < client_count; i++) {
        if (clients[i] != NULL) {
            send_message(message, clients[i]->sock);
        }
    }
    
    delete_message(message);
}

void send_message_list_to_all(message_list_t *messages) {
    linked_list_iterator_t *iterator = create_iterator(g_client_list);
    
    while (has_next_element(iterator)) {
        player_t *client = (player_t *) get_next_element(iterator);
        send_message_list(messages, client);
    }
    
    delete_message_list(messages);
}

void send_message_list_to_selected(message_list_t *messages,
        player_t **clients, int32_t client_count) {
    int32_t i;
    for (i = 0; i < client_count; i++) {
        if (clients[i] != NULL) {
            send_message_list(messages, clients[i]);
        }
    }
    
    delete_message_list(messages);
}
