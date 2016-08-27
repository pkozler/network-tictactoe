/* 
 * Author: Petr Kozler
 */

#include "player.h"
#include "err.h"

/*
 * Pomocné funkce pro operace s hráči:
 */

void lock_player(player_t *player) {
    pthread_mutex_lock(&(player->lock));
}

void unlock_player(player_t *player) {
    pthread_mutex_unlock(&(player->lock));
}

bool is_player_nick_valid(char *nick) {
    // TODO zkontrolovat format nicku
}

/*
 * Funkce pro příjem a zpracovávání jednotlivých typů požadavků hráče (klienta):
 */

void handle_activation_request(player_t *player) {
    while (player->nick == NULL) {
        char *nick;
        // TODO prijmout nickname
        
        if (get_from_list_by_name(g_player_list, nick) != NULL) {
            // TODO odeslat chybu (jmeno existuje)
        }
        else if (!is_player_nick_valid(nick)) {
            // TODO odeslat chybu (jmeno neplatne)
        }
        else {
            player->nick = nick;
            add_to_list(g_player_list, (void *) player);
            // TODO odeslat ID
        }
    }
    
    player->active = true;
}

void handle_deactivation_request(player_t *player) {
    // TODO implementovat (prijmout zpravu o odpojeni, odebrat hrace ze seznamu a odstranit ho)
}

void handle_create_game_request(player_t *player) {
    // TODO implementovat
}

void handle_join_game_request(player_t *player) {
    // TODO implementovat
}

void handle_leave_game_request(player_t *player) {
    // TODO implementovat
}

void handle_restart_game_request(player_t *player) {
    // TODO implementovat
}

void handle_play_game_request(player_t *player) {
    // TODO implementovat
}

void parse_received_message(player_t *player) {
    message_t *message = receive_message(player->sock);
    // TODO precist zpravu a provest prikaz
    
    delete_message(message);
}

/*
 * Základní funkce pro vytvoření, běh a odstranění hráče:
 */

void run_player(void *arg) {
    player_t *player = (player_t *) arg;

    while (true) {
        lock_player(player);
        parse_received_message(player);
        unlock_player(player);
    }
}

void create_player(int sock) {
    player_t *player = (player_t *) malloc(sizeof(player_t));
    pthread_mutex_init(&player->lock, NULL);
    player->sock = sock;
    player->id = 0;
    player->nick = NULL;
    player->current_game = NULL;
    player->index = 0;
    player->active = false;
    
    if (pthread_create(&player->thread, NULL, run_game, (void *) player) < 0) {
        die("Chyba při spouštění vlákna pro příjem zpráv klienta");
    }
}

void delete_player(player_t *player) {
    pthread_cancel(player->thread);
    pthread_mutex_destroy(&(player->lock));
    close(player->sock);
    free(player);
}

/*
 * Funkce pro použití ve funkcích spojového seznamu hráčů:
 */

int32_t get_player_key(void *item) {
    player_t *player = (player_t *) item;
    
    return player->id;
}

void set_player_key(void *item, int32_t id) {
    player_t *player = (player_t *) item;
    player->id = id;
}

char *get_player_name(void *item) {
    player_t *player = (player_t *) item;
    
    return player->nick;
}

message_t *player_to_msg(void *item) {
    // TODO implementovat
}

message_t *player_list_to_msg() {
    // TODO implementovat
}

/*
 * Funkce pro vytvoření a odstranění seznamu hráčů:
 */

void create_player_list() {
    g_player_list = create_list("seznam hráčů", get_player_key, set_player_key,
            get_player_name, player_to_msg, player_list_to_msg);
}

void delete_player_list() {
    lock_list(g_player_list);
    list_iterator_t *iterator = create_list_iterator(g_player_list);
    player_t *player = (player_t *) get_next_item(iterator);
    
    while (player != NULL) {
        delete_player(player);
        player = (player_t *) get_next_item(iterator);
    }
    
    free(iterator);
    unlock_list(g_player_list, false);
    delete_list(g_player_list);
}