/* 
 * Author: Petr Kozler
 */

#ifndef PLAYER_H
#define PLAYER_H

#include "observed_list.h"
#include "message.h"
#include "game.h"
#include <pthread.h>
#include <stdbool.h>
#include <stdint.h>

observed_list_t *g_player_list;

struct GAME;

typedef struct PLAYER {
    pthread_t thread;
    pthread_mutex_t lock;
    bool active;
    int sock;
    int32_t id;
    char *nick;
    struct GAME *current_game;
    int32_t index;
} player_t;

void create_player(int sock);
void delete_player(player_t *player);

#endif /* PLAYER_H */

