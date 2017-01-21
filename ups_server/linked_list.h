/*
 * Hlavičkový soubor linked_list definuje strukturu spojového seznamu
 * používanou pro uchovávání dat v různých částech programu
 * (hry, hráči, nepřihlášení klienti).
 * 
 * Author: Petr Kozler
 */

#ifndef LINKED_LIST_H
#define LINKED_LIST_H

#include "linked_list_node.h"
#include <stdint.h>
#include <stdbool.h>

// ukazatel na funkci pro uvolnění hodnoty uložené v uzlu seznamu z paměti
typedef void (*dispose_func_t)(void *e);

/**
 * Struktura obousměrně zřetězeného spojového seznamu.
 */
typedef struct {
    int32_t count; // počet prvků
    linked_list_node_t *first; // první uzel
    linked_list_node_t *last; // poslední uzel
} linked_list_t;

linked_list_t *create_linked_list();
void delete_linked_list(linked_list_t *list, dispose_func_t dispose);
void add_after_node(linked_list_t *list, linked_list_node_t *node, linked_list_node_t *new_node);
void add_before_node(linked_list_t *list, linked_list_node_t *node, linked_list_node_t *new_node);
void add_first_node(linked_list_t *list, linked_list_node_t *new_node);
void add_last_node(linked_list_t *list, linked_list_node_t *new_node);
void remove_node(linked_list_t *list, linked_list_node_t *node);
int32_t count_elements(linked_list_t *list);
bool is_linked_list_empty(linked_list_t *list);
void add_element(linked_list_t *list, void *e);
void remove_element(linked_list_t *list, void *e);
void enqueue_element(linked_list_t *list, void *e);
void *dequeue_element(linked_list_t *list);

#endif /* LINKED_LIST_H */

