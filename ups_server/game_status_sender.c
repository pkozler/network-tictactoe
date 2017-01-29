/* 
 * Modul game_status_sender definuje funkce pro odesílání stavu hry.
 * 
 * Author: Petr Kozler
 */

#include "game_status_sender.h"
#include "protocol.h"
#include "broadcaster.h"
#include "communicator.h"
#include "message.h"
#include "message_list.h"
#include "config.h"
#include "string_utils.h"
#include <string.h>
#include <stdlib.h>
#include <stdio.h>

/**
 * Sestaví řetězec zprávy představující políčka herního pole.
 * 
 * @param game hra
 * @return řetězec políček herního pole
 */
char *build_board_string(game_t *game) {
    int32_t board_str_len = ((int32_t) game->board_size) * ((int32_t) game->board_size) * BOARD_CELL_SEED_SIZE + 1;

    char *board_str = (char *) malloc(sizeof(char) * board_str_len);
    board_str[0] = '\0';
    char buf[BOARD_CELL_SEED_SIZE + 1];
    
    int32_t i, j;
    for (i = 0; i < game->board_size; i++) {
        for (j = 0; j < game->board_size; j++) {
            int8_t cell = game->board[i][j];
            snprintf(buf, BOARD_CELL_SEED_SIZE + 1, "%hhd", cell);
            strncat(board_str, buf, BOARD_CELL_SEED_SIZE + 1);
        }
    }
    
    return board_str;
}

/**
 * Sestaví zprávu představující stav herní místnosti.
 * 
 * @param game hra
 * @return stav herní místnosti
 */
message_t *game_board_to_message(game_t *game) {
    message_t *new_message = create_message(MSG_GAME_DETAIL, MSG_GAME_DETAIL_ARGC);
    
    put_byte_arg(new_message, game->player_counter);
    put_byte_arg(new_message, game->board_size);
    put_int_arg(new_message, game->current_round);
    put_bool_arg(new_message, game->round_finished);
    put_byte_arg(new_message, game->current_player_on_turn);
    put_byte_arg(new_message, game->last_player_on_turn);
    put_byte_arg(new_message, game->last_cell_x);
    put_byte_arg(new_message, game->last_cell_y);
    put_byte_arg(new_message, game->current_winner);
    
    if (game->current_winner < 1) {
        put_byte_arg(new_message, 0);
        put_byte_arg(new_message, 0);
        put_byte_arg(new_message, 0);
        put_byte_arg(new_message, 0);
    }
    else {
        put_byte_arg(new_message, game->winner_cells_x[0]);
        put_byte_arg(new_message, game->winner_cells_y[0]);
        put_byte_arg(new_message, game->winner_cells_x[game->cell_count - 1]);
        put_byte_arg(new_message, game->winner_cells_y[game->cell_count - 1]);
    }
    
    put_string_arg(new_message, build_board_string(game));
    
    return new_message;
}

/**
 * Sestaví zprávu představující položku seznamu hráčů v herní místnosti.
 * 
 * @param player hráč
 * @return položka seznamu hráčů v herní místnosti
 */
message_t *joined_player_to_message(player_t *player) {
    message_t *new_message = create_message(MSG_GAME_PLAYER, MSG_GAME_PLAYER_ARGC);
    put_int_arg(new_message, player->id);
    put_string_arg(new_message, player->nick);
    put_byte_arg(new_message, player->current_game_index);
    put_int_arg(new_message, player->current_game_score);
    
    return new_message;
}

/**
 * Vytvoří zprávy o stavu herní místnosti a hráčích, kteří se v ní nacházejí.
 * 
 * @param game hra
 * @return seznam zpráv o stavu hry a hráčích
 */
message_list_t *game_status_to_message_list(game_t *game) {
    message_t *header = game_board_to_message(game);
    message_list_t *message_list = create_message_list(
            build_message_string(header), game->player_counter);
    delete_message(header);
    
    int32_t i = 0;
    int32_t j;
    for (j = 0; j < game->player_count; j++) {
        if (game->players[j] != NULL) {
            message_t *item = joined_player_to_message(game->players[j]);
            message_list->msgv[i] = build_message_string(item);
            delete_message(item);
            i++;
        }
    }
    
    return message_list;
}

/**
 * Rozešle stav hry.
 * 
 * @param game hra
 */
void broadcast_game_status(game_t *game) {
    if (is_game_changed(game)) {
        lock_game(game);
        message_list_t *messages = game_status_to_message_list(game);
        set_game_changed(game, false);
        unlock_game(game);
        send_message_list_to_selected(messages, game->players, game->player_count);
    }
}
