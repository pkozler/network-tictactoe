/* 
 * Author: Petr Kozler
 */

#ifndef LOGGER_H
#define LOGGER_H

#include <stdarg.h>

/**
 * Struktura loggeru pro uchovávání záznamů o činnosti pracovních vláken
 * programu a jejich postupný zápis vyhrazeným vláknem do zvoleného souboru.
 */
typedef struct {
    linked_list_t *log_queue; // fronta pro ukládání logů
    pthread_t thread; // logovací vlákno pro výběr a zápis záznamů
    pthread_mutex_t lock; // zámek fronty záznamů
    FILE *log_file; // logovací soubor
} logger_t;

void log(const char *format, ...);
void start_logging(char *log_file_name);
void shutdown_logging();

#endif /* LOGGER_H */

