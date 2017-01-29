/* 
 * Modul request_parser definuje funkce pro příjem a zpracování požadavků
 * klienta.
 * 
 * Author: Petr Kozler
 */

#include "request_parser.h"
#include "config.h"
#include "protocol.h"
#include "global.h"
#include "player.h"
#include "game.h"
#include "request_checker.h"
#include "player_list.h"
#include "game_list.h"
#include "game_logic.h"
#include "communicator.h"
#include <string.h>
#include <sys/socket.h>

/**
 * Vytvoří zprávu s potvrzením požadavku.
 * 
 * @param message požadavek
 * @param player klient
 * @return odpověď
 */
message_t *create_ack_message(message_t *message, player_t *player) {
    message_t *new_message = create_message(message->type, MSG_ACK_ARGC);
    put_string_arg(new_message, MSG_TRUE);
    
    return new_message;
}

/**
 * Vytvoří zprávu s odmítnutím požadavku.
 * 
 * @param message požadavek
 * @param err_msg typ chyby
 * @param player klient
 * @return odpověď
 */
message_t *create_err_message(message_t *message, char *err_msg, player_t *player) {
    message_t *new_message = create_message(message->type, MSG_ERR_ARGC);
    put_string_arg(new_message, MSG_FALSE);
    put_string_arg(new_message, err_msg);
    
    return new_message;
}

/**
 * Zpracuje požadavek na přihlášení.
 * 
 * @param message požadavek
 * @param player klient
 * @return odpověď
 */
message_t *handle_login_request(message_t *message, player_t *player) {
    if (message->argc != MSG_LOGIN_CLIENT_ARGC) {
        return create_err_message(message, MSG_ERR_INVALID_ARG_COUNT, player);
    }
    
    char *nick = get_string_arg(message);
    
    // neplatný nick
    if (!is_name_valid(nick)) {
        return create_err_message(message, MSG_ERR_INVALID_NAME, player);
    }
    
    lock_player_list();
    player_t *existing_player = get_player_by_name(nick);

    // hráč se zadaným nickem existuje a je momentálně připojen
    if (existing_player != NULL && existing_player->socket->connected) {
        unlock_player_list();

        return create_err_message(message, MSG_ERR_EXISTING_NAME, player);
    }
    
    player->nick = nick;
    
    // hráč je nový
    if (existing_player == NULL) {
        // přidání do seznamu přihlášených s přidělením ID
        add_player_to_list(player);
        set_player_list_changed(true);
        unlock_player_list();
    }
    // hráč byl přihlášen a právě obnovil přerušené spojení
    else {
        // navrácení do seznamu s obnovou předchozích údajů
        return_player_to_list(existing_player, player);
        set_player_list_changed(true);
        unlock_player_list();
        
        game_t *last_game = player->current_game;
        
        // pokus o návrat do předchozí herní místnosti
        if (last_game != NULL) {
            lock_game(last_game);
            return_player_to_game(player);
            
            // návrat neúspěšný (místnost obsadili jiní hráči)
            if (player->current_game == NULL) {
                unlock_game(last_game);
            }
            // návrat úspěšný
            else {
                set_game_changed(last_game, true);
                unlock_game(last_game);
                
                lock_game_list();
                set_game_list_changed(true);
                unlock_game_list();
            }
        }
    }
    
    message_t *new_message = create_message(message->type, MSG_LOGIN_CLIENT_ID_ARGC);
    put_string_arg(new_message, MSG_TRUE);
    put_int_arg(new_message, player->id);
    
    return new_message;
}

/**
 * Zpracuje požadavek na odhlášení.
 * 
 * @param message požadavek
 * @param player klient
 * @return odpověď
 */
message_t *handle_logout_request(message_t *message, player_t *player) {
    if (message->argc != MSG_LOGOUT_CLIENT_ARGC) {
        return create_err_message(message, MSG_ERR_INVALID_ARG_COUNT, player);
    }
    
    lock_player_list();
    
    if (!is_player_logged(player)) {
        unlock_player_list();
        
        return create_err_message(message, MSG_ERR_NOT_LOGGED, player);
    }
    
    unlock_player_list();
    
    // vystoupení z aktuální herní místnosti
    if (is_player_in_game_room(player)) {
        lock_game(player->current_game);
        
        game_t *game = player->current_game;
        remove_player_from_game(player);
        
        set_game_changed(game, true);
        unlock_game(game);
        
        lock_game_list();
        set_game_list_changed(true);
        unlock_game_list();
    }
    
    // odstranění ze seznamu přihlášených
    lock_player_list();
    remove_player_by_id(player->id);
    set_player_list_changed(true);
    unlock_player_list();
    
    return create_ack_message(message, player);
}

/**
 * Zpracuje požadavek na vytvoření hry.
 * 
 * @param message požadavek
 * @param player klient
 * @return odpověď
 */
message_t *handle_create_game_request(message_t *message, player_t *player) {
    if (message->argc != MSG_CREATE_GAME_ARGC) {
        return create_err_message(message, MSG_ERR_INVALID_ARG_COUNT, player);
    }
    
    char *name = get_string_arg(message);
    int8_t player_count = get_byte_arg(message);
    int8_t board_size = get_byte_arg(message);
    int8_t cell_count = get_byte_arg(message);
    message_t *new_message;

    // neplatné jméno
    if (!is_name_valid(name)) {
        return create_err_message(message, MSG_ERR_INVALID_NAME, player);
    }
    
    lock_game_list();
    
    // existující jméno
    if (get_game_by_name(name) != NULL) {
        unlock_game_list();
        
        return create_err_message(message, MSG_ERR_EXISTING_NAME, player);
    }
    
    unlock_game_list();
    
    if (!is_game_player_count_valid(player_count)) {
        new_message = create_message(message->type, MSG_ERR_INVALID_PLAYER_COUNT_ARGC);
        put_string_arg(new_message, MSG_FALSE);
        put_string_arg(new_message, MSG_ERR_INVALID_PLAYER_COUNT);
        put_byte_arg(new_message, MIN_PLAYERS_SIZE);
        put_byte_arg(new_message, MAX_PLAYERS_SIZE);
        
        return new_message;
    }
    
    if (!is_game_board_size_valid(board_size)) {
        new_message = create_message(message->type, MSG_ERR_INVALID_BOARD_SIZE_ARGC);
        put_string_arg(new_message, MSG_FALSE);
        put_string_arg(new_message, MSG_ERR_INVALID_BOARD_SIZE);
        put_byte_arg(new_message, MIN_BOARD_SIZE);
        put_byte_arg(new_message, MAX_BOARD_SIZE);
        
        return new_message;
    }
    
    if (!is_game_cell_count_valid(cell_count)) {
        new_message = create_message(message->type, MSG_ERR_INVALID_CELL_COUNT_ARGC);
        put_string_arg(new_message, MSG_FALSE);
        put_string_arg(new_message, MSG_ERR_INVALID_CELL_COUNT);
        put_byte_arg(new_message, MIN_CELL_COUNT);
        put_byte_arg(new_message, MAX_CELL_COUNT);
        
        return new_message;
    }
    
    game_t *game = create_game(player, name, board_size, player_count, cell_count);
    lock_game(game);
    add_player_to_game(game, player);
    set_game_changed(game, true);
    unlock_game(game);
    
    lock_game_list();
    add_game_to_list(game);
    set_game_list_changed(true);
    unlock_game_list();

    new_message = create_message(message->type, MSG_CREATE_GAME_ID_ARGC);
    put_string_arg(new_message, MSG_TRUE);
    put_int_arg(new_message, player->current_game->id);
    
    return new_message;
}

/**
 * Zpracuje požadavek na připojení ke hře.
 * 
 * @param message požadavek
 * @param player klient
 * @return odpověď
 */
message_t *handle_join_game_request(message_t *message, player_t *player) {
    if (message->argc != MSG_JOIN_GAME_ARGC) {
        return create_err_message(message, MSG_ERR_INVALID_ARG_COUNT, player);
    }
    
    int32_t game_id = get_int_arg(message);
    
    if (!is_id_valid(game_id)) {
        return create_err_message(message, MSG_ERR_INVALID_ID, player);
    }
    
    lock_game_list();
    game_t *game = get_game_by_id(game_id);
    
    if (game == NULL) {
        unlock_game_list();
        
        return create_err_message(message, MSG_ERR_ID_NOT_FOUND, player);
    }
    
    unlock_game_list();
    lock_game(game);
    
    if (is_player_in_game_room(player)) {
        unlock_game(game);
        
        return create_err_message(message, MSG_ERR_ALREADY_IN_ROOM, player);
    }
    
    if (is_game_full(game)) {
        unlock_game(game);
        
        return create_err_message(message, MSG_ERR_ROOM_FULL, player);
    }
    
    add_player_to_game(game, player);
    set_game_changed(game, true);
    unlock_game(game);
    
    lock_game_list();
    set_game_list_changed(true);
    unlock_game_list();
    
    return create_ack_message(message, player);
}

/**
 * Zpracuje požadavek na opuštění hry.
 * 
 * @param message požadavek
 * @param player klient
 * @return odpověď
 */
message_t *handle_leave_game_request(message_t *message, player_t *player) {
    if (message->argc != MSG_LEAVE_GAME_ARGC) {
        return create_err_message(message, MSG_ERR_INVALID_ARG_COUNT, player);
    }
    
    if (!is_player_in_game_room(player)) {
        return create_err_message(message, MSG_ERR_NOT_IN_ROOM, player);
    }
    
    lock_game(player->current_game);
    
    game_t *game = player->current_game;
    remove_player_from_game(player);
    
    set_game_changed(game, true);
    unlock_game(game);
    
    lock_game_list();
    set_game_list_changed(true);
    unlock_game_list();
    
    return create_ack_message(message, player);
}

/**
 * Zpracuje požadavek na zahájení kola hry.
 * 
 * @param message požadavek
 * @param player klient
 * @return odpověď
 */
message_t *handle_start_game_request(message_t *message, player_t *player) {
    if (message->argc != MSG_START_GAME_ARGC) {
        return create_err_message(message, MSG_ERR_INVALID_ARG_COUNT, player);
    }
    
    if (!is_player_in_game_room(player)) {
        return create_err_message(message, MSG_ERR_NOT_IN_ROOM, player);
    }
    
    lock_game(player->current_game);
    
    if (is_round_started(player->current_game)) {
        unlock_game(player->current_game);
        
        return create_err_message(message, MSG_ERR_ROUND_ALREADY_STARTED, player);
    }
    
    if (!has_game_enough_players(player->current_game)) {
        unlock_game(player->current_game);
        
        return create_err_message(message, MSG_ERR_NOT_ENOUGH_PLAYERS, player);
    }
    
    start_next_round(player->current_game);
    set_game_changed(player->current_game, true);
    unlock_game(player->current_game);
    
    return create_ack_message(message, player);
}

/**
 * Zpracuje požadavek na provedení tahu ve hře.
 * 
 * @param message požadavek
 * @param player klient
 * @return odpověď
 */
message_t *handle_play_game_request(message_t *message, player_t *player) {
    if (message->argc != MSG_PLAY_GAME_ARGC) {
        return create_err_message(message, MSG_ERR_INVALID_ARG_COUNT, player);
    }
    
    int8_t x = get_byte_arg(message);
    int8_t y = get_byte_arg(message);
    
    if (!is_player_in_game_room(player)) {
        return create_err_message(message, MSG_ERR_NOT_IN_ROOM, player);
    }
    
    lock_game(player->current_game);
    
    if (!is_round_started(player->current_game)) {
        unlock_game(player->current_game);
        
        return create_err_message(message, MSG_ERR_ROUND_NOT_STARTED, player);
    }
    
    if (!is_current_player(player->current_game, player)) {
        unlock_game(player->current_game);
        
        return create_err_message(message, MSG_ERR_NOT_ON_TURN, player);
    }
    
    if (!is_in_board_size(player->current_game, x, y)) {
        unlock_game(player->current_game);
        
        return create_err_message(message, MSG_ERR_CELL_OUT_OF_BOARD, player);
    }
    
    if (!can_play(player->current_game, x, y)) {
        unlock_game(player->current_game);
        
        return create_err_message(message, MSG_ERR_CELL_OCCUPIED, player);
    }
    
    play(player->current_game, player->current_game_index, x, y);
    int8_t winner = player->current_game->current_winner;
    set_game_changed(player->current_game, true);
    unlock_game(player->current_game);
    
    if (winner > 0) {
        lock_player_list();
        set_player_list_changed(true);
        unlock_player_list();
    }
    
    return create_ack_message(message, player);
}

/**
 * Zpracuje neznámý požadavek.
 * 
 * @param message požadavek
 * @param player klient
 * @return odpověď
 */
message_t *handle_unknown_request(message_t *message, player_t *player) {
    message_t *new_message = create_message(message->type, MSG_ACK_ARGC);
    put_string_arg(new_message, MSG_FALSE);
    
    return new_message;
}

/**
 * Zpracuje testování odezvy.
 * 
 * @param player klient
 * @return odpověď
 */
message_t *handle_ping(player_t *player) {
    message_t *new_message = create_message(NULL, 0);
    
    return new_message;
}

/**
 * Provede pokus o přihlášení klienta.
 * 
 * @param message požadavek
 * @param player klient
 * @return odpověď
 */
message_t *handle_unlogged_client_message(message_t *message, player_t *player) {
    // odeslána chybová odpověď, pokud je klient již aktivní
    if (is_player_logged(player)) {
        return create_err_message(message, MSG_ERR_ALREADY_LOGGED, player);
    }
    // aktivace klienta, pokud není aktivní
    else {
        return handle_login_request(message, player);
    }
}

/**
 * Provede pokus o zpracování požadavku přihlášeného klienta.
 * 
 * @param message požadavek
 * @param player klient
 * @return odpověď
 */
message_t *handle_logged_player_message(message_t *message, player_t *player) {
    // odeslána chybová odpověď, pokud klient není aktivní
    if (!is_player_logged(player)) {
        return create_err_message(message, MSG_ERR_NOT_LOGGED, player);
    }
    // zpracování požadavku, pokud je klient aktivní
    else {
        // požadavek na deaktivaci klienta
        if (!strcmp(message->type, MSG_LOGOUT_CLIENT)) {
            return handle_logout_request(message, player);
        }
        // požadavek na založení herní místnosti
        else if (!strcmp(message->type, MSG_CREATE_GAME)) {
            return handle_create_game_request(message, player);
        }
        // požadavek na vstup do herní místnosti
        else if (!strcmp(message->type, MSG_JOIN_GAME)) {
            return handle_join_game_request(message, player);
        }
        // požadavek na opuštění herní místnosti
        else if (!strcmp(message->type, MSG_LEAVE_GAME)) {
            return handle_leave_game_request(message, player);
        }
        // požadavek na zahájení nové hry
        else if (!strcmp(message->type, MSG_START_GAME)) {
            return handle_start_game_request(message, player);
        }
        // požadavek na herní tah v místnosti
        else if (!strcmp(message->type, MSG_PLAY_GAME)) {
            return handle_play_game_request(message, player);
        }
        // neznámý požadavek - bude odeslána chybová odpověď
        else {
            return handle_unknown_request(message, player);
        }
    }
}

/**
 * Přijme a zpracuje požadavek klienta.
 * 
 * @param player klient
 */
void handle_received_message(player_t *player) {
    message_t *request = receive_message(player->socket);
    
    // detekováno přerušení spojení - klient bude odstraněn
    if (!player->socket->connected) {
        return;
    }
    
    // příchozí zpráva neplatná - bude ignorována
    if (request == NULL) {
        return;
    }
    
    message_t *response = NULL;
    
    // příchozí zpráva je test odezvy - bude odeslána odpověď (prázdná zpráva)
    if (request->type == NULL) {
        response = handle_ping(player);
    }
    // příchozí zpráva je požadavek
    else {
        // požadavek na aktivaci klienta
        if (!strcmp(request->type, MSG_LOGIN_CLIENT)) {
            response = handle_unlogged_client_message(request, player);
        }
        // jiný požadavek
        else {
            response = handle_logged_player_message(request, player);
        }
    }
    
    send_message(response, player->socket);
    delete_message(request);
    
    if (response != NULL) {
        delete_message(response);
    }
}
