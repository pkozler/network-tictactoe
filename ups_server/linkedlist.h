#ifndef LINKEDLIST_H_INCLUDED
#define LINKEDLIST_H_INCLUDED

#include "defs.h"
#include "game.h"
#include <stdbool.h>

/*
    header s definicí struktury seznamu a deklaracemi funkcí
    pro jeho obsluhu
*/

/* dopředná deklarace uzlu seznamu */
struct LINKED_LIST;

/**
    Strukura, která slouží jako uzel spojového seznamu.
*/
typedef struct LINKED_LIST {
    void *val; // hodnota (ukazatel na strukturu hry, nebo hráče)
    struct LINKED_LIST *next; // ukazatel na další uzel
} linked_list_t;

/* porovnání her */
bool game_id_equals(void *, int32_t);
/* porovnání hráčů */
bool thread_id_equals(void *, int32_t);
/* získání dalšího prvku */
void *next_element(linked_list_t **);
/* přidání do seznamu */
bool add_to_list(linked_list_t **, void *);
/* vyhledávání v seznamu */
void *get_from_list(linked_list_t *, int32_t, bool (*) (void *, int32_t));
/* odstranění ze seznamu */
void remove_from_list(linked_list_t **, int32_t, bool (*) (void *, int32_t));
/* zjištění velikosti seznamu */
int32_t get_list_size(linked_list_t *);

#endif // LINKEDLIST_H_INCLUDED
