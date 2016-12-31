/* 
 * Author: Petr Kozler
 */

#include "game_status_sender.h"
#include "protocol.h"
#include "broadcaster.h"

message_t *game_board_to_message(game_t *game) {
    message_t *new_message = create_message(MSG_GAME_DETAIL, MSG_GAME_DETAIL_ARGC);
    
    put_byte_arg(new_message, game->winner);
    put_byte_arg(new_message, game->current_player);
    
    char *board_str = (char *) malloc(sizeof(char) *
        (game->board_size * game->board_size * (BOARD_CELL_SEED_SIZE + 1) + 1));
    board_str[0] = '\0';
    char buf[BOARD_CELL_SEED_SIZE + 1];
    
    int32_t i, j;
    for (j = 0; j < game->board_size; j++) {
        for (i = 0; i < game->board_size; i++) {
            game_cell_t cell = game->board[j][i];
            snprintf(buf, BOARD_CELL_SEED_SIZE + 1, "%d", cell.index);
            strcat(board_str, buf);
            strcat(board_str, cell.win ? WINNING_CELL_SYMBOL : NORMAL_CELL_SYMBOL);
        }
    }
    
    return new_message;
}

message_t *joined_player_to_message(player_t *player) {
    message_t *new_message = create_message(MSG_GAME_PLAYER, MSG_GAME_PLAYER_ARGC);
    put_int_arg(new_message, player->id);
    put_int_arg(new_message, player->current_game_index);
    put_int_arg(new_message, player->current_game_score);
    
    return new_message;
}

void send_game_status(game_t *game) {
    message_list_t *messages = create_message_list(
            game_board_to_message(game), game->player_counter);
    
    int32_t i = 0;
    int32_t j;
    for (j = 0; j < game->player_count; j++) {
        if (game->players[j] != NULL) {
            messages->msgv[i] = joined_player_to_message(game->players[j]);
            i++;
        }
    }
    
    send_to_selected_clients(messages, game->players, game->player_count);
}
