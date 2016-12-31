/*
 * Author: Petr Kozler
 */

#ifndef LINKED_LIST_H
#define LINKED_LIST_H

#include "linked_list_node.h"
#include <stdint.h>
#include <stdbool.h>

typedef void (*foreach_func_t)(void *e, void *foreach_arg);
typedef void (*dispose_func_t)(void *e);

typedef struct {
    int32_t count;
    linked_list_node_t *first;
    linked_list_node_t *last;
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
void enqueue_element(linked_list_t *list, void *e);
void *dequeue_element(linked_list_t *list);
void do_foreach_element(linked_list_t *list, foreach_func_t foreach_func, void *foreach_arg)

#endif /* LINKED_LIST_H */

