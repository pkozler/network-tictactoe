/* 
 * Author: Petr Kozler
 */

#include "com_stats.h"
#include "global.h"
#include <stdint.h>
#include <sys/time.h>

void inc_stats_bytes_transferred(int32_t bytes_transferred) {
    g_server_info.stats.bytes_transferred += bytes_transferred;
}

void inc_stats_connections_established() {
    g_server_info.stats.connections_established++;
}

void inc_stats_messages_transferred() {
    g_server_info.stats.messages_transferred++;
}

void inc_stats_transfers_failed() {
    g_server_info.stats.transfers_failed++;
}

void clear_stats() {
    g_server_info.stats.bytes_transferred = 0;
    g_server_info.stats.connections_established = 0;
    g_server_info.stats.messages_transferred = 0;
    g_server_info.stats.transfers_failed = 0;
    g_server_info.stats.start_time.tv_sec = 0;
    g_server_info.stats.start_time.tv_usec = 0;
}
