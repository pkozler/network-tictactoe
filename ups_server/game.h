#ifndef GAME_H_INCLUDED
#define GAME_H_INCLUDED

#include <pthread.h>
#include <stdbool.h>
#include <stdint.h>
#include "defs.h"
#include "thread.h"

/*
    header s definicí struktury hry a deklaracemi funkcí
    pro její obsluhu
*/

/* alias pro 1-bajtový znak */
typedef signed char byte_t;

/* dopředná deklarace stavu hráče */
struct THREAD;

/**
    Struktura, která představuje aktuální stav hry.
*/
typedef struct GAME {
    int32_t game_id; // unikátní ID hry
    byte_t matrix_size; // velikost hracího pole
    int16_t cell_counter; // aktuální počet obsazených políček
    byte_t **matrix; // hrací pole
    byte_t win_row_len; // počet políček v řadě, které jeden hráč musí obsadit pro vítězství
    byte_t *win_row; // vítězná políčka
    byte_t players_size; // max. počet hráčů
    byte_t player_counter; // aktuální počet hráčů
    struct THREAD **players; // pole hráčů velikosti players_size
    byte_t current_player; // aktuální hráč (který je na řadě ve hře)
    byte_t last_added_player; // pozice posledního připojeného hráče
    byte_t last_removed_player; // pozice posledního odpojeného hráče
    byte_t last_move_player; // ID hráče, který provedl poslední tah
    byte_t last_move_x; // souřadnice Y posledního tahu
    byte_t last_move_y; // souřadnice X posledního tahu
    byte_t winner; // vítězný hráč
    bool running; // příznak běhu hry
    pthread_mutex_t lock; // zámek pro přístup ke stavové struktuře hry
} game_t;

/* vytvoření hry */
game_t *create_game(int32_t, byte_t, byte_t, byte_t);
/* obnova hry */
void restart_game(game_t *);
/* odstranění hry */
void delete_game(game_t *);
/* přidání hráče */
byte_t add_player(game_t *, struct THREAD*);
/* odstranění hráče */
byte_t remove_player(game_t *, struct THREAD*);
/* tah ve hře */
bool play(game_t *, byte_t, byte_t, byte_t);

#endif // GAME_H_INCLUDED
