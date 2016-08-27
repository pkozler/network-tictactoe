/* 
 * Author: Petr Kozler
 */

#include "broadcast.h"
#include "observed_list.h"
#include "player.h"
#include <stdlib.h>

void send_to_all_clients(int32_t msgc, message_t **msgv) {
    list_iterator_t *iterator = create_list_iterator(g_player_list);
    
    player_t *player = (player_t *) get_next_item(iterator);
    
    int32_t i;
    while (player != NULL) {
        if (player->active) {
            for (i = 0; i < msgc; i++) {
                send_message(msgv[i], player->sock);
            }
        }
        
        player = (player_t *) get_next_item(iterator);
    }
    
    free(iterator);
}