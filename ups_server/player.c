/* 
 * Modul player definuje funkce pro vytvoření a odstranění hráče
 * a pro příjem požadavků hráče.
 * 
 * Author: Petr Kozler
 */

#include "player.h"
#include "global.h"
#include "protocol.h"
#include "client_socket.h"
#include "logger.h"
#include "config.h"
#include "request_checker.h"
#include "game_logic.h"
#include "player_list.h"
#include "broadcaster.h"
#include "request_parser.h"
#include "player_list_sender.h"
#include "game_list_sender.h"
#include "message_list.h"
#include "status_cleaner.h"
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

/**
 * Přijímá a zpracovává požadavky hráče.
 * 
 * @param arg struktura hráče
 * @return null
 */
void *run_player(void *arg) {
    player_t *player = (player_t *) arg;

    send_message_list(player_list_to_message_list(), player->socket);
    send_message_list(game_list_to_message_list(), player->socket);
    
    while (player->socket->connected) {
        handle_received_message(player);
    }
    
    print_out("Klient %d: Odpojen", player->socket->sock);
    handle_disconnected_player(player);
    
    return NULL;
}

/**
 * Vytvoří strukturu hráče.
 * 
 * @param sock deskriptor socketu
 * @return struktura hráče
 */
player_t *create_player(int sock) {
    client_socket_t *socket = (client_socket_t *) malloc(sizeof(client_socket_t));
    pthread_mutex_init(&socket->lock, NULL);
    socket->sock = sock;
    socket->connected = true;
    
    player_t *player = (player_t *) malloc(sizeof(player_t));
    player->id = 0;
    player->nick = NULL;
    player->current_game = NULL;
    player->current_game_index = 0;
    player->current_game_score = 0;
    player->total_score = 0;
    player->socket = socket;
    
    if (pthread_create(&player->thread, NULL, run_player, (void *) player) < 0) {
        print_err("Chyba při spouštění vlákna pro příjem zpráv klienta");
    }
    
    return player;
}

/**
 * Odstraní strukturu hráče.
 * 
 * @param player struktura hráče
 */
void delete_player(player_t *player) {
    pthread_cancel(player->thread);
    pthread_mutex_destroy(&(player->socket->lock));
    free(player->socket);
    free(player);
}
