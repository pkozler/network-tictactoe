/* 
 * Author: Petr Kozler
 */

#ifndef STATUS_CLEANER_H
#define STATUS_CLEANER_H

#include "player.h"
#include "game.h"

void handle_disconnected_player(player_t *player);
void handle_empty_game(game_t *game);

#endif /* STATUS_CLEANER_H */

