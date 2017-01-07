/* 
 * Modul com_stats definuje funkce pro počítání statistik běhu serveru.
 * 
 * Author: Petr Kozler
 */

#include "com_stats.h"
#include "global.h"
#include <stdint.h>

/**
 * Přičte zadaný počet přenesených bajtů do statistiky.
 * 
 * @param bytes_transferred počet přenesených bajtů
 */
void inc_stats_bytes_transferred(int32_t bytes_transferred) {
    g_server_info.stats.bytes_transferred += bytes_transferred;
}

/**
 * Inkrementuje počet navázaných spojení ve statistice.
 */
void inc_stats_connections_established() {
    g_server_info.stats.connections_established++;
}

/**
 * Inkrementuje počet přenesených zpráv ve statistice.
 */
void inc_stats_messages_transferred() {
    g_server_info.stats.messages_transferred++;
}

/**
 * Inkrementuje počet spojení zrušených pro chybu ve statistice.
 */
void inc_stats_transfers_failed() {
    g_server_info.stats.transfers_failed++;
}

/**
 * Vynuluje statistiky
 */
void clear_stats() {
    g_server_info.stats.bytes_transferred = 0;
    g_server_info.stats.connections_established = 0;
    g_server_info.stats.messages_transferred = 0;
    g_server_info.stats.transfers_failed = 0;
    g_server_info.stats.start_time.tv_sec = 0;
    g_server_info.stats.start_time.tv_usec = 0;
}
