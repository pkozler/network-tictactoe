/* 
 * Author: Petr Kozler
 */

#ifndef OBSERVABLE_LIST_H
#define OBSERVABLE_LIST_H

#include <stdbool.h>

typedef struct {
    linked_list_t *list;
    pthread_t thread;
    pthread_mutex_t lock;
    bool changed;
} observable_list_t;

#endif /* OBSERVABLE_LIST_H */
