/* 
 * Modul printer definuje funkce pro výpis na konzoli.
 * 
 * Author: Petr Kozler
 */

#include "printer.h"
#include "config.h"
#include "string_builder.h"
#include <error.h>
#include <errno.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

/**
 * Vypíše na obrazovku zadanou hlášku a ukončí řádek.
 * 
 * @param format formát řetězce hlášky
 * @param ... argumenty řetězce hlášky
 */
void print_out(const char *format, ...) {
    char buf[MAX_STR_LENGHT];
    
    va_list vargs;
    va_start(vargs, format);
    create_string(MAX_STR_LENGHT, buf, format, vargs);
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
    char buf[MAX_STR_LENGHT];
    
    va_list vargs;
    va_start(vargs, format);
    create_string(0, buf, format, vargs);
    va_end(vargs);
    
    printf("%s: %s\n", buf, strerror(errno));
}
