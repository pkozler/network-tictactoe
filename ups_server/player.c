/* 
 * Author: Petr Kozler
 */

#include <stdlib.h>

#include "player.h"
#include "err.h"
#include "config.h"

/*
 * Pomocné funkce pro operace s hráči:
 */

void lock_player(player_t *player) {
    pthread_mutex_lock(&(player->lock));
}

void unlock_player(player_t *player) {
    pthread_mutex_unlock(&(player->lock));
}

/*
 * Funkce pro příjem a zpracovávání jednotlivých typů požadavků hráče (klienta):
 */

message_t *create_ack_message(message_t *message, player_t *player) {
    message_t *new_message = create_message(message->type, MSG_ACK_ARGC);
    new_message[0] = MSG_OK;
    
    return new_message;
}

message_t *create_err_message(message_t *message, char *err_msg, player_t *player) {
    message_t *new_message = create_message(message->type, MSG_ERR_ARGC);
    new_message[0] = MSG_FAIL;
    new_message[1] = err_msg;
    
    return new_message;
}

message_t *handle_ping(player_t *player) {
    message_t *new_message = create_message(NULL, 0);
    
    return new_message;
}

message_t *handle_activation_request(message_t *message, player_t *player) {
    if (message->argc != MSG_ACTIVATE_CLIENT_ARGC) {
        return create_err_message(message, MSG_ERR_INVALID_ARG_COUNT, player);
    }
    
    char *nick = message->argv[0];

    // neplatný nick
    if (!is_item_name_valid(nick)) {
        return create_err_message(message, MSG_ERR_INVALID_NAME, player);
    }
    
    // existující nick
    lock_list(g_player_list);
    
    if (get_from_list_by_name(g_player_list, nick) != NULL) {
        unlock_list(g_player_list, false);
        
        return create_err_message(message, MSG_ERR_EXISTING_NAME, player);
    }
    
    player->nick = nick;
    add_to_list(g_player_list, (void *) player);
    player->active = true;
    unlock_list(g_player_list, true);

    message_t *new_message = create_message(message->type, MSG_ACTIVATE_CLIENT_ID_ARGC);
    new_message[0] = MSG_OK;
    new_message[1] = int_to_string(player->id);
    
    return new_message;
}

message_t *handle_deactivation_request(message_t *message, player_t *player) {
    if (message->argc != MSG_DEACTIVATE_CLIENT_ARGC) {
        return create_err_message(message, MSG_ERR_INVALID_ARG_COUNT, player);
    }
    
    lock_list(g_player_list);
    remove_from_list_by_id(player->id);
    unlock_list(g_player_list, true);
    player->active = false;
    
    return create_ack_message(message, player);
}

message_t *handle_create_game_request(message_t *message, player_t *player) {
    if (message->argc != MSG_CREATE_GAME_ARGC) {
        return create_err_message(message, MSG_ERR_INVALID_ARG_COUNT, player);
    }
    
    char *name = message->argv[0];
    int8_t board_size = string_to_byte(message->argv[1]);
    int8_t player_count = string_to_byte(message->argv[2]);
    int8_t cell_count = string_to_byte(message->argv[3]);
    message_t *new_message;

    // neplatné jméno
    if (!is_item_name_valid(name)) {
        return create_err_message(message, MSG_ERR_INVALID_NAME, player);
    }
    
    lock_list(g_game_list);
    
    // existující jméno
    if (get_from_list_by_name(g_game_list, name) != NULL) {
        unlock_list(g_game_list, false);
        
        return create_err_message(message, MSG_ERR_EXISTING_NAME, player);
    }
    
    if (!is_game_board_size_valid(board_size)) {
        unlock_list(g_game_list, false);
        new_message = create_message(message->type, MSG_ERR_INVALID_BOARD_SIZE_ARGC);
        new_message[0] = MSG_FAIL;
        new_message[1] = MSG_ERR_INVALID_BOARD_SIZE;
        new_message[2] = byte_to_string(MIN_BOARD_SIZE);
        new_message[3] = byte_to_string(MAX_BOARD_SIZE);
        
        return new_message;
    }
    
    if (!is_game_player_count_valid(player_count)) {
        unlock_list(g_game_list, false);
        new_message = create_message(message->type, MSG_ERR_INVALID_PLAYER_COUNT_ARGC);
        new_message[0] = MSG_FAIL;
        new_message[1] = MSG_ERR_INVALID_PLAYER_COUNT;
        new_message[2] = byte_to_string(MIN_PLAYERS_SIZE);
        new_message[3] = byte_to_string(MAX_PLAYERS_SIZE);
        
        return new_message;
    }
    
    if (!is_game_cell_count_valid(cell_count)) {
        unlock_list(g_game_list, false);
        new_message = create_message(message->type, MSG_ERR_INVALID_CELL_COUNT_ARGC);
        new_message[0] = MSG_FAIL;
        new_message[1] = MSG_ERR_INVALID_CELL_COUNT;
        new_message[2] = byte_to_string(MIN_CELL_COUNT);
        new_message[3] = byte_to_string(MAX_CELL_COUNT);
        
        return new_message;
    }
    
    create_game(player, name, board_size, player_count, cell_count);
    add_to_list(g_game_list, player->current_game);
    unlock_list(g_game_list, true);

    new_message = create_message(message->type, MSG_CREATE_GAME_ID_ARGC);
    new_message[0] = MSG_OK;
    new_message[1] = int_to_string(player->current_game->id);
    
    return new_message;
}

message_t *handle_join_game_request(message_t *message, player_t *player) {
    if (message->argc != MSG_JOIN_GAME_ARGC) {
        return create_err_message(message, MSG_ERR_INVALID_ARG_COUNT, player);
    }
    
    int32_t game_id = message->argv[0];
    
    if (!is_item_id_valid(game_id)) {
        return create_err_message(message, MSG_ERR_INVALID_ID, player);
    }
    
    lock_list(g_game_list);
    game_t *game = get_from_list_by_id(g_game_list, game_id);
    
    if (game == NULL) {
        unlock_list(g_game_list, false);
        
        return create_err_message(message, MSG_ERR_ID_NOT_FOUND, player);
    }
    
    lock_game(game);
    
    if (game == player->current_game) {
        unlock_game(game, false);
        unlock_list(g_game_list, false);
        
        return create_err_message(message, MSG_ERR_ALREADY_IN_ROOM, player);
    }
    
    if (is_game_full(game)) {
        unlock_game(game, false);
        unlock_list(g_game_list, false);
        
        return create_err_message(message, MSG_ERR_ROOM_FULL, player);
    }
    
    add_player(game, player);
    unlock_game(game, true);
    unlock_list(g_game_list, true);
    
    return create_ack_message(message, player);
}

message_t *handle_leave_game_request(message_t *message, player_t *player) {
    if (message->argc != MSG_LEAVE_GAME_ARGC) {
        return create_err_message(message, MSG_ERR_INVALID_ARG_COUNT, player);
    }
    
    lock_list(g_game_list);
    
    if (player->current_game == NULL) {
        unlock_list(g_game_list, false);
        
        return create_err_message(message, MSG_ERR_NOT_IN_ROOM, player);
    }
    
    lock_game(player->current_game);
    remove_player(player->current_game, player);
    unlock_game(player->current_game, true);
    unlock_list(g_game_list, true);
    
    return create_ack_message(message, player);
}

message_t *handle_start_game_request(message_t *message, player_t *player) {
    if (message->argc != MSG_START_GAME_ARGC) {
        return create_err_message(message, MSG_ERR_INVALID_ARG_COUNT, player);
    }
    
    lock_list(g_game_list);
    
    if (player->current_game == NULL) {
        unlock_list(g_game_list, false);
        
        return create_err_message(message, MSG_ERR_NOT_IN_ROOM, player);
    }
    
    lock_game(player->current_game);
    
    if (has_game_enough_players(player->current_game)) {
        unlock_game(player->current_game, false);
        unlock_list(g_game_list, false);
        
        return create_err_message(message, MSG_ERR_NOT_ENOUGH_PLAYERS, player);
    }
    
    if (is_game_active(player->current_game)) {
        unlock_game(player->current_game, false);
        unlock_list(g_game_list, false);
        
        return create_err_message(message, MSG_ERR_ROUND_NOT_FINISHED, player);
    }
    
    restart_game(player->current_game);
    unlock_game(player->current_game, true);
    unlock_list(g_game_list, true);
    
    return create_ack_message(message, player);
}

message_t *handle_play_game_request(message_t *message, player_t *player) {
    if (message->argc != MSG_PLAY_GAME_ARGC) {
        return create_err_message(message, MSG_ERR_INVALID_ARG_COUNT, player);
    }
    
    int8_t x = string_to_byte(message->argv[0]);
    int8_t y = string_to_byte(message->argv[1]);
    
    lock_list(g_game_list);
    
    if (player->current_game == NULL) {
        unlock_list(g_game_list, false);
        
        return create_err_message(message, MSG_ERR_NOT_IN_ROOM, player);
    }
    
    lock_game(player->current_game);
    
    if (!is_game_active(player->current_game)) {
        unlock_game(player->current_game, false);
        unlock_list(g_game_list, false);
        
        return create_err_message(message, MSG_ERR_ROUND_NOT_STARTED, player);
    }
    
    if (!player->playing) {
        unlock_game(player->current_game, false);
        unlock_list(g_game_list, false);
        
        return create_err_message(message, MSG_ERR_CANNOT_PLAY_IN_ROUND, player);
    }
    
    if (!is_current_player(player->current_game, player)) {
        unlock_game(player->current_game, false);
        unlock_list(g_game_list, false);
        
        return create_err_message(message, MSG_ERR_CANNOT_PLAY_NOW, player);
    }
    
    if (x < (int8_t) 0 || y < (int8_t) 0) {
        unlock_game(player->current_game, false);
        unlock_list(g_game_list, false);
        
        return create_err_message(message, MSG_ERR_INVALID_POSITION, player);
    }
    
    if (is_cell_in_board(player->current_game, x, y)) {
        unlock_game(player->current_game, false);
        unlock_list(g_game_list, false);
        
        return create_err_message(message, MSG_ERR_CELL_OUT_OF_BOARD, player);
    }
    
    if (!can_play(player->current_game, x, y)) {
        unlock_game(player->current_game, false);
        unlock_list(g_game_list, false);
        
        return create_err_message(message, MSG_ERR_CELL_OCCUPIED, player);
    }
    
    play(player->current_game, player->current_game_index, x, y);
    unlock_game(player->current_game, true);
    unlock_list(g_game_list, true);
    
    return create_ack_message(message, player);
}

message_t *handle_unknown_request(message_t *message, player_t *player) {
    message_t *new_message = create_message(message->type, MSG_ACK_ARGC);
    new_message[0] = MSG_FAIL;
    
    return new_message;
}

message_t *try_handle_activation_request(message_t *message, player_t *player) {
    // odeslána chybová odpověď, pokud je klient již aktivní
    if (player->active) {
        return create_err_message(message, MSG_ERR_ALREADY_ACTIVE, player);
    }
    // aktivace klienta, pokud není aktivní
    else {
        return handle_activation_request(message, player);
    }
}

message_t *try_handle_client_request(message_t *message, player_t *player) {
    // odeslána chybová odpověď, pokud klient není aktivní
    if (!player->active) {
        return create_err_message(message, MSG_ERR_NOT_ACTIVE, player);
    }
    // zpracování požadavku, pokud je klient aktivní
    else {
        // požadavek na deaktivaci klienta
        if (!strcmp(message->type, MSG_DEACTIVATE_CLIENT)) {
            return handle_deactivation_request(message, player);
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
        // požadavek na zahájení herního kola v místnosti
        else if (!strcmp(message->type, MSG_START_GAME)) {
            return handle_start_game_request(message, player);
        }
        // požadavek na herní tah v místnosti
        else if (!strcmp(message->type, MSG_PLAY_GAME)) {
            return handle_play_game_request(message, player);
        }
        // odpověď na restart serveru
        else if (!strcmp(message->type, MSG_SERVER_SHUTDOWN)) {
            return NULL;
        }
        // odpověď na aktualizaci seznamu hráčů
        else if (!strcmp(message->type, MSG_CLIENT_LIST)) {
            return NULL;
        }
        // odpověď na aktualizaci seznamu her
        else if (!strcmp(message->type, MSG_GAME_LIST)) {
            return NULL;
        }
        // odpověď na aktualizaci stavu hry
        else if (!strcmp(message->type, MSG_GAME_STATUS)) {
            return NULL;
        }
        // neznámý požadavek - bude odeslána prázdná chybová odpověď
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
    
    lock_player(player);
        
    // příchozí zpráva je test odezvy - bude odeslána odpověď (prázdná zpráva)
    if (request->type == NULL) {
        response = handle_ping(player);
    }
    // příchozí zpráva je požadavek
    else {
        // požadavek na aktivaci klienta
        if (!strcmp(request->type, MSG_ACTIVATE_CLIENT)) {
            response = try_handle_activation_request(request, player);
        }
        // jiný požadavek
        else {
            response = try_handle_client_request(request, player);
        }
    }
    
    send_message(response, player->sock);
    unlock_player(player);
    
    delete_message(request);
    
    if (response != NULL) {
        delete_message(response);
    }
}

/*
 * Základní funkce pro vytvoření, běh a odstranění hráče:
 */

void run_player(void *arg) {
    player_t *player = (player_t *) arg;

    while (true) {
        parse_received_message(player);
        unlock_player(player);
        
        if (player->id != 0 && !player->active) {
            delete_player(player);
        }
    }
}

void create_player(int sock) {
    player_t *player = (player_t *) malloc(sizeof(player_t));
    pthread_mutex_init(&player->lock, NULL);
    player->sock = sock;
    player->id = 0;
    player->nick = NULL;
    player->current_game = NULL;
    player->current_game_index = 0;
    player->current_game_score = 0;
    player->total_score = 0;
    player->active = false;
    player->playing = false;
    
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
    player_t *player = (player_t *) item;
    message_t *message = create_message(MSG_CLIENT_LIST_ITEM, MSG_CLIENT_LIST_ITEM_ARGC);
    message->argv[0] = int_to_string(player->id);
    message->argv[1] = player->nick;
    message->argv[2] = int_to_string(player->total_score);
    
    return message;
}

message_t *player_list_to_msg()  {
    message_t *message = create_message(MSG_CLIENT_LIST, MSG_CLIENT_LIST_ARGC);
    message->argv[0] = int_to_string(g_player_list->count);
    
    return message;
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