/* 
 * Modul printer definuje funkce pro výpis logů na konzoli.
 * 
 * Author: Petr Kozler
 */

#include "logger.h"
#include "config.h"
#include <error.h>
#include <errno.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

/**
 * Vypíše aktuální datum a čas.
 * 
 * @param buf buffer pro uložení formátovaného řetězce s časovým údajem
 * @param success true v případě běžného záznamu, false v případě záznamu o chybě
 */
void print_datetime(char *buf, bool success) {
    time_t timer;
    struct tm* tm_info;

    time(&timer);
    tm_info = localtime(&timer);

    strftime(buf, MAX_STR_LENGHT, "%Y-%m-%d %H:%M:%S", tm_info);
    
    if (!success) {
        fprintf(stderr, "[%s] ", buf);
        
        return;
    }
    
    printf("[%s] ", buf);
}

/**
 * Vypíše na obrazovku zadanou hlášku a ukončí řádek.
 * 
 * @param format formát řetězce hlášky
 * @param ... argumenty řetězce hlášky
 */
void print_out(const char *format, ...) {
    char buf[MAX_STR_LENGHT];
    print_datetime(buf, true);
    
    va_list vargs;
    va_start(vargs, format);
    vsnprintf(buf, sizeof(char) * MAX_STR_LENGHT, format, vargs);
    va_end(vargs);
    
    printf("%s\n", buf);
}

/**
 * Vypíše záznam o zprávě přijaté od klienta.
 * 
 * @param msg řetězec zprávy
 * @param sock socket klienta
 * @param success true, pokud přenos proběhl v pořádku, jinak false
 */
void print_recv(const char *msg, int sock, bool success) {
    char buf[MAX_STR_LENGHT];
    
    if (!success) {
        print_datetime(buf, success);
        fprintf(stderr, "Server <-- Klient %d - Chyba příjmu: \"%s\"\n",
                sock, msg == NULL ? strerror(errno) : msg);
        
        return;
    }
    
    if (msg[0] == '\0') {
        return;
    }
    
    print_datetime(buf, success);
    printf("Server <-- Klient %d: \"%s\"\n", sock, msg);
}

/**
 * Vypíše záznam o zprávě zaslané klientovi.
 * 
 * @param msg řetězec zprávy
 * @param sock socket klienta
 * @param success true, pokud přenos proběhl v pořádku, jinak false
 */
void print_send(const char *msg, int sock, bool success) {
    char buf[MAX_STR_LENGHT];
    
    if (!success) {
        print_datetime(buf, success);
        fprintf(stderr, "Server --> Klient %d - Chyba odesílání: \"%s\"\n",
                sock, msg == NULL ? strerror(errno) : msg);
        
        return;
    }
    
    if (msg[0] == '\0') {
        return;
    }
    
    print_datetime(buf, success);
    printf("Server --> Klient %d: \"%s\"\n", sock, msg);
}

/**
 * Vypíše na obrazovku zadanou chybovou hlášku spolu s popisem chyby podle čísla
 * uloženého v konstantě errno a ukončí řádek.
 * 
 * @param format formát řetězce chybové hlášky
 * @param ... argumenty řetězce chybové hlášky
 */
void print_err(const char *format, ...) {
    char buf[MAX_STR_LENGHT];
    print_datetime(buf, false);
    
    va_list vargs;
    va_start(vargs, format);
    vsnprintf(buf, sizeof(char) * MAX_STR_LENGHT, format, vargs);
    va_end(vargs);
    
    fprintf(stderr, "%s: \"%s\"\n", buf, strerror(errno));
}
