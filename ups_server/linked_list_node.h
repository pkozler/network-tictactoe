/*
 * Hlavičkový soubor linked_list_node definuje strukturu uzlu
 * spojového seznamu.
 * 
 * Author: Petr Kozler
 */

#ifndef LINKED_LIST_NODE_H
#define LINKED_LIST_NODE_H

/**
 * Struktura uzlu obousměrně zřetězeného spojového seznamu.
 */
typedef struct LINKED_LIST_NODE {
    struct LINKED_LIST_NODE *next; // další uzel
    struct LINKED_LIST_NODE *previous; // předchozí uzel
    void *value; // uložená hodnota v uzlu
} linked_list_node_t;

#endif /* LINKED_LIST_NODE_H */

