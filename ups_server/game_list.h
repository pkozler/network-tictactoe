/* 
 * Hlavičkový soubor game_list deklaruje funkce pro práci se seznamem her.
 * 
 * Author: Petr Kozler
 */

#ifndef GAME_LIST_H
#define GAME_LIST_H

#include <stdint.h>
#include <stdbool.h>
#include "game.h"

void create_game_list();
void delete_game_list();
void lock_game_list();
void unlock_game_list(bool changed);
void add_game_to_list(game_t *game);
game_t *get_game_by_id(int32_t id);
game_t *get_game_by_name(char *name);
game_t *remove_game_by_id(int32_t id);

#endif /* GAME_LIST_H */

