/* 
 * Hlavičkový soubor player_list deklaruje funkce pro práci se seznamem hráčů.
 * 
 * Author: Petr Kozler
 */

#ifndef PLAYER_LIST_H
#define PLAYER_LIST_H

#include "player.h"
#include <stdint.h>
#include <stdbool.h>

void create_player_list();
void delete_player_list();
void add_player_to_list(player_t *player);
void return_player_to_list(player_t *existing_player, player_t *player);
player_t *get_player_by_id(int32_t id);
player_t *get_player_by_name(char *name);
player_t *remove_player_by_id(int32_t id);
void lock_player_list();
void unlock_player_list();
bool is_player_list_changed();
void set_player_list_changed(bool changed);

#endif /* PLAYER_LIST_H */

