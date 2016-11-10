/* 
 * Author: Petr Kozler
 */

#include "connection.h"
#include "config.h"
#include "console.h"
#include "connection_stats.h"
#include "message.h"
#include "printer.h"
#include "logger.h"
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <arpa/inet.h>

#define HOST_MAX_LEN 15 // maximální délka řetězce IP adresy pro naslouchání
#define PORT_MAX_LEN 5 // maximální délka řetězce portu pro naslouchání
#define LOG_MAX_LEN 2000 // maximální délka cesty k souboru logu
#define CMD_MAX_LEN 10 // maximální řetězce příkazu uživatele
#define STATS_CMD "stats" // příkaz pro výpis statistik serveru
#define RESET_CMD "reset" // příkaz pro restart serveru s novými parametry
#define HELP_CMD "help" // příkaz pro výpis všech dostupných příkazů
#define EXIT_CMD "exit" // příkaz pro zastavení komunikace a ukončení programu


/**
 * Zobrazí dialog pro zadání IP adresy pro naslouchání příchozím spojením
 * s klienty (nebo řetězce, který symbolizuje naslouchání ze všech IP adres).
 * Zadaný řetězec poté zkontroluje a je-li platný, vrátí odpovídající hodnotu
 * v podobě celého čísla pro použití v komunikačních funkcích programu.
 * Výzva k zadání je vypisována, dokud není zadán platný vstup.
 * Řetězec může být předán funkci i jako parametr programu. V takovém případě
 * proběhne nejprve validace před případným zobrazením výzvy k opravě zadání.
 * 
 * @param host_arg řetězec IP adresy pro naslouchání
 * @return platná IP adresa jako 32-bitové celé číslo
 */
int32_t parse_host(char *host_arg) {
    while (true) {
        int32_t host = 0;
        
        if (host_arg != NULL) {
            if (strcmp(ANY_IP_STR, host_arg)) {
                print_out("Server bude přijímat spojení z libovolné adresy.\n");
                
                return INADDR_ANY;
            }

            if (inet_pton(AF_INET, host_arg, ((void *) &host))) {
                printf("Server bude přijímat spojení z adresy %s.\n", host_arg);
                host = htonl(host);
                
                return host;
            }
            
            printf("Neplatný formát IP adresy. IP adresa musí být ve tvaru \"#.#.#.#\", kde \"#\" je celé číslo v rozsahu 0 - 255.\n");
            free(host_arg);
        }
        
        host_arg = (char *) malloc(sizeof(char) * HOST_MAX_LEN + 1);
        printf("Zadejte IP adresu pro naslouchání nebo \"%s\" pro naslouchání na všech adresách a stiskněte ENTER:\n", ANY_IP_STR);
        fgets(host_arg, HOST_MAX_LEN, stdin);
    }
}

/**
 * Zobrazí dialog pro zadání portu pro naslouchání příchozím spojením
 * s klienty.
 * Zadaný řetězec poté zkontroluje a je-li platný, vrátí odpovídající hodnotu
 * v podobě celého čísla pro použití v komunikačních funkcích programu.
 * Výzva k zadání je vypisována, dokud není zadán platný vstup.
 * Řetězec může být předán funkci i jako parametr programu. V takovém případě
 * proběhne nejprve validace před případným zobrazením výzvy k opravě zadání.
 * 
 * @param port_arg řetězec portu pro naslouchání
 * @return platný port jako 32-bitové celé číslo
 */
int32_t parse_port(char *port_arg) {
    while (true) {
        int32_t port = 0;
        
        if (port_arg != NULL) {
            int32_t port = strtol(port_arg, NULL, 10);
            
            if (port >= MIN_PORT && port <= MAX_PORT) {
                printf("Server bude přijímat spojení na portu %d.\n", port);
                
                return port;
            }
            
            printf("Neplatný formát čísla portu. Port musí být celé číslo v rozsahu %d - %d\n", MIN_PORT, MAX_PORT);
            free(port_arg);
        }
        
        port_arg = (char *) malloc(sizeof(char) * PORT_MAX_LEN + 1);
        printf("Zadejte číslo portu pro naslouchání a stiskněte ENTER:\n");
        fgets(port_arg, PORT_MAX_LEN, stdin);
    }
}

/**
 * Zobrazí dialog pro zadání cesty k výstupnímu souboru pro zápis logů.
 * Zadaný řetězec poté zkontroluje a je-li platný, je předán zpět
 * v návratové hodnotě.
 * Výzva k zadání je vypisována, dokud není zadán platný vstup.
 * Řetězec může být předán funkci i jako parametr programu. V takovém případě
 * proběhne nejprve validace před případným zobrazením výzvy k opravě zadání.
 * 
 * @param log_arg cesta k souboru logu
 * @return platná cesta k souboru
 */
char *parse_log(char *log_arg) {
    while (true) {
        if (log_arg != NULL) {
            char invalid_chars[] = "!@%^*~|";
            bool valid_path = true;
            int i;
            for (i = 0; i < strlen(invalid_chars); ++i) {
                if (strchr(log_arg, invalid_chars[i]) != NULL) {
                    valid_path = false;

                    break;
                }
            }
            
            if (valid_path) {
                printf("Server bude zapisovat logy do souboru %s.\n", log_arg);
                
                return log_arg;
            }
            
            printf("Neplatný formát cesty k souboru.\n");
        }
        
        log_arg = (char *) malloc(sizeof(char) * LOG_MAX_LEN + 1);
        printf("Zadejte název souboru pro logování včetně cesty a stiskněte ENTER:\n");
        fgets(log_arg, LOG_MAX_LEN, stdin);
    }
}

/**
 * Vypíše statistiky aktuálně probíhající komunikace serveru.
 */
void print_stats() {
    // zjištění aktuálního času a výpočet doby běhu v sekundách
    struct timeval current_time;
    gettimeofday(&current_time, NULL);
    double elapsed_time = (current_time.tv_sec - g_stats.start_time.tv_sec) +
                          (current_time.tv_usec - g_stats.start_time.tv_usec) / 1000000.0;
    
    printf("Statistika běhu serveru:\n");
    printf("Přenesený počet bytů: %o\n", g_stats.bytes_transferred);
    printf("Přenesený počet zpráv: %o\n", g_stats.messages_transferred);
    printf("Počet navázaných spojení: %o\n", g_stats.connections_established);
    printf("Počet přenosů zrušených pro chybu: %o\n", g_stats.transfers_failed);
    printf("Doba běhu: %f\n", elapsed_time);
}

/**
 * Rozešle aktuálně připojeným klientům zprávu o zastavení komunikace
 * a restartuje server se zadáním nových vstupních parametrů.
 * Všechny v paměti uložené záznamy serveru o aktuálně připojených hráčích
 * a vytvořených hrách jsou odstraněny a všechny statistiky serveru vynulovány.
 */
void restart_server() {
    g_running = false;
}

/**
 * Vypíše seznam dostupných příkazů se stručným popisem jejich činnosti.
 */
void print_commands() {
    printf("Dostupné příkazy:\n");
    printf("   %s ... výpis statistik\n", STATS_CMD);
    printf("   %s ... restart serveru\n", RESET_CMD);
    printf("   %s  ... nápověda příkazů\n", EXIT_CMD);
    printf("   %s  ... ukončení programu\n", EXIT_CMD);
}

/**
 * Vstupní bod vlákna pro čtení příkazů uživatele. Periodicky načítá a spouští
 * příkazy z konzole, dokud není čtecí vlákno ukončeno hlavním vláknem
 * při zastavení serveru.
 * 
 * @param arg argument
 */
void *run_prompt(void *arg) {
    while (true) {
        char buf[CMD_MAX_LEN];
        fgets(buf, CMD_MAX_LEN, stdin);

        if (!strcmp(STATS_CMD, buf)) {
            print_stats();
        }
        else if (!strcmp(RESET_CMD, buf)) {
            restart_server();
        }
        else if (!strcmp(HELP_CMD, buf)) {
            print_commands();
        }
        else if (!strcmp(EXIT_CMD, buf)) {
            exit(EXIT_SUCCESS);
        }
        else {
            printf("Neznámý příkaz.\n");
        }
    }
    
    return NULL;
}

/**
 * Vypíše seznam dostupných příkazů a spustí vlákno pro načítání a spouštění
 * příkazů uživatele z konzole.
 */
void start_prompt() {
    print_commands();
    
    if (pthread_create(&g_cmd_thread, NULL, run_prompt, NULL) < 0) {
        print_err("Chyba při vytváření vlákna pro čtení příkazů");
    }
}

/**
 * Ukončí vlákno pro čtení příkazů z konzole.
 */
void shutdown_prompt() {
    pthread_cancel(g_cmd_thread);
}