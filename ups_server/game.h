/* 
 * Author: Petr Kozler
 */

#ifndef GAME_H
#define GAME_H

#include "observed_list.h"
#include "message.h"
#include "player.h"
#include <pthread.h>
#include <stdbool.h>
#include <stdint.h>

observed_list_t *g_game_list;

struct PLAYER;

typedef struct {
    int32_t index;
    bool win;
} game_cell_t;

typedef struct GAME {
    pthread_t thread;
    pthread_mutex_t lock;
    int32_t id;
    char *name;
    int8_t board_size;
    int8_t win_row_len;
    int8_t players_size;
    int8_t player_counter;
    bool active;
    bool changed;
    struct PLAYER **players;
    int8_t current_player;
    int8_t winner;
    int16_t cell_counter;
    game_cell_t **board;
} game_t;

void create_game(char *, int8_t, int8_t, int8_t);
void delete_game(game_t *);
void restart_game(game_t *);
int8_t add_player(game_t *, struct PLAYER*);
int8_t remove_player(game_t *, struct PLAYER*);
bool play(game_t *, int8_t, int8_t, int8_t);

#endif /* GAME_H */

