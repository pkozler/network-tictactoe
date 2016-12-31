/* 
 * Author: Petr Kozler
 */

#ifndef GAME_LOGIC_H
#define GAME_LOGIC_H

#include "game.h"

void start_next_round(game_t *game);
void add_player_to_game(game_t *game, player_t *player);
void remove_player_from_game(player_t *player);
void play(game_t *game, int8_t player_pos, int8_t x, int8_t y);

#endif /* GAME_LOGIC_H */
