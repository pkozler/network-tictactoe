/* 
 * Author: Petr Kozler
 */

#ifndef PLAYER_H
#define PLAYER_H

#include "message.h"
#include "game.h"
#include <pthread.h>
#include <stdbool.h>
#include <stdint.h>

struct GAME;

typedef struct PLAYER {
    pthread_t thread;
    pthread_mutex_t lock;
    int sock;
    bool connected;
    // základní informace o připojeném klientovi odesílané v položkách seznamu hráčů:
    int32_t id;
    char *nick;
    int32_t total_score;
    struct GAME *current_game;
    int8_t current_game_index;
    int32_t current_game_score;
} player_t;

player_t *create_player(int sock);
void delete_player(player_t *player);
void lock_player(player_t *player);
void unlock_player(player_t *player);

#endif /* PLAYER_H */

