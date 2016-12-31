/* 
 * Author: Petr Kozler
 */

#ifndef OBSERVABLE_LIST_H
#define OBSERVABLE_LIST_H

#include <stdint.h>
#include <stdbool.h>

typedef struct {
    linked_list_t *list;
    pthread_t thread;
    pthread_mutex_t lock;
    bool changed;
} observable_list_t;

bool is_id_valid(int32_t id);
bool is_name_valid(char *name);

#endif /* OBSERVABLE_LIST_H */
