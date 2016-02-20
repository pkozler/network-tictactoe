#include "linkedlist.h"

#include <stdlib.h>
#include <stdio.h>

/*
    modul obsahující funkce pro vytváření spojového seznamu a
    vyhledávání a mazání prvků seznamu
*/

/**
    Porovnávací funkce pro hry podle ID.
*/
bool game_id_equals(void *game, int32_t game_id) {
    return (((game_t *)game)->game_id == game_id);
}

/**
    Porovnávací funkce pro hráče podle ID.
*/
bool thread_id_equals(void *thread, int32_t client_id) {
    return (((thread_t *)thread)->client_id == client_id);
}

/**
    Vytvoří nový uzel seznamu z předanou hodnotou.
*/
bool add_to_list(linked_list_t **head, void *val) {
    linked_list_t *new_node;
    new_node = calloc(1, sizeof(linked_list_t));

    if (new_node == NULL) {
        return false;
    }

    new_node->val = val;
    new_node->next = *head;
    *head = new_node;

    return true;
}

/**
    Přesune se na další uzel seznamu a vrátí jeho hodnotu.
*/
void *next_element(linked_list_t **current) {
    void *item = (*current)->val;
    *current = (*current)->next;
    return item;
}

/**
    Vyhledá prvek v seznamu podle předaného klíče a porovnávací funkce.
*/
void *get_from_list(linked_list_t *head, int32_t item_id, bool (*item_equals) (void *, int32_t)) {
    linked_list_t *current;

    for (current = head; current != NULL; current = current->next) {
        if (item_equals(current->val, item_id)) {
            return current->val;
        }
    }

    return NULL;
}

/**
    Odstraní ze seznamu prvek nalezený podle předaného klíče a porovnávací funkce.
*/
void remove_from_list(linked_list_t **head, int32_t item_id, bool (*item_equals) (void *, int32_t)) {
    linked_list_t *current = *head;
    linked_list_t *temp_node = NULL;

    if (item_equals((*head)->val, item_id)) {
        temp_node = (*head)->next;
        free(*head);
        *head = temp_node;

        return;
    }

    while (current->next != NULL) {
        if (item_equals(current->next->val, item_id)) {
            temp_node = current->next;
            current->next = temp_node->next;
            free(temp_node);

            return;
        }

        current = current->next;
    }
}

/**
    Určí velikost (počet uzlů) spojového seznamu.
*/
int32_t get_list_size(linked_list_t *head) {
    linked_list_t *current = head;
    int32_t count = 0;

    while (current != NULL) {
        count++;
        current = current->next;
    }

    return count;
}
