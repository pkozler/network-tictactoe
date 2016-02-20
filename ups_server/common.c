#include "common.h"

#include "defs.h"
#include "logging.h"
#include "linkedlist.h"
#include "logging.h"

#include <stdbool.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>

#include <netinet/in.h>
#include <sys/socket.h>
#include <arpa/inet.h>

/*
    modul obsahující některé pomocné funkce a funkce pro komunikaci s klientem
    nebo manipulaci se stavem hry používané oběma obslužnými vlákny
*/

/**
    Přidá hráče do hry a přiřadí mu herní ID.
*/
void add_player_to_game(game_t *game, thread_t *thread) {
    thread->player_id = add_player(game, thread);
}

/**
    Odstraní hráče ze hry a při opuštění hry všemi hráči
    případně odstraní samotnou hru.
*/
void remove_player_from_game(thread_t *thread) {
    game_t *tmp_game = thread->game;

    if (remove_player(thread->game, thread) == 0) {
        pthread_mutex_lock(&game_list_lock);
        remove_from_list(&game_list, tmp_game->game_id, game_id_equals);
        pthread_mutex_unlock(&game_list_lock);
        delete_game(tmp_game);
    }
}

/**
    Přijímá části zprávy dokud není přijata celá zpráva
    o požadované délce. Pokud není v definovaném čase nic přijato
    (ani část zprávy, ani potvrzení), označí spojení s klientem
    jako přerušené za účelem ukončení tohoto spojení.
*/
void read_bytes(thread_t *thread, void *buf, unsigned int len) {
    int bytes_read = 0;
    int read_result = 0;

    while (bytes_read < len) {
        read_result = recv(thread->sock, buf + bytes_read, len - bytes_read, 0);

        if (read_result < 0) {
            print("Klient %d: spojení přerušeno.",
                thread->client_id);
            thread->connected = false;
            pthread_exit(NULL);
        }

        bytes_read += read_result;
    }
}

/**
    Odesílá části zprávy dokud není odeslána celá zpráva
    o požadované délce.
*/
void write_bytes(thread_t *thread, void *buf, unsigned int len) {
    int bytes_wrote = 0;
    int write_result = 0;

    while (bytes_wrote < len) {
        write_result = send(thread->sock, buf + bytes_wrote, len - bytes_wrote, 0);

        if (write_result < 0) {
            print("Klient %d: spojení přerušeno.",
                thread->client_id);
            thread->connected = false;
            pthread_exit(NULL);
        }

        bytes_wrote += write_result;
    }
}

/**
    Ověří, zda je hráč na řadě.
*/
bool can_play(thread_t *thread) {
    return (thread->player_id == thread->game->current_player);
}
