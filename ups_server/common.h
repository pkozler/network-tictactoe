#ifndef COMMON_H_INCLUDED
#define COMMON_H_INCLUDED

#include "game.h"
#include "thread.h"

/*
    header s deklaracemi společných a pomocných funkcí
*/

/* pomocná funkce pro přidání hráče */
void add_player_to_game(game_t *, thread_t *);
/* pomocná funkce pro odstranění hráče */
void remove_player_from_game(thread_t *);
/* příjem dat */
void read_bytes(thread_t *, void*, unsigned int);
/* odesílání dat */
void write_bytes(thread_t *, void *, unsigned int);
/* zjištění hráče, který je na tahu */
bool can_play(thread_t *);

#endif // COMMON_H_INCLUDED
