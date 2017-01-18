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
#include <pthread.h>
#include <stdbool.h>
#include <stdint.h>

struct GAME;

/**
 * Struktura hráče - klienta.
 */
typedef struct PLAYER {
    pthread_t thread; // vlákno pro čtení požadavků klienta
    pthread_mutex_t lock; // zámek strukury klienta
    int sock; // deskriptor socketu klienta
    bool connected; // příznak připojení klienta
    
    /*
     * základní informace o připojeném klientovi odesílané v položkách seznamu hráčů:
     */
    int32_t id; // ID hráče
    char *nick; // přezdívka hráče
    struct GAME *current_game; // herní místnost, ve které se hráč nachází
    int8_t current_game_index; // pořadí v aktuální hře
    int32_t current_game_score; // skóre v aktuální hře
    int32_t total_score; // celkové skóre
    bool playing; // příznak, zda se hráč účastní aktuálního kola hry
} player_t;

player_t *create_player(int sock);
void delete_player(player_t *player);
void lock_player(player_t *player);
void unlock_player(player_t *player);

#endif /* PLAYER_H */

