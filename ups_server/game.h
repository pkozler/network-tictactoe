/* 
 * Hlavičkový soubor game definuje strukuru herní místnosti a deklaruje
 * funkce pro její vytvoření a odstranění.
 * 
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

/**
 * Struktura herní místnosti.
 */
typedef struct GAME {
    pthread_t thread; // vlákno pro pozorování stavu hry
    pthread_mutex_t lock; // zámek struktury hry
    bool changed; // příznak změny stavu hry
    /*
     * základní informace o herní místnosti odesílané v položkách seznamu her:
     */
    int32_t id; // ID hry
    char *name; // název hry
    int8_t board_size; // rozměr herního pole
    int8_t cell_count; // počet políček potřebných k obsazení
    int8_t player_count; // maximální počet hráčů
    int8_t player_counter; // aktuální počet hráčů
    /*
     * detailní informace odesílané pouze hráčům v dané herní místnosti:
     */
    struct PLAYER **players; // pole hráčů
    int32_t current_round; // aktuální kolo hry
    bool round_finished; // příznak dokončení kola
    int8_t current_playing; // pořadí hráče, který je na řadě (0, pokud hra ještě nezačala)
    int8_t last_playing; // pořadí hráče, který táhl jako poslední (0, pokud ještě nikdo netáhl)
    int8_t last_cell_x; // souřadnice X políčka, na které byl proveden poslední tah
    int8_t last_cell_y; // souřadnice Y políčka, na které byl proveden poslední tah
    int8_t last_leaving; // pořadí hráče, který hru opustil jako poslední
    int8_t current_winner; // pořadí hráče, který je vítězem (0, pokud ještě nikdo nevyhrál)
    int8_t *winner_cells_x; // souřadnice X vítězných políček
    int8_t *winner_cells_y; // souřadnice Y vítězných políček
    int8_t **board; // herní pole 
    /*
     * neodesílané informace o herní místnosti:
     */
    int16_t occupied_cell_counter; // počet obsazených políček v herním poli
} game_t;

game_t *create_game(struct PLAYER *player, char *name,
        int8_t board_size, int8_t player_count, int8_t cell_count);
void delete_game(game_t *);
void lock_game(game_t *game);
void unlock_game(game_t *game, bool changed);

#endif /* GAME_H */
