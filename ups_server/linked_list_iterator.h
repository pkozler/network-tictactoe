/*
 * Hlavičkový soubor linked_list_iterator definuje strukturu iterátoru
 * spojového seznamu.
 * 
 * Author: Petr Kozler
 */

#ifndef LINKED_LIST_ITERATOR_H
#define LINKED_LIST_ITERATOR_H

#include "linked_list.h"
#include "linked_list_node.h"
#include <stdbool.h>

/**
 * Struktura iterátoru obousměrně zřetězeného spojového seznamu.
 */
typedef struct {
    linked_list_t *list; // iterovaný spojový seznam
    linked_list_node_t *current; // aktuální prvek
    linked_list_node_t *recent; // ukazatel na předchozí prvek
} linked_list_iterator_t;

linked_list_iterator_t *create_iterator(linked_list_t *list);
void *get_next_element(linked_list_iterator_t *iterator);
void *remove_last_element(linked_list_iterator_t *iterator);
bool has_next_element(linked_list_iterator_t *iterator);

#endif /* LINKED_LIST_ITERATOR_H */
