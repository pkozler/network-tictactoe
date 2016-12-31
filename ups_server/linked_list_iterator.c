/*
 * Author: Petr Kozler
 */

#include "linked_list_iterator.h"
#include <stdlib.h>

linked_list_iterator_t *create_iterator(linked_list_t *list) {
    if (list == NULL) {
        return NULL;
    }
    
    linked_list_iterator_t *iterator = (linked_list_iterator_t *) malloc(sizeof(linked_list_iterator_t));
    
    iterator->list = list;
    iterator->current == list->first;
    
    return iterator;
}

void *get_next_element(linked_list_iterator_t *iterator) {
    if (iterator == NULL || iterator->current == NULL) {
        return NULL;
    }
    
    void *e = iterator->current->value;
    iterator->current = iterator->current->next;
    
    return e;
}

void *remove_last_element(linked_list_iterator_t *iterator) {
    if (iterator == NULL || iterator->current == NULL) {
        return NULL;
    }
    
    linked_list_node_t *node = iterator->current->previous;
    void *e = node->value;
    remove_node(iterator->list, node);
    iterator->list->count--;
    
    return e;
}

bool has_next_element(linked_list_iterator_t *iterator) {
    if (iterator == NULL) {
        return false;
    }
    
    return iterator->current != NULL;
}
