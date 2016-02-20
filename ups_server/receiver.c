#include "receiver.h"

#include "defs.h"
#include "common.h"
#include "game.h"
#include "logging.h"

#include <stdbool.h>
#include <stdint.h>

#include <netinet/in.h>
#include <sys/socket.h>
#include <arpa/inet.h>

/*
    modul s funkcemi pro příjem požadavků od uživatele
    z klienta
*/

/**
    Zkontroluje, zda přijatá data přestavují potvrzení klienta
    o přijetí stavu, nebo hlavičku požadavku od uživatele.
*/
bool is_message(thread_t *thread) {
    int32_t code;
    read_bytes(thread, &code, sizeof(code));
    code = ntohl(code);

    if (code == ACK_CODE) {
        return false;
    }
    else if (code == MSG_CODE) {
        return true;
    }
    else {
        print("Klient %d: odmítnut neplatný požadavek.",
            thread->client_id);
        return false;
    }
}

/**
    Přijme požadavek na vytvoření nové hry,
    připojení hráče do hry z aktuálního seznamu her,
    nebo odpojení hráče ze hry.
*/
void receive_game_request(thread_t *thread) {
    int32_t code;
    read_bytes(thread, &code, sizeof(code));
    code = ntohl(code);

    if (code == ACK_CODE) {
        return;
    }
    else if (code == MSG_CODE);
    else {
        print("Klient %d: odmítnut neplatný požadavek.",
            thread->client_id);
        return;
    }

    game_t *game;
    int32_t game_id = 0;

    print("Klient %d: zahájen příjem požadavku na výběr hry.",
            thread->client_id);

    read_bytes(thread, &game_id, sizeof(game_id));
    game_id = ntohl(game_id);

    if (game_id == -1) { // klient požaduje odstranění ze hry
        print("Klient %d: hráč požaduje odstranění ze hry s ID %d.",
            thread->client_id, thread->game->game_id);

        remove_player_from_game(thread);

        print("Klient %d: hráč byl odstraněn ze hry.",
            thread->client_id);

        return;
    }

    if (game_id < -1) {
        print("Klient %d: odmítnut požadavek na hru s neplatným ID.",
            thread->client_id);
        return;
    }

    else if (game_id != 0) { // klient se pokusil připojit k existující hře
        print("Klient %d: hráč požaduje připojení ke hře s ID %d.",
            thread->client_id, game_id);

        pthread_mutex_lock(&game_list_lock);
        game = get_from_list(game_list, game_id, game_id_equals);
        pthread_mutex_unlock(&game_list_lock);

        if (game == NULL) {
            print("Klient %d: odmítnut požadavek na neexistující hru.",
                thread->client_id);
            return;
        }

        if (game->running) {
            print("Klient %d: odmítnut požadavek na právě rozehranou hru.",
                thread->client_id);
            return;
        }
    } // klient zaslal požadavek na vytvoření nové hry
    else {
        byte_t players_size, matrix_size, win_row_len;

        print("Klient %d: hráč požaduje vytvoření nové hry.",
            thread->client_id);

        read_bytes(thread, &players_size, sizeof(players_size));

        if (players_size < MIN_PLAYERS_SIZE || players_size > MAX_PLAYERS_SIZE) {
            print("Klient %d: odmítnut počet hráčů"
                    " mimo povolený interval <%d, %d>.",
                thread->client_id, MIN_PLAYERS_SIZE, MAX_PLAYERS_SIZE);
            return;
        }

        read_bytes(thread, &matrix_size, sizeof(matrix_size));

        if (matrix_size < MIN_MATRIX_SIZE || matrix_size > MAX_MATRIX_SIZE) {
            print("Klient %d: odmítnuta velikost herního pole"
                    " mimo povolený interval <%d, %d>.",
                thread->client_id, MIN_MATRIX_SIZE, MAX_MATRIX_SIZE);
            return;
        }

        read_bytes(thread, &win_row_len, sizeof(win_row_len));

        if (win_row_len < MIN_MATRIX_SIZE || win_row_len > MAX_MATRIX_SIZE) {
            print("Klient %d: odmítnut počet obsazených políček pro vítězství"
                    " mimo povolený interval <%d, %d>.",
                thread->client_id, MIN_MATRIX_SIZE, MAX_MATRIX_SIZE);
            return;
        }

        matrix_size = matrix_size;
        players_size = players_size;
        win_row_len = win_row_len;

        pthread_mutex_lock(&game_list_lock);
        int32_t game_id = game_counter + 1;
        game = create_game(game_id, matrix_size, players_size, win_row_len);

        if (game == NULL) {
            print("Klient %d: požadovanou hru nebylo možné vytvořit.",
                thread->client_id);
            pthread_mutex_unlock(&game_list_lock);
            return;
        }

        if (!add_to_list(&game_list, game)) {
            print("Klient %d: požadovanou hru nebylo možné uložit do seznamu.",
                thread->client_id);
            free(game);
            pthread_mutex_unlock(&game_list_lock);
            return;
        }

        game_counter++;
        pthread_mutex_unlock(&game_list_lock);
        print("Klient %d: na požadavek hráče byla vytvořena hra s ID %d.",
            thread->client_id, game->game_id);
    }

    add_player_to_game(game, thread);

    if (thread->player_id == 0) {
        print("Klient %d: hráč se nemohl připojit ke hře s ID %d.",
            thread->client_id, thread->game->game_id);
        return;
    }

    print("Klient %d: hráč se připojil ke hře s ID %d na pozici %d.",
            thread->client_id, thread->game->game_id, thread->player_id);
}

/**
    Přijme požadavej představující tah hráče
    v jeho právě rozehrané hře.
*/
void receive_message(thread_t *thread) {
    int32_t code;
    read_bytes(thread, &code, sizeof(code));
    code = ntohl(code);

    if (code == ACK_CODE) {
        return;
    }
    else if (code == MSG_CODE);
    else {
        print("Klient %d: odmítnut neplatný požadavek.",
            thread->client_id);
        return;
    }

    byte_t x, y;

    print("Klient %d: zahájen příjem tahu hráče.",
            thread->client_id);

    read_bytes(thread, &x, sizeof(x));
    read_bytes(thread, &y, sizeof(y));

    if (x == -1 && y == -1) {
        print("Klient %d: přijat požadavek na vystoupení ze hry.",
            thread->client_id);
        remove_player_from_game(thread);
        return;
    }

    if (!(thread->game->running)) {
        print("Klient %d: přijat požadavek na restart hry.",
            thread->client_id);
        restart_game(thread->game);
        return;
    }

    if (x < 0 || x >= thread->game->matrix_size) {
        print("Klient %d: odmítnut tah na neexistujících souřadnicích.",
            thread->client_id);
        return;
    }

    if (y < 0 || y >= thread->game->matrix_size) {
        print("Klient %d: odmítnut tah na neexistujících souřadnicích.",
            thread->client_id);
        return;
    }

    if (!can_play(thread)) {
        print("Klient %d: odmítnut tah hráče, protože není na řadě.", thread->client_id);
        return;
    }

    if (play(thread->game, thread->player_id, x, y)) {
        print("Klient %d: hráč táhnul na souřadnicích {%d, %d}.", thread->client_id, (int)x, (int)y);
    }
    else {
        print("Klient %d: hráč nemůže táhnout na souřadnicích {%d, %d}.", thread->client_id, (int)x, (int)y);
    }
}
