/* 
 * Author: Petr Kozler
 */

#include "game_status_sender.h"
#include "protocol.h"
#include "broadcaster.h"
#include "message.h"
#include "message_list.h"
#include "config.h"
#include "string_utils.h"

char *build_winner_cells_string(int8_t cell_count, int8_t current_winner, int8_t *winner_cells) {
    int32_t cells_str_len = (cell_count * (BYTE_BUF_LEN + 1)) + 1;
    char *winner_cells_str = (char *) malloc(sizeof(char) * cells_str_len);
    winner_cells_str[0] = '\0';
    
    if (current_winner < 1) {
        return winner_cells_str;
    }
    
    int32_t i;
    int8_t cell = winner_cells[i];
    char *cell_str = int_to_string(cell);
    strncat(winner_cells_str, cell_str, BYTE_BUF_LEN);
    free(cell_str);
    
    for (i = 1; i < cell_count; i++) {
        cell = winner_cells[i];
        cell_str = int_to_string(cell);
        strncat(winner_cells_str, ",", 1);
        strncat(winner_cells_str, cell_str, BYTE_BUF_LEN);
        free(cell_str);
    }
    
    return winner_cells_str;
}

char *build_board_string(game_t *game) {
    int32_t board_str_len = game->board_size * game->board_size * BOARD_CELL_SEED_SIZE + 1;
    char *board_str = (char *) malloc(sizeof(char) * board_str_len);
    board_str[0] = '\0';
    char buf[BOARD_CELL_SEED_SIZE + 1];
    
    int32_t i, j;
    for (j = 0; j < game->board_size; j++) {
        for (i = 0; i < game->board_size; i++) {
            int8_t cell = game->board[j][i];
            snprintf(buf, BOARD_CELL_SEED_SIZE, "%d", cell);
            strncat(board_str, buf, BOARD_CELL_SEED_SIZE);
        }
    }
    
    return board_str;
}

message_t *game_board_to_message(game_t *game) {
    message_t *new_message = create_message(MSG_GAME_DETAIL, MSG_GAME_DETAIL_ARGC);
    
    put_int_arg(new_message, game->current_round);
    put_bool_arg(new_message, game->round_finished);
    put_byte_arg(new_message, game->current_playing);
    put_byte_arg(new_message, game->last_playing);
    put_byte_arg(new_message, game->last_cell_x);
    put_byte_arg(new_message, game->last_cell_y);
    put_byte_arg(new_message, game->last_leaving);
    put_byte_arg(new_message, game->current_winner);
    put_string_arg(new_message, build_winner_cells_string(
            game->cell_count, game->current_winner, game->winner_cells_x));
    put_string_arg(new_message, build_winner_cells_string(
            game->cell_count, game->current_winner, game->winner_cells_y));
    put_string_arg(new_message, game->board);
    
    return new_message;
}

message_t *joined_player_to_message(player_t *player) {
    message_t *new_message = create_message(MSG_GAME_PLAYER, MSG_GAME_PLAYER_ARGC);
    put_int_arg(new_message, player->id);
    put_int_arg(new_message, player->current_game_index);
    put_int_arg(new_message, player->current_game_score);
    
    return new_message;
}

message_list_t *game_status_to_message_list(game_t *game) {
    message_list_t *message_list = create_message_list(
            game_board_to_message(game), game->player_counter);
    
    int32_t i = 0;
    int32_t j;
    for (j = 0; j < game->player_count; j++) {
        if (game->players[j] != NULL) {
            message_list->msgv[i] = joined_player_to_message(game->players[j]);
            i++;
        }
    }
    
    return message_list;
}

void broadcast_game_status(game_t *game) {
    send_message_list_to_selected(game_status_to_message_list(game),
            game->players, game->player_count);
}
