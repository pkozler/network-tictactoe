/* 
 * Hlavičkový soubor global obsahuje globální proměnné používané
 * v různých částech programu.
 * 
 * Author: Petr Kozler
 */

#ifndef GLOBAL_H
#define GLOBAL_H

#include "cmd_arg.h"
#include "com_stats.h"
#include "logger.h"
#include "observable_list.h"
#include "linked_list.h"
#include <pthread.h>

/**
 * Struktura informací o aktuální instanci serveru.
 */
struct {
    args_t args; // struktura uchovávající aktuální nastavení serveru
    stats_t stats; // struktura uchovávající statistiky běhu serveru
    bool running; // příznak probíhajícího navazování spojení a komunikace s klienty
} g_server_info;

// vlákno pro načítání příkazů uživatele z konzole při probíhající komunikaci
pthread_t g_cmd_thread;
// instance loggeru
logger_t *g_logger;
// seznam vytvořených her
observable_list_t *g_game_list;
// seznam přihlášených hráčů
observable_list_t *g_player_list;
// seznam všech připojených klientů (včetně nepřihlášených)
linked_list_t *g_client_list;

#endif /* GLOBAL_H */

