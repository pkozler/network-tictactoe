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
    int8_t index;
    bool win;
} game_cell_t;

typedef struct GAME {
    pthread_t thread;
    pthread_mutex_t lock;
    bool changed;
    // základní informace o herní místnosti odesílané v položkách seznamu her:
    int32_t id;
    char *name;
    int8_t board_size;
    int8_t cell_count;
    int8_t player_count;
    int8_t player_counter;
    int32_t round_counter;
    bool active;
    // detailní informace odesílané pouze hráčům v dané herní místnosti:
    struct PLAYER **players;
    int8_t current_player;
    int8_t winner;
    int16_t occupied_cell_counter;
    game_cell_t **board;
} game_t;

void create_game(struct PLAYER *player, char *name,
        int8_t board_size, int8_t player_count, int8_t cell_count);
void delete_game(game_t *);
void restart_game(game_t *);
void add_player(game_t *, struct PLAYER *);
void remove_player(game_t *, struct PLAYER *);
void play(game_t *, int8_t, int8_t, int8_t);

#endif /* GAME_H */

