/* 
 * Author: Petr Kozler
 */

#include "game.h"
#include "err.h"
#include <stdlib.h>

#define CHECK_FUNC_CNT 4

typedef int8_t (*check_direction)(game_t *game, int8_t player_pos, int8_t x, int8_t y);

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

bool is_game_name_valid(char *name) {
    // TODO zkontrolovat format jmena
}

/*
 * Funkce pro sestavování a odesílání odpovědí serveru o aktuálním stavu hry:
 */

int32_t count_status_messages(game_t *game) {
    return game->player_counter + 1;
}

message_t *game_to_message(game_t *game) {
    // TODO implementovat
}

message_t *player_to_message(player_t *player) {
    // TODO implementovat
}

message_t **status_to_messages(game_t *game, int32_t count) {
    message_t **messages = (message_t **) malloc(sizeof(message_t *) * count);
    messages[0] = game_to_message(game);
   
    int32_t i = 1;
    int32_t j;
    for (j = 1; j < game->players_size; j++) {
        if (game->players[j] != NULL) {
            messages[i] = player_to_message(game->players[j]);
            i++;
        }
    }
    
    return messages;
}

void send_to_current_players(game_t *game, int32_t msgc, message_t **msgv) {
    int32_t i, j;
    for (j = 0; j < game->players_size; j++) {
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
        free(messages[i]);
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
        int8_t board_size, int8_t players_size, int8_t win_row_len) {
    game_t *game = calloc(sizeof(game_t), 1);
    pthread_mutex_init(&(game->lock), NULL);

    game->players_size = players_size;
    game->board_size = board_size;
    game->win_row_len = win_row_len;

    game->players = calloc(sizeof(player_t *), players_size);
    game->board = calloc(sizeof(game_cell_t *), board_size);

    int8_t i = 0;
    for (i = 0; i < game->board_size; i++) {
        game->board[i] = calloc(sizeof(game_cell_t), board_size);
    }

    game->current_player = 1;
    game->player_counter = 0;
    game->cell_counter = 0;
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
    lock_game(game);
    
    int8_t i;
    for (i = 0; i < game->board_size; i++) {
        free(game->board[i]);
        game->board[i] = calloc(sizeof(game_cell_t), game->board_size);
    }

    game->current_player = 1;
    game->cell_counter = 0;
    game->winner = 0;
    game->active = (game->player_counter > 1);
    
    unlock_game(game, true);
}

void add_player(game_t *game, player_t *player) {
    lock_game(game);
    
    int8_t i;
    for (i = 0; i < game->players_size; i++) {
        if (game->players[i] == NULL) {
            player->current_game = game;
            game->players[i] = player;
            game->player_counter++;
            
            break;
        }
    }
    
    unlock_game(game, true);
}

void set_next_player(game_t *game) {
    int8_t i = game->current_player - 1;

    do {
        i++;
        i %= game->players_size;
    }
    while(game->players[i] == 0);

    game->current_player = i + 1;
}

void remove_player(game_t *game, player_t *player) {
    lock_game(game);
    
    int8_t i;
    for (i = 0; i < game->players_size; i++) {
        if (game->players[i] != player) {
            continue;
        }
        
        if (game->current_player == player->index) {
            set_next_player(game);
        }

        player->index = 0;
        player->current_game = NULL;
        game->players[i] = NULL;
        game->player_counter--;
    }

    unlock_game(game, true);
}

/*
 * Funkce k provádění herních tahů a k určování výsledků hry:
 */

int8_t check_horizontal(game_t *game, int8_t player_pos, int8_t x, int8_t y) {
    int8_t counter = 0;

    int j;
    for (j = x - game->win_row_len + 1;
            j < x + game->win_row_len; j++) {
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

        if (counter >= game->win_row_len) {
            return player_pos;
        }
    }

    return 0;
}

int8_t check_vertical(game_t *game, int8_t player_pos, int8_t x, int8_t y) {
    int8_t counter = 0;

    int i;
    for (i = y - game->win_row_len + 1;
            i < y + game->win_row_len; i++) {
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

        if (counter >= game->win_row_len) {
            return player_pos;
        }
    }

    return 0;
}

int8_t check_diag_right(game_t *game, int8_t player_pos, int8_t x, int8_t y) {
    int8_t counter = 0;

    int i = y + game->win_row_len - 1;

    int j;
    for (j = x - game->win_row_len + 1;
            j < x + game->win_row_len
            && i >= y - game->win_row_len; j++) {
        if (i < 0 || j < 0 || i >= game->board_size || j >= game->board_size) {
            i--;
            continue;
        }

        if (game->board[i][j] == game->players_size) {
            game->board[i][j].win = true;
            counter++;
        }
        else {
            counter = 0;
        }

        if (counter >= game->win_row_len) {
            return player_pos;
        }

        i--;
    }

    return 0;
}

int8_t check_diag_left(game_t *game, int8_t player_pos, int8_t x, int8_t y) {
    int8_t counter = 0;

    int i = y - game->win_row_len + 1;

    int j;
    for (j = x - game->win_row_len + 1;
            j < x + game->win_row_len
            && i < y + game->win_row_len; j++) {
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

        if (counter >= game->win_row_len) {
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
    check_direction[CHECK_FUNC_CNT];
    check_direction[0] = check_horizontal;
    check_direction[1] = check_vertical;
    check_direction[2] = check_diag_right;
    check_direction[3] = check_diag_left;
    
    int32_t i;
    for (i = 0; i < CHECK_FUNC_CNT; i++) {
        int8_t winner = check_direction[i];
        
        if (winner > 0) {
            return winner;
        }
    }
    
    return 0;
}

bool is_draw(game_t *game) {
    return game->cell_counter == game->board_size * game->board_size;
}

void play(game_t *game, int8_t player_pos, int8_t x, int8_t y) {
    lock_game(game);
    
    game->board[y][x] = player_pos;
    game->cell_counter++;
    set_next_player(game);

    game->winner = get_winner(game, player_pos, x, y);
    game->active = game->winner == 0 && !is_draw(game);
    
    unlock_game(game, true);
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
    // TODO implementovat
}

message_t *game_list_to_msg() {
    // TODO implementovat
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