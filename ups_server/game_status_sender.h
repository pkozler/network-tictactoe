/* 
 * Hlavičkový soubor game_status_sender deklaruje funkce pro odesílání
 * stavu hry.
 * 
 * Author: Petr Kozler
 */

#ifndef GAME_STATUS_SENDER_H
#define GAME_STATUS_SENDER_H

#include "game.h"

void broadcast_game_status(game_t *game);

#endif /* GAME_STATUS_SENDER_H */
