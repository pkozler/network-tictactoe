/* 
 * Author: Petr Kozler
 */

#include "tcp_server_info.h"

#include <sys/time.h>

struct {
    args_t args; // struktura uchovávající aktuální nastavení serveru
    
    /**
     * Struktura uchovávající statistiky běhu serveru.
     */
    struct {
        uint32_t bytes_transferred; // přenesený počet bytů
        uint32_t messages_transferred; // přenesený počet zpráv
        uint32_t connections_established; // počet navázaných spojení
        uint32_t transfers_failed; // počet přenosů zrušených pro chybu
        struct timeval start_time; // čas spuštění serveru
    } stats;
    
    bool running; // příznak probíhajícího navazování spojení a komunikace s klienty
} g_tcp_server_info;

bool is_server_running() {
    return g_tcp_server_info.running;
}

void start_server() {
    // uložení času spuštění
    gettimeofday(&(g_tcp_server_info.stats.start_time), NULL);
    g_tcp_server_info.running = true;
}

/**
 * Zastaví server, který může být poté opět spuštěn se zadáním nového nastavení.
 * Všechny v paměti uložené záznamy serveru o aktuálně připojených hráčích
 * a vytvořených hrách jsou odstraněny a všechny statistiky komunikace vynulovány.
 */
void stop_server() {
    g_tcp_server_info.running = false;
}

/**
 * Vypíše parametry pro aktuální spuštění serveru.
 */
void print_args() {
    printf("Nastavené parametry serveru:\n");
    printf("IP adresa pro naslouchání: %s\n", g_tcp_server_info.args.host);
    printf("Císlo portu pro naslouchání: %d\n", g_tcp_server_info.args.port);
    printf("Cesta k logovacímu souboru: %s\n", g_tcp_server_info.args.log_file);
    printf("Délka fronty pro příchozí spojení: %d\n", g_tcp_server_info.args.queue_length);
}

/**
 * Vypíše statistiky aktuálně probíhající komunikace serveru.
 */
void print_stats() {
    // zjištění aktuálního času a výpočet doby běhu v sekundách
    struct timeval current_time;
    gettimeofday(&current_time, NULL);
    double elapsed_time = (current_time.tv_sec - g_tcp_server_info.stats.start_time.tv_sec) +
                          (current_time.tv_usec - g_tcp_server_info.stats.start_time.tv_usec) / 1000000.0;
    
    printf("Statistika běhu serveru:\n");
    printf("Přenesený počet bytů: %o\n", g_tcp_server_info.stats.bytes_transferred);
    printf("Přenesený počet zpráv: %o\n", g_tcp_server_info.stats.messages_transferred);
    printf("Počet navázaných spojení: %o\n", g_tcp_server_info.stats.connections_established);
    printf("Počet přenosů zrušených pro chybu: %o\n", g_tcp_server_info.stats.transfers_failed);
    printf("Doba běhu: %f\n", elapsed_time);
}

void add_stats_bytes_transferred(int32_t bytes_transferred) {
    g_tcp_server_info.stats.bytes_transferred += bytes_transferred;
}

void add_stats_connections_established(int32_t connections_established) {
    g_tcp_server_info.stats.connections_established += connections_established;
}

void add_stats_messages_transferred(int32_t messages_transferred) {
    g_tcp_server_info.stats.messages_transferred += messages_transferred;
}

void add_stats_transfers_failed(int32_t transfers_failed) {
    g_tcp_server_info.stats.transfers_failed += transfers_failed;
}

void clear_stats() {
    g_tcp_server_info.stats.bytes_transferred = 0;
    g_tcp_server_info.stats.connections_established = 0;
    g_tcp_server_info.stats.messages_transferred = 0;
    g_tcp_server_info.stats.transfers_failed = 0;
    g_tcp_server_info.stats.start_time.tv_sec = 0;
    g_tcp_server_info.stats.start_time.tv_usec = 0;
}