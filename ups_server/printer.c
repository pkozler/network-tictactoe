/* 
 * Author: Petr Kozler
 */

#include "printer.h"
#include "config.h"
#include <error.h>
#include <errno.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

#define LOG_LINE_LENGTH 65535 // velikost bufferu pro výpis hlášení

/**
 * Vypíše na obrazovku zadanou hlášku a ukončí řádek.
 * 
 * @param format formát řetězce hlášky
 * @param ... argumenty řetězce hlášky
 */
void print_out(const char *format, ...) {
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
void print_err(const char *format, ...) {
    char buf[LOG_LINE_LENGTH];
    
    va_list vargs;
    va_start(vargs, format);
    vsnprintf(buf, sizeof(buf), format, vargs);
    va_end(vargs);
    
    printf("%s: %s\n", buf, strerror(errno));
}
