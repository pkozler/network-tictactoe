#include "sender.h"

#include "defs.h"
#include "common.h"
#include "game.h"
#include "logging.h"

#include <stdbool.h>
#include <stdint.h>
#include <unistd.h>

#include <netinet/in.h>
#include <sys/socket.h>
#include <arpa/inet.h>

/*
    modul s funkcemi pro odesílání aktuálního seznamu her
    nebo stavu hry klientovi a příjem potvrzení klienta
*/

/**
    Zkontroluje, zda spojení není označené jako přerušené
    (vypršel timeout při volání komunikační funkce).
    Pokud ano, odstraní klienta ze hry i ze seznamu hráčů
    a uzavře spojení s klientem.
*/
void check_connection(thread_t *thread) {
    if (thread->connected) {
        return;
    }

    if (thread->game != NULL) {
        remove_player_from_game(thread);
    }

    pthread_mutex_lock(&thread_list_lock);
    remove_from_list(&thread_list, thread->client_id, thread_id_equals);
    pthread_mutex_unlock(&thread_list_lock);
    close(thread->sock);
    pthread_mutex_destroy(&thread->thread_lock);
    free(thread);
    pthread_exit(NULL);
}

/**
    Odešle aktuální seznam her klientovi
    (klient odpoví potvrzením o příjmu dat).
*/
void send_game_list(thread_t *thread) {
    int32_t code = htonl(MSG_CODE);
    write_bytes(thread, &code, sizeof(code));

    linked_list_t *current_node = game_list;

    int32_t game_count = 0;
    while (current_node != NULL) {
        game_t *game = next_element(&current_node);

        if (!(game->running) || game == thread->game) {
            game_count++;
        }
    }

    game_count = htonl(game_count);
    write_bytes(thread, &game_count, sizeof(game_count));
    //print("Klient %d: odeslán počet her v seznamu.", thread->client_id);

    int32_t current_game_id = ((thread->game == NULL) ? htonl(0)
        : htonl(thread->game->game_id));
    write_bytes(thread, &current_game_id, sizeof(current_game_id));
    //print("Klient %d: odesláno ID aktuálně zvolené hry.", thread->client_id);

    current_node = game_list;

    // odeslání seznamu běžících her
    while (current_node != NULL) {
        game_t *game = next_element(&current_node);

        if (!(game->running) || game == thread->game) {
            int32_t game_id = htonl(game->game_id);
            write_bytes(thread, &game_id, sizeof(game_id));
            write_bytes(thread, &(game->matrix_size), sizeof(game->matrix_size));
            write_bytes(thread, &(game->win_row_len), sizeof(game->win_row_len));
            write_bytes(thread, &(game->players_size), sizeof(game->players_size));
            write_bytes(thread, &(game->player_counter), sizeof(game->player_counter));
            byte_t r = (game->running ? 1 : 0);
            write_bytes(thread, &r, sizeof(r));

            /*print("Klient %d: odeslány informace o hře s ID %d.",
                thread->client_id, game->game_id);*/
        }
    }

    //print("Klient %d: odeslán seznam vytvořených her.", thread->client_id);
}

/**
    Odešle aktuální stav hry klientovi
    (klient odpoví potvrzením o příjmu dat).
*/
void send_status(thread_t *thread) {
    int32_t code = htonl(MSG_CODE);
    write_bytes(thread, &code, sizeof(code));

    write_bytes(thread, &(thread->player_id),
        sizeof(thread->player_id));
    //print("Klient %d: odesláno pořadí ve hře.", thread->client_id);

    byte_t r = (thread->game->running ? 1 : 0);
    write_bytes(thread, &r, sizeof(r));
    /*print("Klient %d: odeslán příznak běhu hry: %s",
        thread->client_id, thread->game->running ? "rozehraná" : "nerozehraná");*/

    write_bytes(thread, &(thread->game->current_player),
        sizeof(thread->game->current_player));
    /*print("Klient %d: odesláno pořadí aktuálního hráče: %d",
        thread->client_id, thread->game->current_player);*/

    write_bytes(thread, &(thread->game->last_added_player),
        sizeof(thread->game->last_added_player));
    /*print("Klient %d: odeslána herní pozice posledního přidaného hráče: %d",
        thread->client_id, thread->game->last_added_player);*/

    write_bytes(thread, &(thread->game->last_removed_player),
        sizeof(thread->game->last_removed_player));
    /*print("Klient %d: odeslána herní pozice posledního odebraného hráče: %d",
        thread->client_id, thread->game->last_removed_player);*/

    write_bytes(thread, &(thread->game->last_move_player),
        sizeof(thread->game->last_move_player));
    /*print("Klient %d: odeslána herní pozice posledního hráče, který provedl tah: %d",
        thread->client_id, thread->game->last_move_player);*/

    if (thread->game->last_move_player != 0) {
        write_bytes(thread, &(thread->game->last_move_x),
            sizeof(thread->game->last_move_x));
        /*print("Klient %d: odeslána x-ová souřadnice posledního tahu: %d",
            thread->client_id, thread->game->last_move_x);*/

        write_bytes(thread, &(thread->game->last_move_y),
            sizeof(thread->game->last_move_y));
        /*print("Klient %d: odeslána y-ová souřadnice posledního tahu: %d",
            thread->client_id, thread->game->last_move_y);*/
    }

    write_bytes(thread, &(thread->game->winner),
        sizeof(thread->game->winner));
    /*print("Klient %d: odeslán vítězný hráč: %d",
        thread->client_id, thread->game->winner);*/

    if (thread->game->winner != 0) {
        //print("Vítězem je hráč %d.\n", (int)thread->game->winner);
        write_bytes(thread, thread->game->win_row, thread->game->win_row_len * 2);
    }
}
