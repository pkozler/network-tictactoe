/*
 * Author: Petr Kozler
 */

#ifndef LINKED_LIST_NODE_H
#define LINKED_LIST_NODE_H

typedef struct LINKED_LIST_NODE {
    struct LINKED_LIST_NODE *next;
    struct LINKED_LIST_NODE *previous;
    void *value;
} linked_list_node_t;

#endif /* LINKED_LIST_NODE_H */

