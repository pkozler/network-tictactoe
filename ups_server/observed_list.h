/* 
 * Author: Petr Kozler
 */

#ifndef OBSERVED_LIST_H
#define OBSERVED_LIST_H

#include "message.h"
#include <stdbool.h>
#include <stdint.h>
#include <unistd.h>
#include <pthread.h>

typedef int32_t (*get_item_key_func_t)(void *item);
typedef char *(*get_item_name_func_t)(void *item);
typedef message_t *(*item_to_msg_func_t)(void *item);
typedef message_t *(*list_to_msg_func_t)();
typedef void (*set_item_key_func_t)(void *item, int32_t id);

typedef struct OBSERVED_LIST_NODE {
    struct OBSERVED_LIST_NODE *next;
    struct OBSERVED_LIST_NODE *previous;
    void *value;
} list_node_t;

typedef struct {
    char *label;
    int32_t count;
    list_node_t *first;
    list_node_t *last;
    bool changed;
    pthread_t thread;
    pthread_mutex_t lock;
    get_item_key_func_t get_item_key_func;
    set_item_key_func_t set_item_key_func;
    get_item_name_func_t get_item_name_func;
    item_to_msg_func_t item_to_message_func;
    list_to_msg_func_t list_to_message_func;
} observed_list_t;

typedef struct {
    list_node_t *current;
} list_iterator_t;

observed_list_t *create_list(char *label,
        get_item_key_func_t get_item_key_func,
        set_item_key_func_t set_item_key_func,
        get_item_name_func_t get_item_name_func,
        item_to_msg_func_t item_to_message_func,
        list_to_msg_func_t list_to_message_func);
void delete_list(observed_list_t *list);
void add_to_list(observed_list_t *list, void *item);
void lock_list(observed_list_t *list);
void unlock_list(observed_list_t *list, bool changed);
bool is_item_id_valid(int32_t id);
bool is_item_name_valid(char *name);
void *get_from_list_by_id(observed_list_t *list, int32_t id);
void *get_from_list_by_name(observed_list_t *list, char *name);
void *remove_from_list_by_id(observed_list_t *list, int32_t id);
int32_t count_list_messages(observed_list_t *list);
message_t **list_to_messages(observed_list_t *list, int32_t count);
list_iterator_t *create_list_iterator(observed_list_t *list);
void *get_next_item(list_iterator_t *iterator);

#endif /* OBSERVED_LIST_H */