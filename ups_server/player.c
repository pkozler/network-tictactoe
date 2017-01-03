/* 
 * Author: Petr Kozler
 */

#include "player.h"
#include "global.h"
#include "protocol.h"
#include "printer.h"
#include "logger.h"
#include "config.h"
#include "checker.h"
#include "player_list.h"
#include "broadcaster.h"
#include "request_parser.h"
#include "player_list_sender.h"
#include "game_list_sender.h"
#include <stdlib.h>
#include <string.h>

player_t *create_player(int sock) {
    player_t *player = (player_t *) malloc(sizeof(player_t));
    pthread_mutex_init(&player->lock, NULL);
    player->sock = sock;
    player->id = 0;
    player->nick = NULL;
    player->current_game = NULL;
    player->current_game_index = 0;
    player->current_game_score = 0;
    player->total_score = 0;
    player->connected = true;
    
    if (pthread_create(&player->thread, NULL, run_player, (void *) player) < 0) {
        print_err("Chyba při spouštění vlákna pro příjem zpráv klienta");
    }
    
    return player;
}

void delete_player(player_t *player) {
    pthread_cancel(player->thread);
    pthread_mutex_destroy(&(player->lock));
    close(player->sock);
    free(player);
}

void lock_player(player_t *player) {
    pthread_mutex_lock(&(player->lock));
}

void unlock_player(player_t *player) {
    pthread_mutex_unlock(&(player->lock));
}

void *run_player(void *arg) {
    player_t *player = (player_t *) arg;

    send_message_list(player_list_to_message_list, player);
    send_message_list(game_list_to_message_list, player);
    
    while (player->connected) {
        lock_player(player);
        parse_received_message(player);
        unlock_player(player);
    }
    
    log("Odpojen klient s číslem socketu %d", player->sock);
    
    if (is_player_logged(player)) {
        remove_player_from_game(player);
        remove_player_by_id(player->id);
    }
    
    remove_element(g_client_list, player);
    delete_player(player);
    
    return NULL;
}
