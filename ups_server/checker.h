/* 
 * Hlavičkový soubor checker deklaruje funkce pro validaci požadavků klienta.
 * 
 * Author: Petr Kozler
 */

#ifndef CHECKER_H
#define CHECKER_H

#include "player.h"
#include "game.h"
#include <stdint.h>
#include <stdbool.h>

bool is_id_valid(int32_t id);
bool is_name_valid(char *name);
bool is_player_logged(player_t *player);
bool is_game_board_size_valid(int8_t board_size);
bool is_game_player_count_valid(int8_t player_count);
bool is_game_cell_count_valid(int8_t cell_count);
bool is_current_player(game_t *game, player_t *player);
bool is_cell_in_board(game_t *game, int8_t x, int8_t y);
bool is_game_full(game_t *game);
bool has_game_enough_players(game_t *game);
bool is_round_started(game_t *game);
bool can_play(game_t *game, int8_t x, int8_t y);

#endif /* CHECKER_H */
