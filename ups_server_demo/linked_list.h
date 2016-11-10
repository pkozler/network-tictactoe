/* 
 * Author: Petr Kozler
 * 
 * Hlavičkový soubor linked_list.h definuje strukturu spojového seznamu
 * a jeho uzlu, který uchovává hodnotu libovolného datového typu, a dále
 * obsahuje deklarace funkcí k provádění základních operací nad tímto seznamem.
 */

#ifndef CLIENT_LIST_H
#define CLIENT_LIST_H

#include <stdbool.h>
#include <stdint.h>

/*
 * Struktura uzlu spojového seznamu.
 */
typedef struct LINKED_LIST_NODE {
    void *value; // hodnota prvku
    struct LINKED_LIST_NODE *next; // další prvek
    struct LINKED_LIST_NODE *previous; // předchozí prvek
} linked_list_node_t;

/*
 * Struktura spojového seznamu.
 */
typedef struct {
    int32_t count; // počet prvků
    linked_list_node_t *first; // první prvek
    linked_list_node_t *last; // poslední prvek
} linked_list_t;

linked_list_t *create_linked_list();

void delete_linked_list(linked_list_t *ptr);

bool add_to_list(linked_list_t *ptr, void *e);

void *get_from_list(linked_list_t *ptr, int32_t index);

void *set_in_list(linked_list_t *ptr, int32_t index, void *element);

void *remove_from_list(linked_list_t *ptr, int32_t index);

int32_t size_of_list(linked_list_t *ptr);

bool is_list_empty(linked_list_t *ptr);

void clear_list(linked_list_t *ptr);

#endif /* CLIENT_LIST_H */

