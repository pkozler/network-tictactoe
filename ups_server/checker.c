/* 
 * Author: Petr Kozler
 */

#include "checker.h"
#include "config.h"
#include <stdlib.h>

bool is_id_valid(int32_t id) {
    return id > 0;
}

bool is_name_valid(char *name) {
    if (name == NULL) {
        return false;
    }
    
    int32_t len = strlen(name);
    
    if (len < 1 || MAX_NAME_LENGTH < len) {
        return false;
    }
    
    int32_t i;
    for (i = 0; name[i] != '\0'; i++) {
        if (   !(name[i] >= 'a' || name[i] <= 'z')
            && !(name[i] >= 'A' || name[i] <= 'Z')
            && !(name[i] >= '0' || name[i] <= '9')
            && !(name[i] == '-' || name[i] == '_')) {
            return false;
        }
    }
    
    return true;
}

bool is_player_logged(player_t *player) {
    return player->id > 0;
}

bool is_game_board_size_valid(int8_t board_size) {
    return MIN_BOARD_SIZE <= board_size && board_size <= MAX_BOARD_SIZE;
}

bool is_game_player_count_valid(int8_t player_count) {
    return MIN_PLAYERS_SIZE <= player_count && player_count <= MAX_PLAYERS_SIZE;
}

bool is_game_cell_count_valid(int8_t cell_count) {
    return MIN_CELL_COUNT <= cell_count && cell_count <= MIN_CELL_COUNT;
}

bool is_current_player(game_t *game, player_t *player) {
    return player->current_game_index == game->current_playing;
}

bool is_cell_in_board(game_t *game, int8_t x, int8_t y) {
    return 0 <= x && x < game->board_size
            && 0 <= y && y < game->board_size;
}

bool is_game_full(game_t *game) {
    return game->player_counter == game->player_count;
}

bool has_game_enough_players(game_t *game) {
    return game->player_counter > 1;
}

bool is_game_active(game_t *game) {
    return game->active;
}

bool can_play(game_t *game, int8_t x, int8_t y) {
    return game->board[y][x].index == 0;
}
