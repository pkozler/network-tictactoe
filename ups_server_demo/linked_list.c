/* 
 * Author: Petr Kozler
 * 
 * Modul linked_list.c definuje funkce k provádění operací nad spojovým seznamem,
 * jehož uzly mohou uchovávat hodnoty libovolného datového typu.
 */

#include "linked_list.h"

#include <stdlib.h>
#include <string.h>

/**
 * Vytvoří nový spojový seznam.
 * 
 * @return struktura spojového seznamu
 */
linked_list_t *create_linked_list() {
    linked_list_t *list = malloc(sizeof(linked_list_t));
    list->count = 0;
    list->first = NULL;
    list->last = NULL;

    return list;
}

/**
 * Odstraní předaný spojový seznam.
 * 
 * @param ptr struktura spojového seznamu
 */
void delete_linked_list(linked_list_t *ptr) {
    clear_list(ptr);
    free(ptr);
}

/**
 * Přidá nový uzel za předaný ve spojovém seznamu.
 * 
 * @param ptr struktura spojového seznamu
 * @param node existující uzel
 * @param newNode nový uzel
 */
void add_after(linked_list_t *ptr, linked_list_node_t *node, linked_list_node_t *newNode) {
    newNode->previous = node;
    newNode->next = node->next;

    if (node->next == NULL) {
        ptr->last = newNode;
    }
    else {
        node->next->previous = newNode;
    }

    node->next = newNode;
}

/**
 * Přidá nový uzel před předaný ve spojovém seznamu.
 * 
 * @param ptr struktura spojového seznamu
 * @param node existující uzel
 * @param newNode nový uzel
 */
void add_before(linked_list_t *ptr, linked_list_node_t *node, linked_list_node_t *newNode) {
    newNode->previous = node->previous;
    newNode->next = node;

    if (node->previous == NULL) {
        ptr->first = newNode;
    }
    else {
        node->previous->next = newNode;
    }

    node->previous = newNode;
}

/**
 * Přidá nový uzel na začátek spojového seznamu.
 * 
 * @param ptr struktura spojového seznamu
 * @param newNode nový uzel
 */
void add_first(linked_list_t *ptr, linked_list_node_t *newNode) {
    if (ptr->first == NULL) {
        ptr->first = newNode;
        ptr->last = newNode;
        newNode->previous = NULL;
        newNode->next = NULL;
    }
    else {
        add_before(ptr, ptr->first, newNode);
    }
}

/**
 * Přidá nový uzel na konec spojového seznamu.
 * 
 * @param ptr struktura spojového seznamu
 * @param newNode nový uzel
 */
void add_last(linked_list_t *ptr, linked_list_node_t *newNode) {
    if (ptr->last == NULL) {
        add_first(ptr, newNode);
    }
    else {
        add_after(ptr, ptr->last, newNode);
    }
}

/**
 * Odstraní předaný uzel ve spojovém seznamu.
 * 
 * @param ptr struktura spojového seznamu
 * @param node existující uzel
 */
void remove_node(linked_list_t *ptr, linked_list_node_t *node) {
    if (node->previous == NULL) {
        ptr->first = node->next;
    }
    else {
        node->previous->next = node->next;
    }

    if (node->next == NULL) {
        ptr->last = node->previous;
    }
    else {
        node->next->previous = node->previous;
    }
}

/**
 * Přidá hodnotu jako prvek do předaného spojového seznamu.
 * 
 * @param ptr struktura spojového seznamu
 * @param e hodnota nového prvku
 * 
 * @return true
 */
bool add_to_list(linked_list_t *ptr, void *e) {
    linked_list_node_t *newNode = malloc(sizeof(linked_list_node_t));
    newNode->value = e;
    add_last(ptr, newNode);
    ptr->count++;

    return true;
}

/**
 * Získá hodnotu prvku seznamu na předané pozici.
 * 
 * @param ptr struktura spojového seznamu
 * @param index pozice prvku
 * 
 * @return hodnota prvku na předané pozici
 */
void *get_from_list(linked_list_t *ptr, int32_t index) {
    linked_list_node_t *node = ptr->first;

    int32_t i;
    for (i = 0; i < index; i++) {
        node = node->next;
    }

    return node->value;
}

/**
 * Změní hodnotu prvku seznamu na předané pozici.
 * 
 * @param ptr struktura spojového seznamu
 * @param index pozice prvku
 * @param element nová hodnota prvku
 * 
 * @return původní hodnota prvku
 */
void *set_in_list(linked_list_t *ptr, int32_t index, void *element) {
    linked_list_node_t *original;

    if (index < 1) {
        original = ptr->first->value;
        ptr->first->value = element;
    }
    else {
        linked_list_node_t *node = ptr->first;

        int32_t i;
        for (i = 0; i < index; i++) {
            node = node->next;
        }

        original = node->value;
        node->value = element;
    }

    return original;
}

/**
 * Odstraní prvek seznamu na předané pozici.
 * 
 * @param ptr struktura spojového seznamu
 * @param index pozice prvku
 * 
 * @return hodnota odstraněného prvku
 */
void *remove_from_list(linked_list_t *ptr, int32_t index) {
    linked_list_node_t *removed;
    linked_list_node_t *node;

    if (index < 1) {
        node = ptr->first;
        removed = node->value;
        remove_node(ptr, node);
        free(node);
    }
    else {
        node = ptr->first;

        int32_t i;
        for (i = 0; i < index; i++) {
            node = node->next;
        }

        removed = node->value;
        remove_node(ptr, node);
        free(node);
    }

    ptr->count--;

    return removed;
}

/**
 * Určí počet prvků seznamu.
 * 
 * @param ptr struktura spojového seznamu
 * @return počet prvků seznamu
 */
int32_t size_of_list(linked_list_t *ptr) {
    return ptr->count;
}

/**
 * Ověří, zda je seznam prázný.
 * 
 * @param ptr struktura spojového seznamu
 * @return true, je-li seznam prázdný, jinak false
 */
bool is_list_empty(linked_list_t *ptr) {
    return (ptr->count == 0);
}

/**
 * Odstraní všechny uzly spojového seznamu.
 * 
 * @param ptr struktura spojového seznamu
 */
void clear_list(linked_list_t *ptr) {
    linked_list_node_t *node;
    ptr->count = 0;

    while (ptr->first != NULL) {
        node = ptr->first;
        remove_node(ptr, node);
        free(node);
    }
}
