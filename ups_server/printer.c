/* 
 * Author: Petr Kozler
 */

#include <err.h>
#include <error.h>
#include <errno.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

/**
 * Vypíše na obrazovku zadanou chybovou hlášku spolu s popisem chyby podle čísla
 * uloženého v konstantě errno a poté ukončí program.
 * Funkce je určena pro případy výskytu závažné chyby, pro kterou server
 * nemůže nadále pokračovat v korektní činnosti.
 * 
 * @param format formát řetězce chybové hlášky
 * @param ... argumenty řetězce chybové hlášky
 */
void die(const char *format, ...) {
    va_list vargs;
    
    va_start(vargs, format);
    printf("%s: %s\n", vargs, strerror(errno));
    va_end(vargs);
    
    exit(errno);
}
