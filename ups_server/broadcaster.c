/* 
 * Modul broadcaster definuje funkce pro rozesílání zpráv většímu počtu
 * klientů najednou.
 * 
 * Author: Petr Kozler
 */

#include "broadcaster.h"
#include "global.h"
#include "communicator.h"
#include "linked_list.h"
#include "linked_list_iterator.h"
#include <stdlib.h>

/**
 * Rozešle seznam zpráv všem připojeným klientům.
 * 
 * @param messages seznam zpráv
 */
void send_message_list_to_all(message_list_t *messages) {
    linked_list_iterator_t *iterator = create_iterator(g_client_list);
    
    while (has_next_element(iterator)) {
        player_t *client = (player_t *) get_next_element(iterator);
        send_message_list(messages, client->socket);
    }
    
    delete_message_list(messages);
}

/**
 * Rozešle seznam zpráv vybraným klientům.
 * 
 * @param messages seznam zpráv
 * @param clients pole klientů
 * @param client_count počet klientů
 */
void send_message_list_to_selected(message_list_t *messages,
        player_t **clients, int32_t client_count) {
    int32_t i;
    for (i = 0; i < client_count; i++) {
        if (clients[i] != NULL) {
            send_message_list(messages, clients[i]->socket);
        }
    }
    
    delete_message_list(messages);
}
