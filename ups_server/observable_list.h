/* 
 * Hlavičkový soubor observable_list definuje strukturu pozorovatelného
 * spojového seznamu (použito pro seznam hráčů a her).
 * 
 * Author: Petr Kozler
 */

#ifndef OBSERVABLE_LIST_H
#define OBSERVABLE_LIST_H

#include <stdbool.h>
#include <pthread.h>
#include "linked_list.h"

/**
 * Struktura pozorovatelného spojového seznamu.
 */
typedef struct {
    linked_list_t *list; // vnitřní spojový seznam
    pthread_t thread; // vlákno pro pozorování seznamu
    pthread_mutex_t lock; // zámek struktury seznamu
    bool changed; // příznak změny seznamu
} observable_list_t;

#endif /* OBSERVABLE_LIST_H */
