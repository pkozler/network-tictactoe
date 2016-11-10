/* 
 * Author: Petr Kozler
 */

#include "logger.h"
#include "printer.h"
#include <stdbool.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <pthread.h>

// maximální délka jednoho logovaného řetězce
#define MAX_LINE_LEN 65535

/**
 * Definice struktury uzlu jednosměrného spojového seznamu. Uzly jsou používány
 * pro uložení řetězců logu a jsou vkládány a vybírány ze seznamu stylem
 * FIFO (fronta).
 */
typedef struct STRING_NODE {
    struct STRING_NODE *next_str;
    char *str;
} string_node_t;

/**
 * Struktura loggeru pro uchovávání záznamů o činnosti pracovních vláken
 * programu a jejich postupný zápis vyhrazeným vláknem do zvoleného souboru.
 */
struct {
    int32_t str_count; // aktuální počet záznamů ve frontě
    string_node_t *first_str; // první uzel fronty (první záznam k zápisu)
    string_node_t *last_str; // poslední uzel fronty (poslední vložený záznam)
    pthread_t thread; // logovací vlákno pro výběr a zápis záznamů
    pthread_mutex_t lock; // zámek fronty záznamů
    FILE *file; // logovací soubor
} g_logger;

/**
 * Uloží zadaný řetězec logu do vytvořeného uzlu a vloží jej na konec fronty.
 * 
 * @param str řetězec logu
 */
void enqueue_log_string(char *str) {
    // vytvoření uzlu
    string_node_t *str_node = (string_node_t *) malloc(sizeof(string_node_t));
    str_node->str = str;
    
    if (g_logger.first_str == NULL) {
        g_logger.first_str = str_node;
        g_logger.last_str = str_node;
        str_node->next_str = NULL;
    }
    else {
        str_node->next_str = g_logger.last_str->next_str;
        g_logger.last_str = str_node;
        g_logger.last_str->next_str = str_node;
    }
    
    g_logger.str_count++;
}

/**
 * Odstraní uzel na začátku fronty a vrátí v něm uložený řetězec logu.
 * 
 * @return řetězec logu
 */
char *dequeue_log_string() {
    if (g_logger.first_str == NULL) {
        return NULL;
    }

    string_node_t *str_node = g_logger.first_str;
    g_logger.first_str = g_logger.first_str->next_str;
    char *str = str_node->str;

    // odstranění uzlu
    free(str_node);
    g_logger.str_count--;

    return str;
}

/**
 * Vloží zadaný formátovaný řetězec, představující záznam logu,
 * do fronty pro zápis do logovacího souboru.
 * Slouží k zaznamenávání činnosti pracovních vláken serveru
 * a trasování síťové komunikace.
 * 
 * @param format formát řetězce záznamu logu
 * @param ... argumenty řetězce záznamu logu
 */
void print_log(const char *format, ...) { 
    va_list vargs;
    
    // vytvoření a zformátování řetězce
    char *log_str = (char *) malloc(sizeof(char) * MAX_LINE_LEN);
    va_start(vargs, format);
    vsnprintf(log_str, MAX_LINE_LEN, format, vargs);
    va_end(vargs);
    
    // vložení záznamu do fronty (atomická operace)
    pthread_mutex_lock(&(g_logger.lock));
    enqueue_log_string(log_str);
    pthread_mutex_unlock(&(g_logger.lock));
}

/**
 * Postupně vybere všechny záznamy logu aktuálně čekající ve frontě a provede
 * jejich zápis do logovacího souboru v pořadí, v jakém byly vloženy.
 */
void write_logs() {
    while (g_logger.str_count > 0) {
        // výběr záznamu z fronty (atomická operace)
        pthread_mutex_lock(&(g_logger.lock));
        char *log_str = dequeue_log_string();
        pthread_mutex_unlock(&(g_logger.lock));
        
        // zápis a odstranění řetězce
        fputs(log_str, g_logger.file);
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
    while (true) {
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
    g_logger.file = fopen(log_file_name, "a");
    
    if (g_logger.file == NULL) {
        print_err("Chyba při otevírání souboru pro zápis logů");
    }

    g_logger.first_str = NULL;
    g_logger.last_str = NULL;
    g_logger.str_count = 0;
    
    if (pthread_mutex_init(&(g_logger.lock), NULL) < 0) {
        print_err("Chyba při vytváření zámku pro zápis logů");
    }
    
    if (pthread_create(&(g_logger.thread), NULL, run_logging, NULL) < 0) {
        print_err("Chyba při vytváření vlákna pro zápis logů");
    }
}

/**
 * Dokončí zápis logů aktuálně uložených ve frontě, ukončí logovací vlákno
 * a uzavře logovací soubor.
 */
void shutdown_logging() {
    pthread_cancel(g_logger.thread);
    pthread_mutex_destroy(&(g_logger.lock));
    
    // výpis zbylých logů ve frontě po ukončení serveru
    write_logs();
    fclose(g_logger.file);
}