/* 
 * Author: Petr Kozler
 */

#include "request_parser.h"
#include "config.h"
#include "protocol.h"
#include "game.h"
#include "checker.h"
#include "player_list.h"
#include "game_list.h"

message_t *create_ack_message(message_t *message, player_t *player) {
    message_t *new_message = create_message(message->type, MSG_ACK_ARGC);
    put_string_arg(new_message, MSG_TRUE);
    
    return new_message;
}

message_t *create_err_message(message_t *message, char *err_msg, player_t *player) {
    message_t *new_message = create_message(message->type, MSG_ERR_ARGC);
    put_string_arg(new_message, MSG_FALSE);
    put_string_arg(new_message, err_msg);
    
    return new_message;
}

message_t *handle_ping(player_t *player) {
    message_t *new_message = create_message(NULL, 0);
    
    return new_message;
}

message_t *handle_login_request(message_t *message, player_t *player) {
    if (message->argc != MSG_LOGIN_CLIENT_ARGC) {
        return create_err_message(message, MSG_ERR_INVALID_ARG_COUNT, player);
    }
    
    char *nick = get_string_arg(message);

    // neplatný nick
    if (!is_name_valid(nick)) {
        return create_err_message(message, MSG_ERR_INVALID_NAME, player);
    }
    
    // existující nick
    lock_player_list();
    
    if (get_player_by_name(g_player_list, nick) != NULL) {
        unlock_player_list(false);
        
        return create_err_message(message, MSG_ERR_EXISTING_NAME, player);
    }
    
    player->nick = nick;
    add_player_to_list(player);
    unlock_player_list(true);

    message_t *new_message = create_message(message->type, MSG_LOGIN_CLIENT_ID_ARGC);
    put_string_arg(new_message, MSG_TRUE);
    put_int_arg(new_message, player->id);
    
    return new_message;
}

message_t *handle_logout_request(message_t *message, player_t *player) {
    if (message->argc != MSG_LOGOUT_CLIENT_ARGC) {
        return create_err_message(message, MSG_ERR_INVALID_ARG_COUNT, player);
    }
    
    lock_player_list();
    remove_player_from_game(player);
    remove_player_by_id(player->id);
    unlock_player_list(true);
    
    return create_ack_message(message, player);
}

message_t *handle_create_game_request(message_t *message, player_t *player) {
    if (message->argc != MSG_CREATE_GAME_ARGC) {
        return create_err_message(message, MSG_ERR_INVALID_ARG_COUNT, player);
    }
    
    char *name = get_string_arg(message);
    int8_t board_size = get_byte_arg(message);
    int8_t player_count = get_byte_arg(message);
    int8_t cell_count = get_byte_arg(message);
    message_t *new_message;

    // neplatné jméno
    if (!is_name_valid(name)) {
        return create_err_message(message, MSG_ERR_INVALID_NAME, player);
    }
    
    lock_game_list();
    
    // existující jméno
    if (get_game_by_name(name) != NULL) {
        unlock_game_list(false);
        
        return create_err_message(message, MSG_ERR_EXISTING_NAME, player);
    }
    
    if (!is_game_board_size_valid(board_size)) {
        unlock_game_list(false);
        new_message = create_message(message->type, MSG_ERR_INVALID_BOARD_SIZE_ARGC);
        put_string_arg(new_message, MSG_FALSE);
        put_string_arg(new_message, MSG_ERR_INVALID_BOARD_SIZE);
        put_byte_arg(new_message, MIN_BOARD_SIZE);
        put_byte_arg(new_message, MAX_BOARD_SIZE);
        
        return new_message;
    }
    
    if (!is_game_player_count_valid(player_count)) {
        unlock_game_list(false);
        new_message = create_message(message->type, MSG_ERR_INVALID_PLAYER_COUNT_ARGC);
        put_string_arg(new_message, MSG_FALSE);
        put_string_arg(new_message, MSG_ERR_INVALID_PLAYER_COUNT);
        put_byte_arg(new_message, MIN_PLAYERS_SIZE);
        put_byte_arg(new_message, MAX_PLAYERS_SIZE);
        
        return new_message;
    }
    
    if (!is_game_cell_count_valid(cell_count)) {
        unlock_game_list(false);
        new_message = create_message(message->type, MSG_ERR_INVALID_CELL_COUNT_ARGC);
        put_string_arg(new_message, MSG_FALSE);
        put_string_arg(new_message, MSG_ERR_INVALID_CELL_COUNT);
        put_byte_arg(new_message, MIN_CELL_COUNT);
        put_byte_arg(new_message, MAX_CELL_COUNT);
        
        return new_message;
    }
    
    game_t *game = create_game(player, name, board_size, player_count, cell_count);
    add_game_to_list(game);
    unlock_game_list(true);

    new_message = create_message(message->type, MSG_CREATE_GAME_ID_ARGC);
    put_string_arg(new_message, MSG_TRUE);
    put_int_arg(new_message, player->current_game->id);
    
    return new_message;
}

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
        unlock_game_list(false);
        
        return create_err_message(message, MSG_ERR_ID_NOT_FOUND, player);
    }
    
    lock_game(game);
    
    if (game == player->current_game) {
        unlock_game(game, false);
        unlock_game_list(false);
        
        return create_err_message(message, MSG_ERR_ALREADY_IN_ROOM, player);
    }
    
    if (is_game_full(game)) {
        unlock_game(game, false);
        unlock_game_list(false);
        
        return create_err_message(message, MSG_ERR_ROOM_FULL, player);
    }
    
    add_player_to_game(game, player);
    unlock_game(game, true);
    unlock_game_list(true);
    
    return create_ack_message(message, player);
}

message_t *handle_leave_game_request(message_t *message, player_t *player) {
    if (message->argc != MSG_LEAVE_GAME_ARGC) {
        return create_err_message(message, MSG_ERR_INVALID_ARG_COUNT, player);
    }
    
    lock_game_list();
    
    if (player->current_game == NULL) {
        unlock_game_list(false);
        
        return create_err_message(message, MSG_ERR_NOT_IN_ROOM, player);
    }
    
    lock_game(player->current_game);
    remove_player_from_game(player->current_game, player);
    unlock_game(player->current_game, true);
    unlock_game_list(true);
    
    return create_ack_message(message, player);
}

message_t *handle_play_game_request(message_t *message, player_t *player) {
    if (message->argc != MSG_PLAY_GAME_ARGC) {
        return create_err_message(message, MSG_ERR_INVALID_ARG_COUNT, player);
    }
    
    int8_t x = get_byte_arg(message);
    int8_t y = get_byte_arg(message);
    
    lock_game_list();
    
    if (player->current_game == NULL) {
        unlock_game_list(false);
        
        return create_err_message(message, MSG_ERR_NOT_IN_ROOM, player);
    }
    
    lock_game(player->current_game);
    
    if (!is_game_active(player->current_game)) {
        unlock_game(player->current_game, false);
        unlock_game_list(false);
        
        return create_err_message(message, MSG_ERR_ROUND_NOT_STARTED, player);
    }
    
    if (!is_player_logged()) {
        unlock_game(player->current_game, false);
        unlock_game_list(false);
        
        return create_err_message(message, MSG_ERR_CANNOT_PLAY_IN_ROUND, player);
    }
    
    if (!is_current_player(player->current_game, player)) {
        unlock_game(player->current_game, false);
        unlock_game_list(false);
        
        return create_err_message(message, MSG_ERR_CANNOT_PLAY_NOW, player);
    }
    
    if (x < (int8_t) 0 || y < (int8_t) 0) {
        unlock_game(player->current_game, false);
        unlock_game_list(false);
        
        return create_err_message(message, MSG_ERR_INVALID_POSITION, player);
    }
    
    if (is_cell_in_board(player->current_game, x, y)) {
        unlock_game(player->current_game, false);
        unlock_game_list(false);
        
        return create_err_message(message, MSG_ERR_CELL_OUT_OF_BOARD, player);
    }
    
    if (!can_play(player->current_game, x, y)) {
        unlock_game(player->current_game, false);
        unlock_game_list(false);
        
        return create_err_message(message, MSG_ERR_CELL_OCCUPIED, player);
    }
    
    play(player->current_game, player->current_game_index, x, y);
    unlock_game(player->current_game, true);
    unlock_game_list(true);
    
    return create_ack_message(message, player);
}

message_t *handle_unknown_request(message_t *message, player_t *player) {
    message_t *new_message = create_message(message->type, MSG_ACK_ARGC);
    put_string_arg(new_message, MSG_FALSE);
    
    return new_message;
}

message_t *try_handle_login_request(message_t *message, player_t *player) {
    // odeslána chybová odpověď, pokud je klient již aktivní
    if (is_player_logged()) {
        return create_err_message(message, MSG_ERR_ALREADY_LOGGED, player);
    }
    // aktivace klienta, pokud není aktivní
    else {
        return handle_login_request(message, player);
    }
}

message_t *try_handle_client_request(message_t *message, player_t *player) {
    // odeslána chybová odpověď, pokud klient není aktivní
    if (!is_player_logged()) {
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

void parse_received_message(player_t *player) {
    message_t *request = receive_message(player->sock);
    message_t *response = NULL;
    
    // příchozí zpráva neplatná - bude ignorována
    if (request == NULL) {
        return;
    }
    
    // příchozí zpráva je test odezvy - bude odeslána odpověď (prázdná zpráva)
    if (request->type == NULL) {
        response = handle_ping(player);
    }
    // příchozí zpráva je požadavek
    else {
        // požadavek na aktivaci klienta
        if (!strcmp(request->type, MSG_LOGIN_CLIENT)) {
            response = try_handle_login_request(request, player);
        }
        // jiný požadavek
        else {
            response = try_handle_client_request(request, player);
        }
    }
    
    send_message(response, player->sock);
    delete_message(request);
    
    if (response != NULL) {
        delete_message(response);
    }
}