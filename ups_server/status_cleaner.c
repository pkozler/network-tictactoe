/* 
 * Author: Petr Kozler
 */

#include "global.h"
#include "status_cleaner.h"
#include "player_list.h"
#include "game_list.h"
#include "game_logic.h"
#include "request_checker.h"

/**
 * Zpracuje opuštění herní místnosti všemi hráči.
 * 
 * @param game místnost
 */
void handle_empty_game(game_t *game) {
    lock_game_list();
    remove_game_by_id(game->id);
    set_game_list_changed(true);
    unlock_game_list();
    
    delete_game(game);
}

/**
 * Zpracuje odpojení klienta.
 * 
 * @param player klient
 */
void handle_disconnected_player(player_t *player) {
    // uzavření socketu
    close(player->socket->sock);
    
    if (!is_player_logged(player)) {
        // kompletní odstranění odhlášeného a odpojeného hráče
        remove_element(g_client_list, player);
        delete_player(player);
        
        return;
    }
    
    // zachování v seznamech hráčů, pokud je hráč v okamžiku odpojení přihlášen
    lock_player_list();
    set_player_list_changed(true);
    unlock_player_list();
    
    if (!is_player_in_game_room(player)) {
        return;
    }
    
    // vystoupení z herní místnosti (údaje o poslední hře ve struktuře hráče zůstanou)
    lock_game(player->current_game);

    game_t *game = player->current_game;
    remove_player_from_game(player);

    set_game_changed(game, true);
    unlock_game(game);

    lock_game_list();
    set_game_list_changed(true);
    unlock_game_list();
}