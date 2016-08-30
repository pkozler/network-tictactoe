/* 
 * Author: Petr Kozler
 */

#include "game.h"
#include "err.h"
#include "config.h"
#include <stdlib.h>
#include <string.h>

/*#define CHECK_FUNC_CNT 4

typedef int8_t (*check_direction)(game_t *game, int8_t player_pos, int8_t x, int8_t y);*/

/*
 * Pomocné funkce pro operace s hrami:
 */

void lock_game(game_t *game) {
    pthread_mutex_lock(&(game->lock));
}

void unlock_game(game_t *game, bool changed) {
    game->changed = changed;
    pthread_mutex_unlock(&(game->lock));
}

bool is_game_board_size_valid(int8_t board_size) {
    return MIN_BOARD_SIZE <= board_size && board_size <= MAX_BOARD_SIZE;
}

bool is_game_player_count_valid(int8_t player_count) {
    return MIN_PLAYERS_SIZE <= player_count && player_count <= MAX_PLAYERS_SIZE;
}

bool is_game_cell_count_valid(int8_t cell_count) {
    return MIN_CELL_COUNT <= cell_count && cell_count <= MIN_CELL_COUNT;
}

bool is_current_player(game_t *game, player_t *player) {
    return player->current_game_index == game->current_player;
}

bool is_cell_in_board(game_t *game, int8_t x, int8_t y) {
    return 0 <= x && x < game->board_size
            && 0 <= y && y < game->board_size;
}

bool is_current_player(game_t *game, player_t *player) {
    return player->current_game_index == game->current_player;
}

/*
 * Funkce pro sestavování a odesílání odpovědí serveru o aktuálním stavu hry:
 */

int32_t count_status_messages(game_t *game) {
    return game->player_counter + 1;
}

message_t *game_to_message(game_t *game) {
    message_t *new_message = create_message(MSG_GAME_STATUS, MSG_GAME_STATUS_ARGC);
    new_message[0] = game->winner; // TODO prevest na retezec
    new_message[1] = game->current_player; // TODO prevest na retezec
    
    // TODO vypsat herni pole
    
    return new_message;
}

message_t *player_to_message(player_t *player) {
    message_t *new_message = create_message(MSG_GAME_PLAYER, MSG_GAME_PLAYER_ARGC);
    new_message[0] = player->id; // TODO prevest na retezec
    new_message[1] = player->current_game_index; // TODO prevest na retezec
    new_message[2] = player->current_game_score; // TODO prevest na retezec
    
    return new_message;
}

message_t **status_to_messages(game_t *game, int32_t count) {
    message_t **messages = (message_t **) malloc(sizeof(message_t *) * count);
    messages[0] = game_to_message(game);
   
    int32_t i = 1;
    int32_t j;
    for (j = 1; j < game->player_count; j++) {
        if (game->players[j] != NULL) {
            messages[i] = player_to_message(game->players[j]);
            i++;
        }
    }
    
    return messages;
}

void send_to_current_players(game_t *game, int32_t msgc, message_t **msgv) {
    int32_t i, j;
    for (j = 0; j < game->player_count; j++) {
        if (game->players[j] != NULL) {
            for (i = 0; i < msgc; i++) {
                send_message(msgv[i], game->players[j]->sock);
            }
        }
    }
}

void send_game_status(game_t *game) {
    int32_t count = count_status_messages(game);
    message_t **messages = status_to_messages(game, count);
    send_to_current_players(count, messages);
    
    int32_t i;
    for (i = 0; i < count; i++) {
        delete_message(messages[i]);
    }
    
    free(messages);
}

/*
 * Základní funkce pro vytvoření, běh a odstranění hry:
 */

void run_game(void *arg) {
    game_t *game = (game_t *) arg;
    
    while (true) {
        if (game->changed) {
            lock_game(game);
            send_game_status(game);
            unlock_game(game, false);
        }
    }
}

void create_game(player_t *player, char *name,
        int8_t board_size, int8_t player_count, int8_t cell_count) {
    game_t *game = calloc(sizeof(game_t), 1);
    pthread_mutex_init(&(game->lock), NULL);

    game->player_count = player_count;
    game->board_size = board_size;
    game->cell_count = cell_count;

    game->players = calloc(sizeof(player_t *), player_count);
    game->board = calloc(sizeof(game_cell_t *), board_size);

    int8_t i = 0;
    for (i = 0; i < game->board_size; i++) {
        game->board[i] = calloc(sizeof(game_cell_t), board_size);
    }

    game->current_player = 1;
    game->player_counter = 0;
    game->occupied_cell_counter = 0;
    game->winner = 0;
    game->active = false;
    game->changed = true;
    add_player(game, player);
    
    if (pthread_create(game->thread, NULL, run_game, game) < 0) {
        die("Chyba při spouštění vlákna pro rozesílání stavu hry");
    }
    
    return game;
}

void delete_game(game_t *game) {
    int8_t i = 0;
    for (i = 0; i < game->board_size; i++) {
        free(game->board[i]);
    }

    free(game->board);
    pthread_cancel(game->thread);
    pthread_mutex_destroy(&(game->lock));
    
    free(game);
}

/*
 * Funkce pro změny stavu herní místnosti (reset, přidání nebo odebrání hráče):
 */

void restart_game(game_t *game) {
    int8_t i;
    for (i = 0; i < game->board_size; i++) {
        free(game->board[i]);
        game->board[i] = calloc(sizeof(game_cell_t), game->board_size);
    }

    game->current_player = 1;
    game->occupied_cell_counter = 0;
    game->winner = 0;
    game->active = (game->player_counter > 1);
}

bool is_game_full(game_t *game) {
    return game->player_counter == game->player_count;
}

bool has_game_enough_players(game_t *game) {
    return game->player_counter > 1;
}

bool is_game_active(game_t *game) {
    return game->active;
}

void add_player(game_t *game, player_t *player) {
    int8_t i;
    for (i = 0; i < game->player_count; i++) {
        if (game->players[i] == NULL) {
            player->current_game = game;
            game->players[i] = player;
            game->player_counter++;
            
            break;
        }
    }
}

void set_next_player(game_t *game) {
    int8_t i = game->current_player - 1;

    do {
        i++;
        i %= game->player_count;
    }
    while(game->players[i] == 0);

    game->current_player = i + 1;
}

void remove_player(game_t *game, player_t *player) {
    int8_t i;
    for (i = 0; i < game->player_count; i++) {
        if (game->players[i] != player) {
            continue;
        }
        
        if (game->current_player == player->current_game_index) {
            set_next_player(game);
        }

        player->current_game_index = 0;
        player->current_game = NULL;
        game->players[i] = NULL;
        game->player_counter--;
    }
}

/*
 * Funkce k provádění herních tahů a k určování výsledků hry:
 */

int8_t check_horizontal(game_t *game, int8_t player_pos, int8_t x, int8_t y) {
    int8_t counter = 0;

    int j;
    for (j = x - game->cell_count + 1;
            j < x + game->cell_count; j++) {
        if (j < 0 || j >= game->board_size) {
            continue;
        }

        if (game->board[y][j] == player_pos) {
            game->board[y][j].win = true;
            counter++;
        }
        else {
            counter = 0;
        }

        if (counter >= game->cell_count) {
            return player_pos;
        }
    }

    return 0;
}

int8_t check_vertical(game_t *game, int8_t player_pos, int8_t x, int8_t y) {
    int8_t counter = 0;

    int i;
    for (i = y - game->cell_count + 1;
            i < y + game->cell_count; i++) {
        if (i < 0 || i >= game->board_size) {
            continue;
        }

        if (game->board[i][x] == player_pos) {
            game->board[i][x].win = true;
            counter++;
        }
        else {
            counter = 0;
        }

        if (counter >= game->cell_count) {
            return player_pos;
        }
    }

    return 0;
}

int8_t check_diag_right(game_t *game, int8_t player_pos, int8_t x, int8_t y) {
    int8_t counter = 0;

    int i = y + game->cell_count - 1;

    int j;
    for (j = x - game->cell_count + 1;
            j < x + game->cell_count
            && i >= y - game->cell_count; j++) {
        if (i < 0 || j < 0 || i >= game->board_size || j >= game->board_size) {
            i--;
            continue;
        }

        if (game->board[i][j] == game->player_count) {
            game->board[i][j].win = true;
            counter++;
        }
        else {
            counter = 0;
        }

        if (counter >= game->cell_count) {
            return player_pos;
        }

        i--;
    }

    return 0;
}

int8_t check_diag_left(game_t *game, int8_t player_pos, int8_t x, int8_t y) {
    int8_t counter = 0;

    int i = y - game->cell_count + 1;

    int j;
    for (j = x - game->cell_count + 1;
            j < x + game->cell_count
            && i < y + game->cell_count; j++) {
        if (i < 0 || j < 0 || i >= game->board_size || j >= game->board_size) {
            i++;
            continue;
        }

        if (game->board[i][j] == player_pos) {
            game->board[i][j].win = true;
            counter++;
        }
        else {
            counter = 0;
        }

        if (counter >= game->cell_count) {
            return player_pos;
        }

        i++;
    }

    return 0;
}

bool can_play(game_t *game, int8_t x, int8_t y) {
    return game->board[y][x].index == 0;
}

int8_t get_winner(game_t *game, int8_t player_pos, int8_t x, int8_t y) {
    /*check_direction[CHECK_FUNC_CNT];
    check_direction[0] = check_horizontal;
    check_direction[1] = check_vertical;
    check_direction[2] = check_diag_right;
    check_direction[3] = check_diag_left;
    
    int32_t i;
    for (i = 0; i < CHECK_FUNC_CNT; i++) {
        int8_t winner = check_direction[i](game, player_pos, x, y);
        
        if (winner > 0) {
            return winner;
        }
    }*/
    
    int8_t winner = check_horizontal(game, player_pos, x, y);
    
    if (winner > 0) {
        return winner;
    }
    
    winner = check_vertical(game, player_pos, x, y);
    
    if (winner > 0) {
        return winner;
    }
    
    winner = check_diag_right(game, player_pos, x, y);
    
    if (winner > 0) {
        return winner;
    }
    
    winner = check_diag_left(game, player_pos, x, y);
    
    if (winner > 0) {
        return winner;
    }
    
    return 0;
}

bool is_draw(game_t *game) {
    return game->occupied_cell_counter == game->board_size * game->board_size;
}

void play(game_t *game, int8_t player_pos, int8_t x, int8_t y) {
    game->board[y][x] = player_pos;
    game->occupied_cell_counter++;
    set_next_player(game);

    game->winner = get_winner(game, player_pos, x, y);
    game->active = game->winner == 0 && !is_draw(game);
}

/*
 * Funkce pro použití ve funkcích spojového seznamu her:
 */

int32_t get_game_key(void *item) {
    game_t *game = (game_t *) item;
    
    return game->id;
}

void set_game_key(void *item, int32_t id) {
    game_t *game = (game_t *) item;
    game->id = id;
}

char *get_game_name(void *item) {
    game_t *game = (game_t *) item;
    
    return game->name;
}

message_t *game_to_msg(void *item) {
    game_t *game = (game_t *) item;
    message_t *message = create_message(MSG_GAME_LIST_ITEM, MSG_GAME_LIST_ITEM_ARGC);
    message->argv[0] = game->id;
    message->argv[1] = game->name;
    message->argv[2] = game->player_count;
    message->argv[3] = game->board_size;
    message->argv[4] = game->cell_count;
    message->argv[5] = game->player_counter;
    message->argv[6] = game->round_counter;
    message->argv[7] = game->active;
    
    return message;
}

message_t *game_list_to_msg() {
    message_t *message = create_message(MSG_GAME_LIST, MSG_GAME_LIST_ARGC);
    message->argv[0] = g_player_list->count;
    
    return message;
}

/*
 * Funkce pro vytvoření a odstranění seznamu her:
 */

void create_game_list() {
    g_game_list = create_list("seznam her", get_player_key, set_player_key,
            get_player_name, player_to_msg, player_list_to_msg);
}

void delete_game_list() {
    lock_list(g_game_list);
    list_iterator_t *iterator = create_list_iterator(g_game_list);
    game_t *game = (game_t *) get_next_item(iterator);
    
    while (game != NULL) {
        delete_game(game);
        game = (game *) get_next_item(iterator);
    }
    
    free(iterator);
    unlock_list(g_game_list, false);
    delete_list(g_game_list);
}