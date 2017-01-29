/* 
 * Modul player_list_sender definuje funkce pro odesílání seznamu hráčů.
 * 
 * Author: Petr Kozler
 */

#include "player_list_sender.h"
#include "global.h"
#include "protocol.h"
#include "player.h"
#include "broadcaster.h"
#include "message_list.h"
#include "linked_list_iterator.h"
#include "player_list.h"

/**
 * Sestaví zprávu představující hlavičku seznamu hráčů.
 * 
 * @return hlavička seznamu hráčů
 */
message_t *player_list_to_message() {
    message_t *message = create_message(MSG_PLAYER_LIST, MSG_PLAYER_LIST_ARGC);
    put_int_arg(message, count_elements(g_player_list->list));
    
    return message;
}

/**
 * Sestaví zprávu představující položku seznamu hráčů.
 * 
 * @param player hráč
 * @return položka seznamu hráčů
 */
message_t *player_to_message(player_t *player) {
    message_t *message = create_message(MSG_PLAYER_LIST_ITEM, MSG_PLAYER_LIST_ITEM_ARGC);
    put_int_arg(message, player->id);
    put_string_arg(message, player->nick);
    put_int_arg(message, player->total_score);
    put_bool_arg(message, player->socket->connected);
    
    return message;
}

/**
 * Vytvoří zprávy představující seznam hráčů.
 * 
 * @return seznam hráčů
 */
message_list_t *player_list_to_message_list() {
    message_list_t *message_list = create_message_list(
            player_list_to_message(), count_elements(g_player_list->list));
    linked_list_iterator_t *iterator = create_iterator(g_player_list->list);
    player_t *current_player;
    
    int32_t i = 0;
    while (has_next_element(iterator)) {
        current_player = get_next_element(iterator);
        message_list->msgv[i++] = player_to_message(current_player);
    }
    
    return message_list;
}

/**
 * Rozešle seznam hráčů.
 */
void broadcast_player_list() {
    if (is_player_list_changed()) {
        lock_player_list();
        message_list_t *messages = player_list_to_message_list();
        set_player_list_changed(false);
        unlock_player_list();
        send_message_list_to_all(messages);
    }
}
