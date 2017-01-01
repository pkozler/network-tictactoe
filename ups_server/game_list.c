/* 
 * Author: Petr Kozler
 */

#include "game_list.h"
#include "global.h"
#include "protocol.h"
#include "observable_list.h"
#include "linked_list_iterator.h"
#include "broadcaster.h"
#include <stdlib.h>

/*
 * Funkce pro vytvoření seznamu her:
 */

void create_game_list() {
    g_game_list = (observable_list_t *) malloc(sizeof(observable_list_t));
    g_game_list->list = create_linked_list();
    g_game_list.changed = true;
    
    if (pthread_mutex_init(&(g_game_list.lock), NULL) < 0) {
        print_err("Chyba při vytváření zámku pro seznam her");
    }
    
    if (pthread_create(&(g_game_list.thread), NULL, run_game_list_observer, &g_game_list) < 0) {
        print_err("Chyba při vytváření vlákna pro seznam her");
    }
}

void delete_game_list() {
    pthread_mutex_destroy(&(g_game_list->lock));
    delete_linked_list(g_game_list->list, delete_game);
    free(g_game_list);
}

void lock_game_list() {
    pthread_mutex_lock(&(g_game_list->lock));
}

void unlock_game_list(bool changed) {
    g_game_list->changed = changed;
    pthread_mutex_unlock(&(g_game_list->lock));
}

void add_game_to_list(game_t *game) {
    if (game == NULL) {
        return;
    }
    
    linked_list_iterator_t *iterator = create_iterator(g_game_list);
    game_t *current_game;
    int32_t max_id = 0;

    while (has_next_element(iterator)) {
        current_game = get_next_element(iterator);

        if (current_game->id > max_id) {
            max_id = current_game->id;
        }
    }

    game->id = max_id + 1;
    add_element(g_game_list->list, game);
}

game_t *get_game_by_id(int32_t id) {
    linked_list_iterator_t *iterator = create_iterator(g_game_list);
    game_t *current_game;

    while (has_next_element(iterator)) {
        current_game = get_next_element(iterator);

        if (current_game->id == id) {
            return current_game;
        }
    }
    
    return NULL;
}

game_t *get_game_by_name(char *name) {
    linked_list_iterator_t *iterator = create_iterator(g_game_list);
    game_t *current_game;

    while (has_next_element(iterator)) {
        current_game = get_next_element(iterator);

        if (current_game->name == name) {
            return current_game;
        }
    }
    
    return NULL;
}

game_t *remove_game_by_id(int32_t id) {
    linked_list_iterator_t *iterator = create_iterator(g_game_list);
    game_t *current_game;

    while (has_next_element(iterator)) {
        current_game = get_next_element(iterator);

        if (current_game->id == id) {
            remove_last_element(iterator);
            current_game->id = 0;
            
            return current_game;
        }
    }
    
    return NULL;
}

message_t *game_to_msg(game_t *game) {
    message_t *message = create_message(MSG_GAME_LIST_ITEM, MSG_GAME_LIST_ITEM_ARGC);
    put_int_arg(message, game->id);
    put_string_arg(message, game->name);
    put_byte_arg(message, game->player_count);
    put_byte_arg(message, game->board_size);
    put_byte_arg(message, game->cell_count);
    put_byte_arg(message, game->player_counter);
    
    return message;
}

message_t *game_list_to_msg() {
    message_t *message = create_message(MSG_GAME_LIST, MSG_GAME_LIST_ARGC);
    put_int_arg(message, count_elements(g_game_list->list));
    
    return message;
}

void send_game_list_update() {
    message_list_t message_list = create_message_list(
            game_list_to_msg(), count_elements(g_game_list));
    linked_list_iterator_t *iterator = create_iterator(g_game_list);
    game_t *current_game;
    
    int32_t i = 0;
    while (has_next_element(iterator)) {
        current_game = get_next_element(iterator);
        message_list->msgv[i++] = game_to_msg(current_game);
    }
    
    send_to_all_clients(message_list);
}

void *run_game_list_observer(void *arg) {
    while (is_server_running()) {
        if (g_game_list->changed) {
            lock_game_list();
            send_game_list_update();
            unlock_game_list(false);
        }
    }
    
    return NULL;
}