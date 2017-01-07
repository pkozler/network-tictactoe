/* 
 * Modul checker definuje funkce pro validaci požadavků klienta.
 * 
 * Author: Petr Kozler
 */

#include "checker.h"
#include "config.h"
#include <stdlib.h>
#include <string.h>

/**
 * Otestuje platnost ID.
 * 
 * @param id ID hráče nebo hry
 * @return true, pokud je ID platné, jinak false
 */
bool is_id_valid(int32_t id) {
    return id > 0;
}

/**
 * Otestuje platnost jména.
 * 
 * @param name jméno hráče nebo hry
 * @return true, pokud je jméno platné, jinak false
 */
bool is_name_valid(char *name) {
    if (name == NULL) {
        return false;
    }
    
    int32_t len = strlen(name);
    
    // testování délky řetězce
    if (len < 1 || MAX_NAME_LENGTH < len) {
        return false;
    }
    
    // testování platnosti znaků
    int32_t i;
    for (i = 0; name[i] != '\0'; i++) {
        if (   !(name[i] >= 'a' || name[i] <= 'z')
            && !(name[i] >= 'A' || name[i] <= 'Z')
            && !(name[i] >= '0' || name[i] <= '9')
            && !(name[i] == '-' || name[i] == '_')) {
            return false;
        }
    }
    
    return true;
}

/**
 * Otestuje, zda je hráč přihlášen.
 * 
 * @param player hráč
 * @return true, pokud je hráč přihlášen, jinak false
 */
bool is_player_logged(player_t *player) {
    return player->id > 0;
}

/**
 * Otestuje, zda je velikost hracího pole platná.
 * 
 * @param board_size velikost hracího pole
 * @return true, pokud je údaj platný, jinak false
 */
bool is_game_board_size_valid(int8_t board_size) {
    return MIN_BOARD_SIZE <= board_size && board_size <= MAX_BOARD_SIZE;
}

/**
 * Otestuje, zda je počet hráčů platný.
 * 
 * @param player_count počet hráčů
 * @return true, pokud je údaj platný, jinak false
 */
bool is_game_player_count_valid(int8_t player_count) {
    return MIN_PLAYERS_SIZE <= player_count && player_count <= MAX_PLAYERS_SIZE;
}

/**
 * Otestuje, zda je počet políček potřebných k obsazení platný.
 * 
 * @param cell_count počet políček k obsazení
 * @return true, pokud je údaj platný, jinak false
 */
bool is_game_cell_count_valid(int8_t cell_count) {
    return MIN_CELL_COUNT <= cell_count && cell_count <= MIN_CELL_COUNT;
}

/**
 * Otestuje, zda je daný hráč právě na tahu.
 * 
 * @param game hra
 * @param player hráč
 * @return true, pokud je hráč na tahu, jinak false
 */
bool is_current_player(game_t *game, player_t *player) {
    return player->current_game_index == game->current_playing;
}

/**
 * Otestuje, zda se políčko o daných souřadnicích nachází v herním poli.
 * 
 * @param game hra
 * @param x souřadnice X
 * @param y souřadnice Y
 * @return true, pokud je políčko v herním poli, jinak false
 */
bool is_cell_in_board(game_t *game, int8_t x, int8_t y) {
    return 0 <= x && x < game->board_size
            && 0 <= y && y < game->board_size;
}

/**
 * Otestuje, zda je herní místnost plná.
 * 
 * @param game hra
 * @return true, pokud je hra plná, jinak false
 */
bool is_game_full(game_t *game) {
    return game->player_counter == game->player_count;
}

/**
 * Otestuje, zda má herní místnost dostatek hráčů.
 * 
 * @param game hra
 * @return true, pokud má hra dostatek hráčů, jinak false
 */
bool has_game_enough_players(game_t *game) {
    return game->player_counter > 1;
}

/**
 * Otestuje, zda bylo herní kolo zahájeno.
 * 
 * @param game hra
 * @return true, pokud bylo herní kolo zahájeno, jinak false
 */
bool is_round_started(game_t *game) {
    return !game->round_finished;
}

/**
 * Otestuje, zda hráč může táhnout na daném políčku.
 * 
 * @param game hra
 * @param x souřadnice X
 * @param y souřadnice Y
 * @return true, pokud hráč může táhnout, jinak false
 */
bool can_play(game_t *game, int8_t x, int8_t y) {
    return game->board[y][x] == 0;
}
