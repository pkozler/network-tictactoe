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
#include <stdio.h>
#include <sys/time.h>
#include <unistd.h>

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
 * Vypíše seznam dostupných příkazů se stručným popisem jejich činnosti.
 */
void print_commands() {
    printf("Dostupné příkazy:\n");
    printf("   %s ... výpis nastavení\n", ARGS_CMD);
    printf("   %s ... výpis statistik\n", STATS_CMD);
    printf("   %s ... nápověda příkazů\n", HELP_CMD);
    printf("   %s ... ukončení programu\n", EXIT_CMD);
}

/**
 * Vypíše parametry pro aktuální spuštění serveru.
 */
void print_args() {
    printf("Nastavené parametry serveru:\n");
    printf("IP adresa pro naslouchání: %s\n", g_server_info.args.host);
    printf("Císlo portu pro naslouchání: %d\n", g_server_info.args.port);
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
 * Zobrazí dialog pro ukončení serveru.
 */
void print_exit_question() {
    printf("Opravdu chcete ukončit server? (y/n)\n");
}

/**
 * Vypíše hlášení o zadání neznámého příkazu do konzole.
 */
void print_unknown_cmd() {
    printf("Neznámý příkaz.\n");
}

/**
 * Zavolá předanou funkci pro výpis do konzole, který ohraničí znaky za účelem
 * vizuálního oddělení výpisu výsledků zpracování uživatelských příkazů
 * a automaticky zapisovaných logů o probíhající komunikaci s klienty.
 * 
 * @param print_func funkce pro výpis
 */
void print_cmd_result(print_func_t print_func) {
    printf("========================================\n");
    print_func();
    printf("========================================\n");
}
