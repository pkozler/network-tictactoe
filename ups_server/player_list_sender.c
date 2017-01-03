/* 
 * Author: Petr Kozler
 */

#include "player_list_sender.h"
#include "global.h"
#include "protocol.h"
#include "message_list.h"
#include "linked_list_iterator.h"

message_t *player_list_to_message() {
    message_t *message = create_message(MSG_PLAYER_LIST, MSG_PLAYER_LIST_ARGC);
    put_int_arg(message, count_elements(g_player_list->list));
    
    return message;
}

message_t *player_to_message(player_t *player) {
    message_t *message = create_message(MSG_PLAYER_LIST_ITEM, MSG_PLAYER_LIST_ITEM_ARGC);
    put_int_arg(message, player->id);
    put_string_arg(message, player->nick);
    
    return message;
}

message_list_t *player_list_to_message_list() {
    message_list_t *message_list = create_message_list(
            player_list_to_message(), count_elements(g_player_list));
    linked_list_iterator_t *iterator = create_iterator(g_player_list);
    player_t *current_player;
    
    int32_t i = 0;
    while (has_next_element(iterator)) {
        current_player = get_next_element(iterator);
        message_list->msgv[i++] = player_to_message(current_player);
    }
    
    return message_list;
}

void broadcast_player_list() {
    send_message_list_to_all(player_list_to_message_list());
}
