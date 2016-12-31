/* 
 * Author: Petr Kozler
 */

#ifndef PLAYER_LIST_H
#define PLAYER_LIST_H

#include <stdint.h>
#include <stdbool.h>

void create_player_list();
void delete_player_list();
void lock_player_list();
void unlock_player_list(bool changed);
void add_player_to_list(player_t *player);
player_t *get_player_by_id(int32_t id);
player_t *get_player_by_name(char *name);
player_t *remove_player_by_id(int32_t id);

#endif /* PLAYER_LIST_H */

