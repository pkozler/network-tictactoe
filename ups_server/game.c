#include "game.h"

#include <stdlib.h>

/*
    modul obsahující funkce pro obsluhu jednotlivých her
*/

/**
    Vytvoří novou stavovou strukturu hry.
*/
game_t *create_game(int32_t game_id, byte_t matrix_size, byte_t players_size, byte_t win_row_len) {
    game_t *game = calloc(sizeof(game_t), 1);
    pthread_mutex_init(&(game->lock), NULL);
    pthread_mutex_lock(&(game->lock));

    game->game_id = game_id;
    game->players_size = players_size;
    game->matrix_size = matrix_size;
    game->win_row_len = win_row_len;

    game->players = calloc(sizeof(thread_t *), players_size);
    game->matrix = calloc(sizeof(byte_t *), matrix_size);
    game->win_row = calloc(sizeof(byte_t), win_row_len * 2);

    byte_t i = 0;
    for (i = 0; i < game->matrix_size; i++) {
        game->matrix[i] = calloc(sizeof(byte_t), matrix_size);
    }

    game->last_added_player = 0;
    game->last_removed_player = 0;
    game->last_move_x = 0;
    game->last_move_y = 0;
    game->last_move_player = 0;
    game->current_player = 1;
    game->player_counter = 0;
    game->cell_counter = 0;
    game->winner = 0;
    game->running = false;
    pthread_mutex_unlock(&(game->lock));

    return game;
}

/**
    Restartuje stav hry.
*/
void restart_game(game_t *game) {
    pthread_mutex_lock(&(game->lock));

    byte_t i = 0;
    for (i = 0; i < game->matrix_size; i++) {
        free(game->matrix[i]);
        game->matrix[i] = calloc(sizeof(char), game->matrix_size);
    }

    free(game->win_row);
    game->win_row = calloc(sizeof(byte_t), game->win_row_len * 2);

    game->last_added_player = 0;
    game->last_removed_player = 0;
    game->last_move_x = 0;
    game->last_move_y = 0;
    game->last_move_player = 0;
    game->current_player = 1;
    game->cell_counter = 0;
    game->winner = 0;
    game->running = (game->player_counter == game->players_size);
    pthread_mutex_unlock(&(game->lock));
}

/**
    Odstraní stavovou strukturu hry.
*/
void delete_game(game_t *game) {
    pthread_mutex_lock(&(game->lock));

    byte_t i = 0;
    for (i = 0; i < game->matrix_size; i++) {
        free(game->matrix[i]);
        game->matrix[i] = calloc(sizeof(char), game->matrix_size);
    }

    free(game->matrix);
    free(game->win_row);
    pthread_mutex_unlock(&(game->lock));
    pthread_mutex_destroy(&(game->lock));
    free(game);
}

/**
    Přidá hráče do hry a při plném počtu hráčů
    spustí nové kolo hry.
*/
byte_t add_player(game_t *game, thread_t *player) {
    // ověří, zda hra ještě nezapočala
    if (game->running) {
        return 0;
    }

    // ověří, zda hráč nebyl dosud přidán
    if (player->game != NULL) {
        return player->player_id;
    }

    pthread_mutex_lock(&(game->lock));

    // najde volné místo v seznamu hráčů a umístí zde ID nového hráče
    byte_t i;
    for (i = 0; i < game->players_size; i++) {
        if (game->players[i] == NULL) {
            player->game = game;
            game->players[i] = player;
            game->last_added_player = player->client_id;
            game->player_counter++;

            // pokud počítadlo hráčů dosáhne požadovaného počtu, spustí hru
            game->running = (game->player_counter == game->players_size);
            pthread_mutex_unlock(&(game->lock));

            return (byte_t) (i + 1);
        }
    }

    pthread_mutex_unlock(&(game->lock));

    return 0;
}

/**
    Nastaví dalšího hráče, který je na tahu.
*/
void set_next_player(game_t *game) {
    byte_t i = game->current_player - 1; // pozice aktuálního hráče v poli

    do {
        i++;
        i %= game->players_size;
    }
    while(game->players[i] == 0);

    game->current_player = i + 1;
}

/**
    Odstraní hráče ze hry. Při nulovém počtu hráčů
    je hra odstraněna.
*/
byte_t remove_player(game_t *game, thread_t *player) {
    pthread_mutex_lock(&(game->lock));

    // najde hráče na seznamu a odstraní jeho ID
    byte_t i;
    for (i = 0; i < game->players_size; i++) {
        if (game->players[i] == player) {
            // přesune tah na dalšího hráče, pokud je na tahu odpojovaný hráč
            if (game->current_player == player->player_id) {
                set_next_player(game);
            }

            player->player_id = 0;
            player->game = NULL;
            game->players[i] = NULL;
            game->last_removed_player = player->client_id;
            game->player_counter--;

            pthread_mutex_unlock(&(game->lock));

            return game->player_counter;
        }
    }

    pthread_mutex_unlock(&(game->lock));

    return -1;
}

/**
    Zkontroluje obsazená políčka v horizontálním směru.
*/
byte_t check_horizontal(game_t *game, byte_t player_pos, byte_t x, byte_t y) {
    byte_t counter = 0;

    int j;
    for (j = x - game->win_row_len + 1;
            j < x + game->win_row_len; j++) {
        if (j < 0 || j >= game->matrix_size) {
            continue;
        }

        if (game->matrix[y][j] == player_pos) {
            game->win_row[2 * counter] = y;
            game->win_row[2 * counter + 1] = j;
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

/**
    Zkontroluje obsazená políčka ve vertikálním směru.
*/
byte_t check_vertical(game_t *game, byte_t player_pos, byte_t x, byte_t y) {
    byte_t counter = 0;

    int i;
    for (i = y - game->win_row_len + 1;
            i < y + game->win_row_len; i++) {
        if (i < 0 || i >= game->matrix_size) {
            continue;
        }

        if (game->matrix[i][x] == player_pos) {
            game->win_row[2 * counter] = i;
            game->win_row[2 * counter + 1] = x;
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

/**
    Zkontroluje obsazená políčka v diagonálním směru zprava doleva.
*/
byte_t check_diag_right(game_t *game, byte_t player_pos, byte_t x, byte_t y) {
    byte_t counter = 0;

    int i = y + game->win_row_len - 1;

    int j;
    for (j = x - game->win_row_len + 1;
            j < x + game->win_row_len
            && i >= y - game->win_row_len; j++) {
        if (i < 0 || j < 0 || i >= game->matrix_size || j >= game->matrix_size) {
            i--;
            continue;
        }

        if (game->matrix[i][j] == game->players_size) {
            game->win_row[2 * counter] = i;
            game->win_row[2 * counter + 1] = j;
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

/**
    Zkontroluje obsazená políčka v diagonálním směru zleva doprava.
*/
byte_t check_diag_left(game_t *game, byte_t player_pos, byte_t x, byte_t y) {
    byte_t counter = 0;

    int i = y - game->win_row_len + 1;

    int j;
    for (j = x - game->win_row_len + 1;
            j < x + game->win_row_len
            && i < y + game->win_row_len; j++) {
        if (i < 0 || j < 0 || i >= game->matrix_size || j >= game->matrix_size) {
            i++;
            continue;
        }

        if (game->matrix[i][j] == player_pos) {
            game->win_row[2 * counter] = i;
            game->win_row[2 * counter + 1] = j;
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

/**
    Provede tah hráče (pokud je platný), nastaví dalšího hráče,
    který je na tahu a zkontroluje obsazenost políček za účelem
    zjištění, zda některý z hráčů obsadil dostatečný počet políček
    za sebou a dosáhl vítězství.
*/
bool play(game_t *game, byte_t player_pos, byte_t x, byte_t y) {
    pthread_mutex_lock(&(game->lock));

    // test, zda lze vložit políčko na danou pozici
    if (game->matrix[y][x] != 0) {
        pthread_mutex_unlock(&(game->lock));

        return false;
    }

    // vloží hráčovo políčko, inkrementuje čítač obsazených políček a určí dalšího hráče, který je na řadě
    game->matrix[y][x] = player_pos;
    game->last_move_player = player_pos;
    game->last_move_x = x;
    game->last_move_y = y;
    game->cell_counter++;
    set_next_player(game);

    // test, zda byl vyplněn požadovaný počet políček v řadě

    game->winner = check_horizontal(game, player_pos, x, y);

    if (game->winner != 0) {
        game->running = false;
        pthread_mutex_unlock(&(game->lock));

        return true;
    }

    game->winner = check_vertical(game, player_pos, x, y);

    if (game->winner != 0) {
        game->running = false;
        pthread_mutex_unlock(&(game->lock));

        return true;
    }

    game->winner = check_diag_right(game, player_pos, x, y);

    if (game->winner != 0) {
        game->running = false;
        pthread_mutex_unlock(&(game->lock));

        return true;
    }

    game->winner = check_diag_left(game, player_pos, x, y);

    if (game->winner != 0) {
        game->running = false;
        pthread_mutex_unlock(&(game->lock));

        return true;
    }

    // test, zda bylo hrací pole zcela zaplněno (pokud ano, ukončí hru)
    if (game->cell_counter == game->matrix_size * game->matrix_size) {
        game->running = false;
        pthread_mutex_unlock(&(game->lock));

        return true;
    }

    pthread_mutex_unlock(&(game->lock));

    return true;
}
