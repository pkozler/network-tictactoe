/*
 * Author: Petr Kozler
 */

#ifndef LINKED_LIST_ITERATOR_H
#define LINKED_LIST_ITERATOR_H

#include "linked_list.h"
#include "linked_list_node.h"
#include <stdbool.h>

typedef struct {
    linked_list_t *list;
    linked_list_node_t *current;
} linked_list_iterator_t;

linked_list_iterator_t *create_iterator(linked_list_t *list);
void *get_next_element(linked_list_iterator_t *iterator);
void *remove_last_element(linked_list_iterator_t *iterator);
bool has_next_element(linked_list_iterator_t *iterator);

#endif /* LINKED_LIST_ITERATOR_H */
