/* 
 * Modul game_logic obsahuje funkce představující herní logiku.
 * 
 * Author: Petr Kozler
 */

#include "game_logic.h"
#include "player_list.h"
#include "game_list.h"
#include <stdlib.h>
#include <stdbool.h>

/**
 * Zahájí další kolo hry.
 * 
 * @param game hra
 */
void start_next_round(game_t *game) {
    int8_t i, j;
    for (i = 0; i < game->player_count; i++) {
        game->players[i]->playing = true;
    }
    
    for (i = 0; i < game->cell_count; i++) {
        game->winner_cells_x[i] = 0;
        game->winner_cells_y[i] = 0;
    }
    
    for (i = 0; i < game->board_size; i++) {
        for (j = 0; j < game->board_size; j++) {
            game->board[i][j] = 0;
        }
    }
    
    game->current_round++;
    game->round_finished = false;
    game->current_playing = 1;
    game->last_playing = 0;
    game->last_cell_x = 0;
    game->last_cell_y = 0;
    game->current_winner = 0;
    
    game->changed = true;
}

/**
 * Přepne na dalšího hráče, který je na řadě.
 * 
 * @param game hra
 */
void set_next_player(game_t *game) {
    int8_t i = game->current_playing - 1;

    do {
        i++;
        i %= game->player_count;
    }
    while (game->players[i] == NULL || !(game->players[i]->playing));

    game->current_playing = i + 1;
}

/**
 * Přidá hráče do místnosti.
 * 
 * @param game hra
 * @param player hráč
 */
void add_player_to_game(game_t *game, player_t *player) {
    int8_t i;
    for (i = 0; i < game->player_count; i++) {
        if (game->players[i] == NULL) {
            player->current_game = game;
            player->current_game_index = i + 1;
            player->playing = false;
            game->players[i] = player;
            game->player_counter++;
            
            break;
        }
    }
    
    game->changed = true;
}

/**
 * Odebere hráče z místnosti.
 * 
 * @param player hráč
 */
void remove_player_from_game(player_t *player) {
    game_t *game = player->current_game;
    
    if (game == NULL) {
        return;
    }
       
    int8_t i;
    for (i = 0; i < game->player_count; i++) {
        if (game->players[i] != player) {
            continue;
        }
        
        if (game->current_playing == player->current_game_index) {
            set_next_player(game);
        }

        player->playing = false;
        player->current_game_index = 0;
        player->current_game = NULL;
        player->current_game_score = 0;
        game->players[i] = NULL;
        game->player_counter--;
        
        game->changed = true;
    }
}

/**
 * Zkontroluje, zda byl obsazen dostatečný počet políček horizontálně.
 * 
 * @param game hra
 * @param player_pos pořadí hráče
 * @param x souřadnice X tahu hráče
 * @param y souřadnice Y tahu hráče
 * @return pořadí hráče nebo 0, pokud není vítěz
 */
int8_t check_horizontal(game_t *game, int8_t player_pos, int8_t x, int8_t y) {
    int8_t counter = 0;

    int j;
    for (j = x - game->cell_count + 1;
            j < x + game->cell_count; j++) {
        if (j < 0 || j >= game->board_size) {
            continue;
        }

        if (game->board[y][j] == player_pos) {
            game->winner_cells_x[counter] = j;
            game->winner_cells_y[counter] = y;
            counter++;
        }
        else {
            counter = 0;
        }

        if (counter >= game->cell_count) {
            return player_pos;
        }
    }

    return 0;
}

/**
 * Zkontroluje, zda byl obsazen dostatečný počet políček vertikálně.
 * 
 * @param game hra
 * @param player_pos pořadí hráče
 * @param x souřadnice X tahu hráče
 * @param y souřadnice Y tahu hráče
 * @return pořadí hráče nebo 0, pokud není vítěz
 */
int8_t check_vertical(game_t *game, int8_t player_pos, int8_t x, int8_t y) {
    int8_t counter = 0;

    int i;
    for (i = y - game->cell_count + 1;
            i < y + game->cell_count; i++) {
        if (i < 0 || i >= game->board_size) {
            continue;
        }

        if (game->board[i][x] == player_pos) {
            game->winner_cells_x[counter] = x;
            game->winner_cells_y[counter] = i;
            counter++;
        }
        else {
            counter = 0;
        }

        if (counter >= game->cell_count) {
            return player_pos;
        }
    }

    return 0;
}

/**
 * Zkontroluje, zda byl obsazen dostatečný počet políček diagonálně zprava.
 * 
 * @param game hra
 * @param player_pos pořadí hráče
 * @param x souřadnice X tahu hráče
 * @param y souřadnice Y tahu hráče
 * @return pořadí hráče nebo 0, pokud není vítěz
 */
int8_t check_diag_right(game_t *game, int8_t player_pos, int8_t x, int8_t y) {
    int8_t counter = 0;

    int i = y + game->cell_count - 1;

    int j;
    for (j = x - game->cell_count + 1;
            j < x + game->cell_count
            && i >= y - game->cell_count; j++) {
        if (i < 0 || j < 0 || i >= game->board_size || j >= game->board_size) {
            i--;
            continue;
        }

        if (game->board[i][j] == game->player_count) {
            game->winner_cells_x[counter] = i;
            game->winner_cells_y[counter] = j;
            counter++;
        }
        else {
            counter = 0;
        }

        if (counter >= game->cell_count) {
            return player_pos;
        }

        i--;
    }

    return 0;
}

/**
 * Zkontroluje, zda byl obsazen dostatečný počet políček diagonálně zleva.
 * 
 * @param game hra
 * @param player_pos pořadí hráče
 * @param x souřadnice X tahu hráče
 * @param y souřadnice Y tahu hráče
 * @return pořadí hráče nebo 0, pokud není vítěz
 */
int8_t check_diag_left(game_t *game, int8_t player_pos, int8_t x, int8_t y) {
    int8_t counter = 0;

    int i = y - game->cell_count + 1;

    int j;
    for (j = x - game->cell_count + 1;
            j < x + game->cell_count
            && i < y + game->cell_count; j++) {
        if (i < 0 || j < 0 || i >= game->board_size || j >= game->board_size) {
            i++;
            continue;
        }

        if (game->board[i][j] == player_pos) {
            game->winner_cells_x[counter] = i;
            game->winner_cells_y[counter] = j;
            counter++;
        }
        else {
            counter = 0;
        }

        if (counter >= game->cell_count) {
            return player_pos;
        }

        i++;
    }

    return 0;
}

/**
 * Zkontroluje, zda poslední tah hráče byl vítězný.
 * 
 * @param game hra
 * @param player_pos pořadí hráče
 * @param x souřadnice X tahu hráče
 * @param y souřadnice Y tahu hráče
 * @return pořadí hráče nebo 0, pokud není vítěz
 */
int8_t get_winner(game_t *game, int8_t player_pos, int8_t x, int8_t y) {
    int8_t winner = check_horizontal(game, player_pos, x, y);
    
    if (winner > 0) {
        return winner;
    }
    
    winner = check_vertical(game, player_pos, x, y);
    
    if (winner > 0) {
        return winner;
    }
    
    winner = check_diag_right(game, player_pos, x, y);
    
    if (winner > 0) {
        return winner;
    }
    
    winner = check_diag_left(game, player_pos, x, y);
    
    if (winner > 0) {
        return winner;
    }
    
    return 0;
}

/**
 * Zkontroluje, zda výsledkem posledního tahu hráče je remíza.
 * 
 * @param game hra
 * @return true, pokud je remíza, jinak false
 */
bool is_draw(game_t *game) {
    return game->occupied_cell_counter == game->board_size * game->board_size;
}

/**
 * Zpracuje tah hráče na zadané souřadnice.
 * 
 * @param game hra
 * @param player_pos pořadí hráče
 * @param x souřadnice X tahu hráče
 * @param y souřadnice Y tahu hráče
 */
void play(game_t *game, int8_t player_pos, int8_t x, int8_t y) {
    game->board[y][x] = player_pos;
    game->last_cell_x = x;
    game->last_cell_y = y;
    game->occupied_cell_counter++;
    
    set_next_player(game);
    game->current_winner = get_winner(game, player_pos, x, y);
    
    if (game->current_winner > 0) {
        player_t *winner = game->players[game->current_winner - 1];
        winner->current_game_score++;
        winner->total_score++;
    }
    
    game->round_finished = game->current_winner > 0 || is_draw(game);
    game->changed = true;
}
