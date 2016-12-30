/* 
 * Author: Petr Kozler
 */

#include "observed_list.h"
#include "config.h"
#include "broadcast.h"
#include "printer.h"
#include "tcp_server_info.h"
#include <string.h>
#include <stdlib.h>

void lock_list(observed_list_t *list) {
    pthread_mutex_lock(&(list->lock));
}

void unlock_list(observed_list_t *list, bool changed) {
    list->changed = changed;
    pthread_mutex_unlock(&(list->lock));
}

bool is_item_id_valid(int32_t id) {
    return id > 0;
}

bool is_item_name_valid(char *name) {
    if (name == NULL) {
        return false;
    }
    
    int32_t len = strlen(name);
    
    if (len < 1 || MAX_NAME_LENGTH < len) {
        return false;
    }
    
    int32_t i;
    for (i = 0; name[i] != '\0'; i++) {
        if (   !(name[i] >= 'a' || name[i] <= 'z')
            && !(name[i] >= 'A' || name[i] <= 'Z')
            && !(name[i] >= '0' || name[i] <= '9')
            && !(name[i] == '-' || name[i] == '_')) {
            return false;
        }
    }
    
    return true;
}

void add_after_node(observed_list_t *list, list_node_t *node, list_node_t *new_node) {
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

void add_before_node(observed_list_t *list, list_node_t *node, list_node_t *new_node) {
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

void add_first_node(observed_list_t *list, list_node_t *new_node) {
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

void add_last_node(observed_list_t *list, list_node_t *new_node) {
    if (list->last == NULL) {
        add_first_node(list, new_node);
    }
    else {
        add_after_node(list, list->last, new_node);
    }
}

void remove_node(observed_list_t *list, list_node_t *node) {
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
}

void add_to_list(observed_list_t *list, void *item) {
    list_node_t *new_node = malloc(sizeof(list_node_t));
    new_node->value = item;
    
    if (list->count == 0) {
        list->set_item_key_func(new_node->value, 1);
        add_first_node(list, new_node);
    }
    else {
        list_node_t *node;
        int32_t id = 1;

        for (node = list->first; node != NULL; node = node->next) {
            if (list->get_item_key_func(node->value) == id) {
                id++;
            }
            else {
                list->set_item_key_func(new_node->value, id);
                add_before_node(list, node, new_node);
                
                break;
            }
        }
        
        if (node == NULL) {
            list->set_item_key_func(new_node->value, id);
            add_last_node(list, new_node);
        }
    }
    
    list->count++;
}

void *get_from_list_by_id(observed_list_t *list, int32_t id) {
    list_node_t *node;

    for (node = list->first; node != NULL; node = node->next) {
        if (list->get_item_key_func(node->value) == id) {
            return node->value;
        }
    }
    
    return NULL;
}

void *get_from_list_by_name(observed_list_t *list, char *name) {
    list_node_t *node;

    for (node = list->first; node != NULL; node = node->next) {
        if (list->get_item_name_func(node->value) == name) {
            return node->value;
        }
    }
    
    return NULL;
}

void *remove_from_list_by_id(observed_list_t *list, int32_t id) {
    list_node_t *node;

    for (node = list->first; node != NULL; node = node->next) {
        if (list->get_item_key_func(node->value) == id) {
            remove_node(list, node);
            break;
        }
    }
    
    list->count--;
    list->changed = true;
}

int32_t count_list_messages(observed_list_t *list) {
    return list->count + 1;
}

message_t **list_to_messages(observed_list_t *list, int32_t count) {
    message_t **messages = (message_t **) malloc(sizeof(message_t *) * count);
    messages[0] = list->list_to_message_func();
    
    list_node_t *node = list->first;
    
    int32_t i;
    for (i = 1; i < count; i++) {
        messages[i] = list->item_to_message_func(node->value);
    }
    
    return messages;
}

void send_list_update(observed_list_t *list) {
    int32_t count = count_list_messages(list);
    message_t **messages = list_to_messages(list, count);
    send_to_all_clients(count, messages);
    
    int32_t i;
    for (i = 0; i < count; i++) {
        delete_message(messages[i]);
    }
    
    free(messages);
}

void *run_list_observer(void *arg) {
    observed_list_t *list = (observed_list_t *) arg;
    
    while (is_server_running()) {
        if (list->changed) {
            lock_list(list);
            send_list_update(list);
            unlock_list(list, false);
        }
    }
    
    return NULL;
}

observed_list_t *create_list(char *label,
        get_item_key_func_t get_item_key_func,
        set_item_key_func_t set_item_key_func,
        get_item_name_func_t get_item_name_func,
        item_to_msg_func_t item_to_message_func,
        list_to_msg_func_t list_to_message_func) {
    observed_list_t *list = (observed_list_t *) malloc(sizeof(observed_list_t));
    list->get_item_key_func = get_item_key_func;
    list->get_item_name_func = get_item_name_func;
    list->item_to_message_func = item_to_message_func;
    list->list_to_message_func = list_to_message_func;
    list->set_item_key_func = set_item_key_func;
    list->label = label;
    list->first = NULL;
    list->last = NULL;
    list->count = 0;
    list->changed = true;
    
    if (pthread_mutex_init(&(list->lock), NULL) < 0) {
        print_err("Chyba při vytváření zámku pro %s", list->label);
    }
    
    if (pthread_create(&(list->thread), NULL, run_list_observer, list) < 0) {
        print_err("Chyba při vytváření vlákna pro %s", list->label);
    }
}

void delete_list(observed_list_t *list) {
    pthread_mutex_destroy(&(list->lock));
    
    list_node_t *node = list->first;

    while (node != NULL) {
        remove_node(list, node);
        node = list->first;
    }
    
    free(list);
}

list_iterator_t *create_list_iterator(observed_list_t *list) {
    list_iterator_t *iterator = (list_iterator_t *) malloc(sizeof(list_iterator_t));
    iterator->current = list->first;
}

void *get_next_item(list_iterator_t *iterator) {
    list_node_t *node = iterator->current;
    
    if (node == NULL) {
        return NULL;
    }
    
    iterator->current = node->next;
    
    return node->value;
}