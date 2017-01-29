/* 
 * Modul game_list definuje funkce pro vytvoření, odstranění a manipulaci
 * se seznamem her a pozorování seznamu.
 * 
 * Author: Petr Kozler
 */

#include "game_list.h"
#include "global.h"
#include "protocol.h"
#include "logger.h"
#include "server_control.h"
#include "game_list_sender.h"
#include "observable_list.h"
#include "linked_list_iterator.h"
#include "broadcaster.h"
#include <stdlib.h>
#include <string.h>

/**
 * Pozoruje seznam her a v případě změny rozešle zprávu klientům.
 * 
 * @param arg null
 * @return null
 */
void *run_game_list_observer(void *arg) {
    while (is_server_running()) {
        broadcast_game_list();
    }
    
    return NULL;
}

/**
 * Vytvoří seznam her.
 */
void create_game_list() {
    g_game_list = (observable_list_t *) malloc(sizeof(observable_list_t));
    g_game_list->list = create_linked_list();
    g_game_list->changed = false;
    
    if (pthread_mutex_init(&(g_game_list->lock), NULL) < 0) {
        print_err("Chyba při vytváření zámku pro seznam her");
    }
    
    if (pthread_create(&(g_game_list->thread), NULL, run_game_list_observer, &g_game_list) < 0) {
        print_err("Chyba při vytváření vlákna pro seznam her");
    }
}

/**
 * Odstraní seznam her.
 */
void delete_game_list() {
    pthread_mutex_destroy(&(g_game_list->lock));
    delete_linked_list(g_game_list->list, (dispose_func_t) delete_game);
    free(g_game_list);
}

/**
 * Přidá hru do seznamu.
 * 
 * @param game hra
 */
void add_game_to_list(game_t *game) {
    if (game == NULL) {
        return;
    }
    
    linked_list_iterator_t *iterator = create_iterator(g_game_list->list);
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

/**
 * Získá hru ze seznamu podle ID.
 * 
 * @param id ID hry
 * @return hra
 */
game_t *get_game_by_id(int32_t id) {
    linked_list_iterator_t *iterator = create_iterator(g_game_list->list);
    game_t *current_game;

    while (has_next_element(iterator)) {
        current_game = get_next_element(iterator);

        if (current_game->id == id) {
            return current_game;
        }
    }
    
    return NULL;
}

/**
 * Získá hru ze seznamu podle názvu.
 * 
 * @param name jméno hry
 * @return hra
 */
game_t *get_game_by_name(char *name) {
    linked_list_iterator_t *iterator = create_iterator(g_game_list->list);
    game_t *current_game;

    while (has_next_element(iterator)) {
        current_game = (game_t *) get_next_element(iterator);

        if (!strcmp(current_game->name, name)) {
            return current_game;
        }
    }
    
    return NULL;
}

/**
 * Odstraní hru ze seznamu podle ID.
 * 
 * @param id ID hry
 * @return hra
 */
game_t *remove_game_by_id(int32_t id) {
    linked_list_iterator_t *iterator = create_iterator(g_game_list->list);
    game_t *current_game;

    while (has_next_element(iterator)) {
        current_game = get_next_element(iterator);

        if (current_game->id == id) {
            remove_last_element(iterator);
            
            return current_game;
        }
    }
    
    return NULL;
}

/**
 * Uzamkne seznam her.
 */
void lock_game_list() {
    pthread_mutex_lock(&(g_game_list->lock));
}

/**
 * Odemkne seznam her.
 */
void unlock_game_list() {
    pthread_mutex_unlock(&(g_game_list->lock));
}

/**
 * Vrátí příznak změny v seznamu her.
 * 
 * @return příznak změny
 */
bool is_game_list_changed() {
    return g_game_list->changed;
}

/**
 * Nastaví příznak změny v seznamu her.
 * 
 * @param changed příznak změny
 */
void set_game_list_changed(bool changed) {
    g_game_list->changed = changed;
}