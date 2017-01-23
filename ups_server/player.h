/* 
 * Hlavičkový soubor player definuje strukuru hráče a deklaruje
 * funkce pro její vytvoření a odstranění.
 * 
 * Author: Petr Kozler
 */

#ifndef PLAYER_H
#define PLAYER_H

#include "message.h"
#include "game.h"
#include "client_socket.h"
#include <pthread.h>
#include <stdbool.h>
#include <stdint.h>

struct GAME;

/**
 * Struktura hráče - klienta.
 */
typedef struct PLAYER {
    pthread_t thread; // vlákno pro čtení požadavků klienta
    client_socket_t *socket; // struktura socketu klienta
    
    /*
     * základní informace o připojeném klientovi odesílané v položkách seznamu hráčů:
     */
    int32_t id; // ID hráče
    char *nick; // přezdívka hráče
    struct GAME *current_game; // herní místnost, ve které se hráč nachází
    int8_t current_game_index; // pořadí v aktuální hře (čísluje se od 1!)
    int32_t current_game_score; // skóre v aktuální hře
    int32_t total_score; // celkové skóre
} player_t;

player_t *create_player(int sock);
void delete_player(player_t *player);

#endif /* PLAYER_H */

