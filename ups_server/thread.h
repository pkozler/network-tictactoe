#ifndef THREAD_H_INCLUDED
#define THREAD_H_INCLUDED

#include "defs.h"
#include "game.h"

#include <pthread.h>
#include <stdbool.h>

/*
    header s definicí struktury stavu hráče a deklaracemi funkcí
    pro její obsluhu
*/

/* alias pro 1-bajtový znak */
typedef signed char byte_t;

/* dopředná deklarace strukury hry */
struct GAME;

/**
    Struktura, která představuje aktuální stav hráče.
*/
typedef struct THREAD {
    pthread_t send_thread; // vlákno pro odesílání stavu klientovi a příjem potvrzení
    pthread_t recv_thread; // vlákno pro příjem uživatelských vstupů od klienta
    pthread_mutex_t thread_lock; // zámek pro volání komunikačních funkcí
    int sock; // deskriptor socketu příslušného klienta
    int32_t client_id; // unikátní klientské ID
    byte_t player_id; // aktuální ID hráče ve hře, ke které je připojen (index v poli players dané hry)
    struct GAME *game; // hra (místnost), ke které je hráč aktuálně připojen
    bool connected; // příznak připojení klienta (při nastavení na true budou obslužná vlákna ukončena)
} thread_t;

/* spuštění příjmu */
void *run_recv_thread(void *);
/* spuštění odesílání */
void *run_send_thread(void *);

#endif // THREAD_H_INCLUDED
