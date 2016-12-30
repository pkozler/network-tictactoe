/* 
 * Author: Petr Kozler
 */

#include "game.h"
#include "observed_list.h"
#include "printer.h"
#include "message.h"
#include "config.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

observed_list_t *g_game_list;

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

/*
 * Funkce pro sestavování a odesílání odpovědí serveru o aktuálním stavu hry:
 */

int32_t count_status_messages(game_t *game) {
    return game->player_counter + 1;
}

message_t *game_board_to_message(game_t *game) {
    message_t *new_message = create_message(MSG_GAME_DETAIL, MSG_GAME_DETAIL_ARGC);
    
    put_byte_arg(new_message, game->winner);
    put_byte_arg(new_message, game->current_player);
    
    char *board_str = (char *) malloc(sizeof(char) *
        (game->board_size * game->board_size * (BOARD_CELL_SEED_SIZE + 1) + 1));
    board_str[0] = '\0';
    char buf[BOARD_CELL_SEED_SIZE + 1];
    
    int32_t i, j;
    for (j = 0; j < game->board_size; j++) {
        for (i = 0; i < game->board_size; i++) {
            game_cell_t cell = game->board[j][i];
            snprintf(buf, BOARD_CELL_SEED_SIZE + 1, "%d", cell.index);
            strcat(board_str, buf);
            strcat(board_str, cell.win ? WINNING_CELL_SYMBOL : NORMAL_CELL_SYMBOL);
        }
    }
    
    return new_message;
}

message_t *joined_player_to_message(player_t *player) {
    message_t *new_message = create_message(MSG_GAME_PLAYER, MSG_GAME_PLAYER_ARGC);
    put_int_arg(new_message, player->id);
    put_int_arg(new_message, player->current_game_index);
    put_int_arg(new_message, player->current_game_score);
    
    return new_message;
}

message_t **status_to_messages(game_t *game, int32_t count) {
    message_t **messages = (message_t **) malloc(sizeof(message_t *) * count);
    messages[0] = game_board_to_message(game);
    
    int32_t i = 1;
    int32_t j;
    for (j = 1; j < game->player_count; j++) {
        if (game->players[j] != NULL) {
            messages[i] = joined_player_to_message(game->players[j]);
            i++;
        }
    }
    
    return messages;
}

void send_to_current_players(game_t *game, int32_t msgc, message_t **msgv) {
    int32_t i, j;
    for (j = 0; j < game->player_count; j++) {
        if (game->players[j]->active && game->players[j] != NULL) {
            lock_player(game->players[j]);
            
            for (i = 0; i < msgc; i++) {
                send_message(msgv[i], game->players[j]->sock);
            }
            
            unlock_player(game->players[j]);
        }
    }
}

void send_game_status(game_t *game) {
    int32_t count = count_status_messages(game);
    message_t **messages = status_to_messages(game, count);
    send_to_current_players(game, count, messages);
    
    int32_t i;
    for (i = 0; i < count; i++) {
        delete_message(messages[i]);
    }
    
    free(messages);
}

/*
 * Základní funkce pro vytvoření, běh a odstranění hry:
 */

void *run_game(void *arg) {
    game_t *game = (game_t *) arg;
    
    while (true) {
        if (game->changed) {
            lock_game(game);
            send_game_status(game);
            unlock_game(game, false);
        }
    }
    
    return NULL;
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
    
    if (pthread_create(&(game->thread), NULL, run_game, game) < 0) {
        print_err("Chyba při spouštění vlákna pro rozesílání stavu hry");
    }
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

        if (game->board[y][j].index == player_pos) {
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

        if (game->board[i][x].index == player_pos) {
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

        if (game->board[i][j].index == game->player_count) {
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

        if (game->board[i][j].index == player_pos) {
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
    game->board[y][x].index = player_pos;
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
    put_int_arg(message, game->id);
    put_str_arg(message, game->name);
    put_byte_arg(message, game->player_count);
    put_byte_arg(message, game->board_size);
    put_byte_arg(message, game->cell_count);
    put_byte_arg(message, game->player_counter);
    put_int_arg(message, game->round_counter);
    put_int_arg(message, (game->active ? 1 : 0));
    
    return message;
}

message_t *game_list_to_msg() {
    message_t *message = create_message(MSG_GAME_LIST, MSG_GAME_LIST_ARGC);
    put_int_arg(message, g_player_list->count);
    
    return message;
}

/*
 * Funkce pro vytvoření a odstranění seznamu her:
 */

void create_game_list() {
    g_game_list = create_list("seznam her", get_game_key, set_game_key,
            get_game_name, game_to_msg, game_list_to_msg);
}

void delete_game_list() {
    lock_list(g_game_list);
    list_iterator_t *iterator = create_list_iterator(g_game_list);
    game_t *game = (game_t *) get_next_item(iterator);
    
    while (game != NULL) {
        delete_game(game);
        game = (game_t *) get_next_item(iterator);
    }
    
    free(iterator);
    unlock_list(g_game_list, false);
    delete_list(g_game_list);
}