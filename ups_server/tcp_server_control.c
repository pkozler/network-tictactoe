/* 
 * Modul tcp_server_control definuje funkce pro ovládání aktuální
 * instance serveru a výpis informací.
 * 
 * Author: Petr Kozler
 */

#include "tcp_server_control.h"
#include "global.h"
#include "config.h"
#include <stdint.h>
#include <stdlib.h>
#include <sys/time.h>

/**
 * Otestuje, zda server běží.
 * 
 * @return true, pokud server běží, jinak false
 */
bool is_server_running() {
    return g_server_info.running;
}

/**
 * Spustí server se zadaným nastavením. Následně jsou vytvořeny datové struktury
 * pro záznam aktuálně připojených hráčů a vytvořených her.
 */
void start_server() {
    // uložení času spuštění
    gettimeofday(&(g_server_info.stats.start_time), NULL);
    g_server_info.running = true;
}

/**
 * Zastaví server, který může být poté opět spuštěn se zadáním nového nastavení.
 * Všechny v paměti uložené záznamy serveru o aktuálně připojených hráčích
 * a vytvořených hrách jsou odstraněny a všechny statistiky komunikace vynulovány.
 */
void stop_server() {
    g_server_info.running = false;
}

/**
 * Vypíše parametry pro aktuální spuštění serveru.
 */
void print_args() {
    printf("Nastavené parametry serveru:\n");
    printf("IP adresa pro naslouchání: %s\n", g_server_info.args.host);
    printf("Císlo portu pro naslouchání: %d\n", g_server_info.args.port);
    printf("Cesta k logovacímu souboru: %s\n", g_server_info.args.log_file);
    printf("Délka fronty pro příchozí spojení: %d\n", g_server_info.args.queue_length);
}

/**
 * Vypíše statistiky aktuálně probíhající komunikace serveru.
 */
void print_stats() {
    // zjištění aktuálního času a výpočet doby běhu v sekundách
    struct timeval current_time;
    gettimeofday(&current_time, NULL);
    double elapsed_time = (current_time.tv_sec - g_server_info.stats.start_time.tv_sec) +
                          (current_time.tv_usec - g_server_info.stats.start_time.tv_usec) / 1000000.0;
    
    printf("Statistika běhu serveru:\n");
    printf("Přenesený počet bytů: %o\n", g_server_info.stats.bytes_transferred);
    printf("Přenesený počet zpráv: %o\n", g_server_info.stats.messages_transferred);
    printf("Počet navázaných spojení: %o\n", g_server_info.stats.connections_established);
    printf("Počet přenosů zrušených pro chybu: %o\n", g_server_info.stats.transfers_failed);
    printf("Doba běhu: %f\n", elapsed_time);
}

/**
 * Načte nové parametry při restartu serveru.
 */
void scan_args() {
    char *host;
    int32_t port;
    char *log_file;
    int32_t queue_length;
    
    printf("Zadejte IP adresu pro naslouchání (\"%s\"  pro naslouchání na všech adresách)\n",
            DEFAULT_HOST);
    scanf("%s", &host);
    printf("Zadejte číslo portu v rozsahu %d - %d\n",
            MIN_PORT, MAX_PORT);
    scanf("%d", &port);
    printf("Zadejte název souboru pro logování včetně cesty\n");
    scanf("%s", &log_file);
    printf("Zadejte délku fronty v rozsahu %d - %d\n",
            MIN_QUEUE_LENGTH, MAX_QUEUE_LENGTH);
    scanf("%d", &queue_length);
    
    g_server_info.args.host = host;
    g_server_info.args.port = port;
    g_server_info.args.log_file = log_file;
    g_server_info.args.queue_length = queue_length;
}