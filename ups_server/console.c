/* 
 * Author: Petr Kozler
 */

#include "tcp_server.h"
#include "global.h"
#include "config.h"
#include "console.h"
#include "tcp_server_control.h"
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
    while (is_server_running()) {
        char buf[CMD_MAX_LENGTH];
        fgets(buf, CMD_MAX_LENGTH, stdin);

        if (!strcmp(ARGS_CMD, buf)) {
            print_args();
        }
        else if (!strcmp(STATS_CMD, buf)) {
            print_stats();
        }
        else if (!strcmp(RESET_CMD, buf)) {
            stop_server();
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
