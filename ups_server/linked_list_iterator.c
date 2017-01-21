/*
 * Modul linked_list_iterator definuje funkce pro vytvoření a odstranění
 * iterátoru nad zadaným spojovým seznamem a pro iteraci přes jeho prvky.
 * 
 * Author: Petr Kozler
 */

#include "linked_list_iterator.h"
#include <stdlib.h>

/**
 * Vytvoří iterátor.
 * 
 * @param list seznam
 * @return iterátor
 */
linked_list_iterator_t *create_iterator(linked_list_t *list) {
    if (list == NULL) {
        return NULL;
    }
    
    linked_list_iterator_t *iterator = (linked_list_iterator_t *) malloc(sizeof(linked_list_iterator_t));
    
    iterator->list = list;
    iterator->current = list->first;
    iterator->recent = (linked_list_node_t *) malloc(sizeof(linked_list_node_t));
    
    return iterator;
}

/**
 * Získá další prvek seznamu.
 * 
 * @param iterator iterátor
 * @return hodnota prvku
 */
void *get_next_element(linked_list_iterator_t *iterator) {
    if (iterator == NULL || iterator->current == NULL) {
        return NULL;
    }
    
    void *e = iterator->current->value;
    iterator->recent = iterator->current;
    iterator->current = iterator->current->next;
    
    return e;
}

/**
 * Odstraní předchozí prvek seznamu.
 * 
 * @param iterator iterátor
 * @return hodnota prvku
 */
void *remove_last_element(linked_list_iterator_t *iterator) {
    if (iterator == NULL || iterator->recent == NULL) {
        return NULL;
    }
    
    linked_list_node_t *node = iterator->recent;
    void *e = node->value;
    iterator->recent = iterator->recent->previous;
    remove_node(iterator->list, node);
    iterator->list->count--;
    
    return e;
}

/**
 * Otestuje, zda seznam obsahuje další prvek.
 * 
 * @param iterator iterátor
 * @return true, pokud obsahuje další prvek, jinak false
 */
bool has_next_element(linked_list_iterator_t *iterator) {
    if (iterator == NULL) {
        return false;
    }
    
    return iterator->current != NULL;
}
