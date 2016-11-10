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
    int sock;
    bool active;
    // základní informace o připojeném klientovi odesílané v položkách seznamu hráčů:
    int32_t id;
    char *nick;
    int32_t total_score;
    struct GAME *current_game;
    int8_t current_game_index;
    int32_t current_game_score;
    // statistiky přenosů klienta používané pro detekci narušení spojení
    int32_t timeout_counter;
    int32_t valid_transfers_count;
    int32_t invalid_transfers_count;
    int32_t update_nack_count;
    int32_t high_frequency_message_count;
} player_t;

void lock_player(player_t *player);
void unlock_player(player_t *player);
void create_player(int sock);
void delete_player(player_t *player);

#endif /* PLAYER_H */

