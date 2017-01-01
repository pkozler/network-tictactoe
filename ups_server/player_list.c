/* 
 * Author: Petr Kozler
 */

#include "player_list.h"
#include "global.h"
#include "protocol.h"
#include "observable_list.h"
#include "linked_list_iterator.h"
#include "broadcaster.h"
#include <stdlib.h>

/*
 * Funkce pro vytvoření seznamu hráčů:
 */

void create_player_list() {
    g_player_list = (observable_list_t *) malloc(sizeof(observable_list_t));
    g_player_list->list = create_linked_list();
    g_player_list->changed = true;
    
    if (pthread_mutex_init(&(g_player_list->lock), NULL) < 0) {
        print_err("Chyba při vytváření zámku pro seznam hráčů");
    }
    
    if (pthread_create(&(g_player_list->thread), NULL, run_player_list_observer, &g_player_list) < 0) {
        print_err("Chyba při vytváření vlákna pro seznam hráčů");
    }
}

void delete_player_list() {
    pthread_mutex_destroy(&(g_player_list->lock));
    delete_linked_list(g_player_list->list, NULL);
    free(g_player_list);
}

void lock_player_list() {
    pthread_mutex_lock(&(g_player_list->lock));
}

void unlock_player_list(bool changed) {
    g_player_list->changed = changed;
    pthread_mutex_unlock(&(g_player_list->lock));
}

void add_player_to_list(player_t *player) {
    if (player == NULL) {
        return;
    }
    
    linked_list_iterator_t *iterator = create_iterator(g_player_list);
    player_t *current_player;
    int32_t max_id = 0;

    while (has_next_element(iterator)) {
        current_player = get_next_element(iterator);

        if (current_player->id > max_id) {
            max_id = current_player->id;
        }
    }

    player->id = max_id + 1;
    add_element(g_player_list->list, player);
}

player_t *get_player_by_id(int32_t id) {
    linked_list_iterator_t *iterator = create_iterator(g_player_list);
    player_t *current_player;

    while (has_next_element(iterator)) {
        current_player = get_next_element(iterator);

        if (current_player->id == id) {
            return current_player;
        }
    }
    
    return NULL;
}

player_t *get_player_by_name(char *name) {
    linked_list_iterator_t *iterator = create_iterator(g_player_list);
    player_t *current_player;

    while (has_next_element(iterator)) {
        current_player = get_next_element(iterator);

        if (current_player->nick == name) {
            return current_player;
        }
    }
    
    return NULL;
}

player_t *remove_player_by_id(int32_t id) {
    linked_list_iterator_t *iterator = create_iterator(g_player_list);
    player_t *current_player;

    while (has_next_element(iterator)) {
        current_player = get_next_element(iterator);

        if (current_player->id == id) {
            remove_last_element(iterator);
            current_player->id = 0;
            
            return current_player;
        }
    }
    
    return NULL;
}

message_t *player_list_to_msg() {
    message_t *message = create_message(MSG_PLAYER_LIST, MSG_PLAYER_LIST_ARGC);
    put_int_arg(message, count_elements(g_player_list->list));
    
    return message;
}

message_t *player_to_msg(player_t *player) {
    message_t *message = create_message(MSG_PLAYER_LIST_ITEM, MSG_PLAYER_LIST_ITEM_ARGC);
    put_int_arg(message, player->id);
    put_string_arg(message, player->nick);
    
    return message;
}

void send_player_list_update() {
    message_list_t message_list = create_message_list(
            player_list_to_msg(), count_elements(g_player_list));
    linked_list_iterator_t *iterator = create_iterator(g_player_list);
    player_t *current_player;
    
    int32_t i = 0;
    while (has_next_element(iterator)) {
        current_player = get_next_element(iterator);
        message_list->msgv[i++] = player_to_msg(current_player);
    }
    
    send_to_all_clients(message_list);
}

void *run_player_list_observer(void *arg) {
    while (is_server_running()) {
        if (g_player_list->changed) {
            lock_player_list();
            send_player_list_update();
            unlock_player_list(false);
        }
    }
    
    return NULL;
}
