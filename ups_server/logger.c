/* 
 * Modul logger definuje funkce pro logování do souboru.
 * 
 * Author: Petr Kozler
 */

#include "logger.h"
#include "config.h"
#include "global.h"
#include "tcp_server_control.h"
#include "printer.h"
#include <stdbool.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <pthread.h>

/**
 * Vloží zadaný formátovaný řetězec, představující záznam logu,
 * do fronty pro zápis do logovacího souboru.
 * Slouží k zaznamenávání činnosti pracovních vláken serveru
 * a trasování síťové komunikace.
 * 
 * @param format formát řetězce záznamu logu
 * @param ... argumenty řetězce záznamu logu
 */
void append_log(const char *format, ...) { 
    va_list vargs;
    va_start(vargs, format);
    char *log_str = (char *) malloc(sizeof(char) * MAX_STR_LENGHT);
    vsnprintf(log_str, sizeof(char) * MAX_STR_LENGHT, format, vargs);
    va_end(vargs);
    
    // vložení záznamu do fronty (atomická operace)
    pthread_mutex_lock(&(g_logger->lock));
    enqueue_element(g_logger->log_queue, log_str);
    pthread_mutex_unlock(&(g_logger->lock));
}

/**
 * Postupně vybere všechny záznamy logu aktuálně čekající ve frontě a provede
 * jejich zápis do logovacího souboru v pořadí, v jakém byly vloženy.
 */
void write_logs() {
    while (!is_linked_list_empty(g_logger->log_queue)) {
        // výběr záznamu z fronty (atomická operace)
        pthread_mutex_lock(&(g_logger->lock));
        char *log_str = dequeue_element(g_logger->log_queue);
        pthread_mutex_unlock(&(g_logger->lock));
        
        // zápis a odstranění řetězce
        fputs(log_str, g_logger->log_file);
        free(log_str);
    }
}

/**
 * Vstupní bod logovacího vlákna. Periodicky spouští zápis záznamů vkládaných
 * pracovními vlákny do souboru logu, dokud není logovací vlákno ukončeno
 * hlavním vláknem při zastavení serveru.
 * 
 * @param arg argument
 */
void *run_logging(void *arg) {
    while (is_server_running()) {
        write_logs();
    }
    
    return NULL;
}

/**
 * Inicializuje strukturu loggeru, vytvoří logovací soubor na zadané cestě
 * (nebo otevře již existující) a spustí vlákno pro logování.
 * Vkládání řetězců logu probíhá v ostatních vláken programu, jejich následný
 * výběr v odpovídajícím pořadí a samotný zápis do logovacího souboru pak 
 * obstarává logovací vlákno (model producent-konzument). To zajišťuje, že
 * zápis logů nezpůsobuje zpoždění síťové komunikace a dalších operací serveru.
 * 
 * @param log_file_name cesta k souboru pro logování
 */
void start_logging(char *log_file_name) {
    g_logger = (logger_t *) malloc(sizeof(logger_t));
    g_logger->log_file = fopen(log_file_name, "a");
    
    if (g_logger->log_file == NULL) {
        print_err("Chyba při otevírání souboru pro zápis logů");
    }

    g_logger->log_queue = create_linked_list();
    
    if (pthread_mutex_init(&(g_logger->lock), NULL) < 0) {
        print_err("Chyba při vytváření zámku pro zápis logů");
    }
    
    if (pthread_create(&(g_logger->thread), NULL, run_logging, NULL) < 0) {
        print_err("Chyba při vytváření vlákna pro zápis logů");
    }
}

/**
 * Dokončí zápis logů aktuálně uložených ve frontě, ukončí logovací vlákno
 * a uzavře logovací soubor.
 */
void shutdown_logging() {
    pthread_mutex_destroy(&(g_logger->lock));
    
    // výpis zbylých logů ve frontě po ukončení serveru
    write_logs();
    delete_linked_list(g_logger->log_queue, NULL);
    fclose(g_logger->log_file);
    free(g_logger);
}
