/*
 * Modul linked_list definuje funkce pro vytvoření, odstranění a manipulaci
 * se spojovým seznamem.
 * 
 * Author: Petr Kozler
 */

#include "linked_list.h"
#include <stdlib.h>

/**
 * Vytvoří spojový seznam.
 * 
 * @return spojový seznam
 */
linked_list_t *create_linked_list() {
    linked_list_t *list = (linked_list_t *) malloc(sizeof(linked_list_t));
    
    list->first = NULL;
    list->last = NULL;
    list->count = 0;
    
    return list;
}

/**
 * Odstraní spojový seznam.
 * 
 * @param list spojový seznam
 * @param dispose funkce pro uvolnění hodnoty uzlu z paměti
 */
void delete_linked_list(linked_list_t *list, dispose_func_t dispose) {
    linked_list_node_t *node = list->first;

    while (node != NULL) {
        if (dispose != NULL) {
            dispose(node->value);
        }
        
        remove_node(list, node);
        node = list->first;
    }
    
    free(list);
}

/**
 * Přidá prvek za zadaný uzel.
 * 
 * @param list spojový seznam
 * @param node existující uzel
 * @param new_node nový uzel
 */
void add_after_node(linked_list_t *list, linked_list_node_t *node, linked_list_node_t *new_node) {
    if (list == NULL || node == NULL || new_node == NULL) {
        return;
    }
    
    new_node->previous = node;
    new_node->next = node->next;

    if (node->next == NULL) {
        list->last = new_node;
    }
    else {
        node->next->previous = new_node;
    }

    node->next = new_node;
}

/**
 * Přidá prvek před zadaný uzel.
 * 
 * @param list spojový seznam
 * @param node existující uzel
 * @param new_node nový uzel
 */
void add_before_node(linked_list_t *list, linked_list_node_t *node, linked_list_node_t *new_node) {
    if (list == NULL || node == NULL || new_node == NULL) {
        return;
    }
    
    new_node->previous = node->previous;
    new_node->next = node;

    if (node->previous == NULL) {
        list->first = new_node;
    }
    else {
        node->previous->next = new_node;
    }

    node->previous = new_node;
}

/**
 * Přidá prvek na začátek seznamu.
 * 
 * @param list spojový seznam
 * @param new_node nový uzel
 */
void add_first_node(linked_list_t *list, linked_list_node_t *new_node) {
    if (list == NULL || new_node == NULL) {
        return;
    }
    
    if (list->first == NULL) {
        list->first = new_node;
        list->last = new_node;
        new_node->previous = NULL;
        new_node->next = NULL;
    }
    else {
        add_before_node(list, list->first, new_node);
    }
}

/**
 * Přidá prvek na konec seznamu.
 * 
 * @param list spojový seznam
 * @param new_node nový uzel
 */
void add_last_node(linked_list_t *list, linked_list_node_t *new_node) {
    if (list == NULL || new_node == NULL) {
        return;
    }
    
    if (list->last == NULL) {
        add_first_node(list, new_node);
    }
    else {
        add_after_node(list, list->last, new_node);
    }
}

/**
 * Odstraní uzel.
 * 
 * @param list spojový seznam
 * @param node existující uzel
 */
void remove_node(linked_list_t *list, linked_list_node_t *node) {
    if (list == NULL || node == NULL) {
        return;
    }
    
    if (node->previous == NULL) {
        list->first = node->next;
    }
    else {
        node->previous->next = node->next;
    }

    if (node->next == NULL) {
        list->last = node->previous;
    }
    else {
        node->next->previous = node->previous;
    }
    
    free(node);
}

/**
 * Spočítá prvky v seznamu.
 * 
 * @param list spojový seznam
 * @return počet prvků
 */
int32_t count_elements(linked_list_t *list) {
    if (list == NULL) {
        return 0;
    }
    
    return list->count;
}

/**
 * Otestuje, zda je seznam prázdný.
 * 
 * @param list spojový seznam
 * @return true, pokud je prázdný, jinak false
 */
bool is_linked_list_empty(linked_list_t *list) {
    if (list == NULL) {
        return true;
    }
    
    return list->count == 0;
}

/**
 * Přidá prvek.
 * 
 * @param list spojový seznam
 * @param e hodnota prvku
 */
void add_element(linked_list_t *list, void *e) {
    if (list == NULL) {
        return;
    }
    
    linked_list_node_t *node = (linked_list_node_t *) malloc(sizeof(linked_list_node_t));
    node->value = e;
    add_last_node(list, node);
    list->count++;
}

/**
 * Odstraní prvek.
 * 
 * @param list spojový seznam
 * @param e hodnota prvku
 */
void remove_element(linked_list_t *list, void *e) {
    if (list == NULL) {
        return;
    }
    
    linked_list_node_t *node;

    for (node = list->first; node != NULL; node = node->next) {
        if (node->value == e) {
            remove_node(list, node);
            list->count--;
            break;
        }
    }
}

/**
 * Vloží prvek na začátek.
 * 
 * @param list spojový seznam
 * @param e hodnota prvku
 */
void enqueue_element(linked_list_t *list, void *e) {
    if (list == NULL) {
        return;
    }
    
    linked_list_node_t *node = (linked_list_node_t *) malloc(sizeof(linked_list_node_t));
    node->value = e;
    add_first_node(list, node);
    list->count++;
}

/**
 * Vyjme prvek na konci.
 * 
 * @param list spojový seznam
 * @return hodnota prvku
 */
void *dequeue_element(linked_list_t *list) {
    if (list == NULL || list->count < 1) {
        return NULL;
    }
    
    linked_list_node_t *node = list->last;
    void *e = node->value;
    remove_node(list, node);
    list->count--;
    
    return e;
}