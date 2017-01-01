/* 
 * Author: Petr Kozler
 */

#include "game.h"
#include "global.h"
#include "protocol.h"
#include "printer.h"
#include "message.h"
#include "config.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

game_t *create_game(player_t *player, char *name,
        int8_t board_size, int8_t player_count, int8_t cell_count) {
    game_t *game = calloc(sizeof(game_t), 1);
    pthread_mutex_init(&(game->lock), NULL);

    game->player_count = player_count;
    game->board_size = board_size;
    game->cell_count = cell_count;

    game->players = calloc(sizeof(player_t *), player_count);
    game->winner_cells_x = calloc(sizeof(int8_t) * game->cell_count);
    game->winner_cells_y = calloc(sizeof(int8_t) * game->cell_count);
    game->board = calloc(sizeof(int8_t), board_size);

    int8_t i = 0;
    for (i = 0; i < game->board_size; i++) {
        game->board[i] = calloc(sizeof(int8_t), board_size);
    }
    
    // TODO dokončit
    
    game->current_playing = 1;
    game->player_counter = 0;
    game->current_round = 0;
    game->occupied_cell_counter = 0;
    game->current_winner = 0;
    game->round_finished = true;
    game->changed = true;
    add_player_to_game(game, player);
    
    if (pthread_create(&(game->thread), NULL, run_game, game) < 0) {
        print_err("Chyba při spouštění vlákna pro rozesílání stavu hry");
    }
    
    return game;
}

void delete_game(game_t *game) {
    int8_t i = 0;
    for (i = 0; i < game->board_size; i++) {
        free(game->board[i]);
    }

    free(game->board);
    pthread_cancel(game->thread);
    pthread_mutex_destroy(&(game->lock));
    
    free(game);
}

void lock_game(game_t *game) {
    pthread_mutex_lock(&(game->lock));
}

void unlock_game(game_t *game, bool changed) {
    game->changed = changed;
    pthread_mutex_unlock(&(game->lock));
}

void *run_game(void *arg) {
    game_t *game = (game_t *) arg;
    
    while (game->player_counter > 0) {
        if (game->changed) {
            lock_game(game);
            send_game_status(game);
            unlock_game(game, false);
        }
    }
    
    remove_game_by_id(game->id);
    delete_game(game);
    
    return NULL;
}
