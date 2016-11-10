/* 
 * Author: Petr Kozler
 * 
 * Modul err.c definuje funkce pro výpis hlášení o událostech serveru do konzole.
 */

#include "log_printer.h"
#include "defs.h"

#include <error.h>
#include <errno.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

/**
 * Vypíše na obrazovku zadanou hlášku a ukončí řádek.
 * 
 * @param format
 * @param ...
 */
void out(const char *format, ...) {
    char buf[LOG_LINE_LENGTH];
    
    va_list vargs;
    va_start(vargs, format);
    vsnprintf(buf, sizeof(buf), format, vargs);
    va_end(vargs);
    
    printf("%s.\n", buf);
}

/**
 * Vypíše na obrazovku zadanou chybovou hlášku spolu s popisem chyby podle čísla
 * uloženého v konstantě errno a ukončí řádek.
 * 
 * @param format formát řetězce chybové hlášky
 * @param ... argumenty řetězce chybové hlášky
 */
void err(const char *format, ...) {
    char buf[LOG_LINE_LENGTH];
    
    va_list vargs;
    va_start(vargs, format);
    vsnprintf(buf, sizeof(buf), format, vargs);
    va_end(vargs);
    
    printf("%s: %s\n", buf, strerror(errno));
}