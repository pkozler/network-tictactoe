/* 
 * Author: Petr Kozler
 */

#ifndef CONNECTION_STATS_H
#define CONNECTION_STATS_H

#include <stdbool.h>
#include <stdint.h>
#include <sys/time.h>

bool g_running; // příznak probíhajícího navazování spojení a komunikace s klienty

/**
 * Struktura uchovávající statistiky běhu serveru.
 */
struct {
    uint32_t bytes_transferred; // přenesený počet bytů
    uint32_t messages_transferred; // přenesený počet zpráv
    uint32_t connections_established; // počet navázaných spojení
    uint32_t transfers_failed; // počet přenosů zrušených pro chybu
    struct timeval start_time; // čas spuštění serveru
} g_stats;


#endif /* CONNECTION_STATS_H */

