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
 * Označí přítomné hráče jako hrající v aktuálním kole
 * a nastaví prvního hráče na tahu.
 */
void set_players_as_playing(game_t *game) {
    game->current_player_on_turn = 0;
    
    int8_t i;
    for (i = 0; i < game->player_count; i++) {
        if (game->players[i] == NULL) {
            continue;
        }
        
        game->players[i]->playing_in_round = true;
        
        if (game->current_player_on_turn == 0) {
            game->current_player_on_turn = i + 1;
        }
    }
}

/**
 * Přepne do dalšího kola a označí jej za zahájené.
 * 
 * @param game hra
 */
void set_round_as_started(game_t *game) {
    game->last_player_on_turn = 0;
    game->last_cell_x = 0;
    game->last_cell_y = 0;
    game->current_winner = 0;
    game->current_round++;
    game->round_finished = false;
}

/**
 * Vynuluje herní pole.
 * 
 * @param game hra
 */
void clear_game_board(game_t *game) {
    int8_t i, j;
    for (i = 0; i < game->board_size; i++) {
        for (j = 0; j < game->board_size; j++) {
            game->board[i][j] = 0;
        }
    }
    
    game->occupied_cell_counter = 0;
}

/**
 * Vynuluje pořadí vítěze a seznam vítězných políček.
 * 
 * @param game hra
 */
void clear_winner_cells(game_t *game) {
    int8_t i;
    for (i = 0; i < game->cell_count; i++) {
        game->winner_cells_x[i] = 0;
        game->winner_cells_y[i] = 0;
    }
}

/**
 * Zahájí další kolo hry.
 * 
 * @param game hra
 */
void start_next_round(game_t *game) {
    set_players_as_playing(game);
    clear_game_board(game);
    clear_winner_cells(game);
    set_round_as_started(game);
}

/**
 * Přepne na dalšího hráče, který je na řadě.
 * 
 * @param game hra
 */
void set_player_on_turn(game_t *game) {
    int8_t i = game->current_player_on_turn - 1;

    do {
        i++;
        i %= game->player_count;
    }
    while (game->players[i] == NULL || !(game->players[i]->playing_in_round));

    game->current_player_on_turn = i + 1;
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
            player->playing_in_round = false;
            game->players[i] = player;
            game->player_counter++;
            
            break;
        }
    }
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
        
        if (game->current_player_on_turn == player->current_game_index) {
            set_player_on_turn(game);
        }

        player->playing_in_round = false;
        player->current_game_index = 0;
        player->current_game = NULL;
        player->current_game_score = 0;
        game->players[i] = NULL;
        game->player_counter--;
    }
}

/**
 * Zkontroluje, zda byl obsazen dostatečný počet políček horizontálně.
 * 
 * @param game hra
 * @param player_index pořadí hráče
 * @param x souřadnice X tahu hráče
 * @param y souřadnice Y tahu hráče
 * @return pořadí hráče nebo 0, pokud není vítěz
 */
int8_t check_horizontal(game_t *game, int8_t player_index, int8_t x, int8_t y) {
    int8_t counter = 0;
    int8_t length = game->cell_count - 1;
    
    int8_t j;
    // průchod sousedními políčky
    for (j = x - length; j <= x + length; j++) {
        // pozice je mimo hranice herního pole
        if (j < 0 || j >= game->board_size) {
            continue;
        }

        // označení políčka odpovídá označení hráče
        if (game->board[y][j] == player_index) {
            // uložení do seznamu vítězných políček
            game->winner_cells_x[counter] = j;
            game->winner_cells_y[counter] = y;
            counter++;
        }
        // políčko není hráčem obsazeno
        else {
            counter = 0;
        }

        // hráč obsadil dostatečný počet políček
        if (counter >= game->cell_count) {
            return player_index;
        }
    }

    return 0;
}

/**
 * Zkontroluje, zda byl obsazen dostatečný počet políček vertikálně.
 * 
 * @param game hra
 * @param player_index pořadí hráče
 * @param x souřadnice X tahu hráče
 * @param y souřadnice Y tahu hráče
 * @return pořadí hráče nebo 0, pokud není vítěz
 */
int8_t check_vertical(game_t *game, int8_t player_index, int8_t x, int8_t y) {
    int8_t counter = 0;
    int8_t length = game->cell_count - 1;

    int8_t i;
    // průchod sousedními políčky
    for (i = y - length; i <= y + length; i++) {
        // pozice je mimo hranice pole
        if (i < 0 || i >= game->board_size) {
            continue;
        }

        // označení políčka odpovídá označení hráče
        if (game->board[i][x] == player_index) {
            game->winner_cells_x[counter] = x;
            game->winner_cells_y[counter] = i;
            counter++;
        }
        // políčko není hráčem obsazeno
        else {
            counter = 0;
        }

        // hráč obsadil dostatečný počet políček
        if (counter >= game->cell_count) {
            return player_index;
        }
    }

    return 0;
}

/**
 * Zkontroluje, zda byl obsazen dostatečný počet políček diagonálně zprava.
 * 
 * @param game hra
 * @param player_index pořadí hráče
 * @param x souřadnice X tahu hráče
 * @param y souřadnice Y tahu hráče
 * @return pořadí hráče nebo 0, pokud není vítěz
 */
int8_t check_diag_right(game_t *game, int8_t player_index, int8_t x, int8_t y) {
    int8_t counter = 0;
    int8_t length = game->cell_count - 1;
    
    int8_t j = x + length; // začíná se vpravo
    int8_t i = y - length; // začíná se nahoře
    int8_t length_j = x - length; // končí se vlevo
    int8_t length_i = y + length; // končí se dole
    
    // průchod sousedními políčky
    while (j >= length_j && i <= length_i) {
        // pozice je mimo hranice pole
        if (j < 0 || i < 0 || j >= game->board_size || i >= game->board_size) {
            j--;
            i++;
            
            continue;
        }
        
        // označení políčka odpovídá označení hráče
        if (game->board[i][j] == player_index) {
            game->winner_cells_x[counter] = j;
            game->winner_cells_y[counter] = i;
            counter++;
        }
        // políčko není hráčem obsazeno
        else {
            counter = 0;
        }

        // hráč obsadil dostatečný počet políček
        if (counter >= game->cell_count) {
            return player_index;
        }
        
        j--;
        i++;
    }
    
    return 0;
}

/**
 * Zkontroluje, zda byl obsazen dostatečný počet políček diagonálně zleva.
 * 
 * @param game hra
 * @param player_index pořadí hráče
 * @param x souřadnice X tahu hráče
 * @param y souřadnice Y tahu hráče
 * @return pořadí hráče nebo 0, pokud není vítěz
 */
int8_t check_diag_left(game_t *game, int8_t player_index, int8_t x, int8_t y) {
    int8_t counter = 0;
    int8_t length = game->cell_count - 1;
    
    int8_t j = x - length; // začíná se vlevo
    int8_t i = y - length; // začíná se nahoře
    int8_t length_j = x + length; // končí se vpravo
    int8_t length_i = y + length; // končí se dole
    
    // průchod sousedními políčky
    while (j <= length_j && i <= length_i) {
        // pozice je mimo hranice pole
        if (j < 0 || i < 0 || j >= game->board_size || i >= game->board_size) {
            j++;
            i++;
            
            continue;
        }
        
        // označení políčka odpovídá označení hráče
        if (game->board[i][j] == player_index) {
            game->winner_cells_x[counter] = j;
            game->winner_cells_y[counter] = i;
            counter++;
        }
        // políčko není hráčem obsazeno
        else {
            counter = 0;
        }

        // hráč obsadil dostatečný počet políček
        if (counter >= game->cell_count) {
            return player_index;
        }
        
        j++;
        i++;
    }
    
    return 0;
}

/**
 * Zkontroluje, zda poslední tah hráče byl vítězný.
 * 
 * @param game hra
 * @param player_index pořadí hráče
 * @param x souřadnice X tahu hráče
 * @param y souřadnice Y tahu hráče
 * @return pořadí hráče nebo 0, pokud není vítěz
 */
int8_t get_winner(game_t *game, int8_t player_index, int8_t x, int8_t y) {
    int8_t winner = 0;
    
    clear_winner_cells(game);
    winner = check_horizontal(game, player_index, x, y);
    
    if (winner > 0) {
        return winner;
    }
    
    clear_winner_cells(game);
    winner = check_vertical(game, player_index, x, y);
    
    if (winner > 0) {
        return winner;
    }
    
    clear_winner_cells(game);
    winner = check_diag_right(game, player_index, x, y);
    
    if (winner > 0) {
        return winner;
    }
    
    clear_winner_cells(game);
    winner = check_diag_left(game, player_index, x, y);
    
    if (winner > 0) {
        return winner;
    }
    
    clear_winner_cells(game);
    
    return 0;
}

/**
 * Zkontroluje, zda výsledkem posledního tahu hráče je remíza, která nastane
 * v případě, že hráči obsadili všechny políčka bez dosažení vítězství.
 * 
 * @param game hra
 * @return true, pokud je remíza, jinak false
 */
bool is_draw(game_t *game) {
    return game->occupied_cell_counter >= ((int16_t) game->board_size * (int16_t) game->board_size);
}

/**
 * Obsadí políčko a uloží poslední tah hry.
 * 
 * @param game hra
 * @param player_index pořadí hráče
 * @param x souřadnice X tahu hráče
 * @param y souřadnice Y tahu hráče
 */
void set_last_turn(game_t *game, int8_t player_index, int8_t x, int8_t y) {
    game->board[y][x] = player_index;
    game->last_player_on_turn = player_index;
    game->last_cell_x = x;
    game->last_cell_y = y;
    game->occupied_cell_counter++;
}

/**
 * Nastaví pořadí vítěze daného herního kola a to označí jako ukončené,
 * případnému vítězi zároveň přičte body ke skóre ve hře i k celkovému skóre.
 * 
 * @param game hra
 * @param winner_index pořadí vítěze
 */
void set_round_as_finished(game_t *game, int8_t winner_index) {
    game->current_winner = winner_index;
    
    if (game->current_winner > 0) {
        player_t *winner = game->players[game->current_winner - 1];
        winner->current_game_score++;
        winner->total_score++;
    }
    
    game->current_player_on_turn = 0;
    game->round_finished = true;
}

/**
 * Zpracuje tah hráče na zadané souřadnice.
 * 
 * @param game hra
 * @param player_index pořadí hráče
 * @param x souřadnice X tahu hráče
 * @param y souřadnice Y tahu hráče
 */
void play(game_t *game, int8_t player_index, int8_t x, int8_t y) {
    set_last_turn(game, player_index, x, y);
    
    int8_t winner = get_winner(game, player_index, x, y);
    
    if (winner > 0) {
        set_round_as_finished(game, winner);
        
        return;
    }
    
    bool draw = is_draw(game);
    
    if (draw) {
        set_round_as_finished(game, 0);
        
        return;
    }
    
    set_player_on_turn(game);
}
