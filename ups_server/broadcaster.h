/* 
 * Author: Petr Kozler
 */

#ifndef BROADCASTER_H
#define BROADCASTER_H

#include "message.h"
#include "message_list.h"
#include "player.h"
#include <stdint.h>

void send_to_single_client(message_list_t *messages, player_t *client);
void send_to_all_clients(message_list_t *messages);
void send_to_selected_clients(message_list_t *messages,
        player_t **clients, int32_t client_count);

#endif /* BROADCASTER_H */
