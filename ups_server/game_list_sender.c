/* 
 * Modul game_list_sender definuje funkce pro odesílání seznamu her.
 * 
 * Author: Petr Kozler
 */

#include "game_list_sender.h"
#include "global.h"
#include "protocol.h"
#include "broadcaster.h"
#include "message_list.h"
#include "linked_list_iterator.h"

/**
 * Sestaví zprávu představující hlavičku seznamu her.
 * 
 * @return hlavička seznamu her
 */
message_t *game_list_to_message() {
    message_t *message = create_message(MSG_GAME_LIST, MSG_GAME_LIST_ARGC);
    put_int_arg(message, count_elements(g_game_list->list));
    
    return message;
}

/**
 * Sestaví zprávu představující položku seznamu her.
 * 
 * @param game hra
 * @return položka seznamu her
 */
message_t *game_to_message(game_t *game) {
    message_t *message = create_message(MSG_GAME_LIST_ITEM, MSG_GAME_LIST_ITEM_ARGC);
    put_int_arg(message, game->id);
    put_string_arg(message, game->name);
    put_byte_arg(message, game->player_count);
    put_byte_arg(message, game->board_size);
    put_byte_arg(message, game->cell_count);
    put_byte_arg(message, game->player_counter);
    
    return message;
}

/**
 * Vytvoří zprávy představující seznam her.
 * 
 * @return seznam her
 */
message_list_t *game_list_to_message_list() {
    message_list_t *message_list = create_message_list(
            game_list_to_message(), count_elements(g_game_list->list));
    linked_list_iterator_t *iterator = create_iterator(g_game_list->list);
    game_t *current_game;
    
    int32_t i = 0;
    while (has_next_element(iterator)) {
        current_game = get_next_element(iterator);
        message_list->msgv[i++] = game_to_message(current_game);
    }
    
    return message_list;
}

/**
 * Rozešle seznam her.
 */
void broadcast_game_list() {
    send_message_list_to_all(game_list_to_message_list());
}
