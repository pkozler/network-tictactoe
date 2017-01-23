/* 
 * Modul prompt definuje funkce pro zpracování příkazů uživatele
 * z konzole.
 * 
 * Author: Petr Kozler
 */

#include "tcp_server.h"
#include "global.h"
#include "config.h"
#include "prompt.h"
#include "logger.h"
#include "server_control.h"
#include "message.h"
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
 * Načte potvrzení uživatele o ukončení programu.
 * 
 * @param buf buffer pro uložení načtené odpovědi
 * @return true, pokud uživatel nezrušil příkaz k ukončení, jinak false
 */
bool enter_exit_answer(char *buf) {
    if (!strcmp("y", buf)) {
        stop_server();
    }
    else if (!strcmp("n", buf)) {
        print_cmd_result(print_commands);
        
        return false;
    }
    
    return true;
}

/**
 * Provede příkaz uživatele.
 * 
 * @param buf buffer pro uložení načteného příkazu
 * @return true, pokud uživatel zadal příkaz k ukončení programu, jinak false
 */
bool perform_server_cmd(char *buf) {
    bool exiting = false;
    
    print_func_t print_func = NULL;

    if (!strcmp(ARGS_CMD, buf)) {
        print_func = print_args;
    }
    else if (!strcmp(STATS_CMD, buf)) {
        print_func = print_stats;
    }
    else if (!strcmp(HELP_CMD, buf)) {
        print_func = print_commands;
    }
    else if (!strcmp(EXIT_CMD, buf)) {
        print_func = print_exit_question;
        exiting = true;
    }
    else {
        print_func = print_unknown_cmd;
    }

    if (print_func != NULL) {
        print_cmd_result(print_func);
    }
    
    return exiting;
}

/**
 * Vstupní bod vlákna pro čtení příkazů uživatele. Periodicky načítá a spouští
 * příkazy z konzole, dokud není čtecí vlákno ukončeno hlavním vláknem
 * při zastavení serveru.
 * 
 * @param arg argument
 */
void *run_prompt(void *arg) {
    bool exiting = false;
    print_cmd_result(print_commands);
    
    while (is_server_running()) {
        char buf[CMD_MAX_LENGTH];
        fgets(buf, CMD_MAX_LENGTH, stdin);
        
        char *pos;
        if ((pos = strchr(buf, '\n')) != NULL) {
            *pos = '\0';
        }
        
        if (exiting) {
            exiting = enter_exit_answer(buf);
        }
        else {
            exiting = perform_server_cmd(buf);
        }
    }
    
    
    print_cmd_result(print_stats);
    print_out("Ukončuji server");
    
    return NULL;
}

/**
 * Vypíše seznam dostupných příkazů a spustí vlákno pro načítání a spouštění
 * příkazů uživatele z konzole.
 */
void start_prompt() {
    if (pthread_create(&g_cmd_thread, NULL, run_prompt, NULL) < 0) {
        print_err("Chyba při vytváření vlákna pro čtení příkazů");
    }
}
