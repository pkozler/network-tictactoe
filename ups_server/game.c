/* 
 * Modul game definuje funkce pro vytvoření a odstranění hry
 * a pro pozorování stavu hry.
 * 
 * Author: Petr Kozler
 */

#include "game.h"
#include "game_logic.h"
#include "game_status_sender.h"
#include "global.h"
#include "config.h"
#include "protocol.h"
#include "logger.h"
#include "message.h"
#include "game_list.h"
#include "player_list.h"
#include "status_cleaner.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

/**
 * Pozoruje stav herní místnosti a v případě změny rozešle zprávu klientům.
 * 
 * @param arg herní místnost
 * @return null
 */
void *run_game(void *arg) {
    game_t *game = (game_t *) arg;
    
    while (game->played) {
        broadcast_game_status(game);
    }
    
    handle_empty_game(game);
    
    return NULL;
}

/**
 * Vytvoří herní místnost.
 * 
 * @param player hráč, který vyslal požadavek na vytvoření herní místnosti
 * @param name název místnosti
 * @param board_size rozměr hracího pole
 * @param player_count počet hráčů
 * @param cell_count počet políček k obsazení
 * @return herní místnost
 */
game_t *create_game(player_t *player, char *name,
        int8_t board_size, int8_t player_count, int8_t cell_count) {
    game_t *game = calloc(1, sizeof(game_t));
    pthread_mutex_init(&(game->lock), NULL);

    game->name = name;
    game->player_count = player_count;
    game->board_size = board_size;
    game->cell_count = cell_count;

    game->players = calloc(player_count, sizeof(player_t *));
    game->winner_cells_x = calloc(game->cell_count, sizeof(int8_t));
    game->winner_cells_y = calloc(game->cell_count, sizeof(int8_t));
    game->board = calloc(board_size, sizeof(int8_t *));

    int8_t i = 0;
    for (i = 0; i < game->board_size; i++) {
        game->board[i] = calloc(board_size, sizeof(int8_t));
    }
    
    game->occupied_cell_counter = 0;
    game->current_player_on_turn = 0;
    game->last_player_on_turn = 0;
    game->last_cell_x = 0;
    game->last_cell_y = 0;
    game->current_winner = 0;
    game->current_round = 0;
    game->round_finished = true;
    
    game->played = true;
    game->changed = false;
    
    if (pthread_create(&(game->thread), NULL, run_game, game) < 0) {
        print_err("Chyba při spouštění vlákna pro rozesílání stavu hry");
    }
    
    return game;
}

/**
 * Odstraní herní místnost.
 * 
 * @param game herní místnost
 */
void delete_game(game_t *game) {
    int8_t i = 0;
    for (i = 0; i < game->board_size; i++) {
        free(game->board[i]);
    }

    free(game->board);
    free(game->players);
    free(game->winner_cells_x);
    free(game->winner_cells_y);
    pthread_cancel(game->thread);
    pthread_mutex_destroy(&(game->lock));
    
    free(game);
}

/**
 * Uzamkne herní místnost.
 * 
 * @param game herní místnost
 */
void lock_game(game_t *game) {
    pthread_mutex_lock(&(game->lock));
}

/**
 * Odemkne herní místnost.
 * 
 * @param game herní místnost
 */
void unlock_game(game_t *game) {
    pthread_mutex_unlock(&(game->lock));
}

/**
 * Vrátí příznak změny v herní místnosti.
 * 
 * @return příznak změny
 */
bool is_game_changed(game_t *game) {
    return game->changed;
}

/**
 * Nastaví příznak změny v herní místnosti.
 * 
 * @return příznak změny
 */
void set_game_changed(game_t *game, bool changed) {
    game->changed = changed;
}