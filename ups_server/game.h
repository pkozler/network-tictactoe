/* 
 * Author: Petr Kozler
 */

#ifndef GAME_H
#define GAME_H

#include "message.h"
#include "player.h"
#include <pthread.h>
#include <stdbool.h>
#include <stdint.h>

struct PLAYER;

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
    // detailní informace odesílané pouze hráčům v dané herní místnosti:
    struct PLAYER **players;
    int32_t current_round;
    bool round_finished;
    int8_t current_playing;
    int8_t last_playing;
    int8_t last_cell_x;
    int8_t last_cell_y;
    int8_t last_outgoing;
    int8_t last_disconnected;
    int8_t current_winner;
    int8_t *winner_cells_x;
    int8_t *winner_cells_y;
    int8_t **board;
    // neodesílané informace o herní místnosti:
    int16_t occupied_cell_counter;
} game_t;

game_t *create_game(struct PLAYER *player, char *name,
        int8_t board_size, int8_t player_count, int8_t cell_count);
void delete_game(game_t *);
void lock_game(game_t *game);
void unlock_game(game_t *game, bool changed);

#endif /* GAME_H */
