#include "logging.h"

#include <string.h>
#include <errno.h>

/*
    modul poskytující funkce pro výpis událostí serveru
    a jejich současné logování do souboru
*/

/**
    Vypíše zprávu na obrazovku a do souboru.
*/
void print(const char *format, ...) {
    va_list vargs;
    va_start(vargs, format);
    vprintf(format, vargs);
    va_end(vargs);

    va_start(vargs, format);
    vfprintf(log_file, format, vargs);
    va_end(vargs);

    printf("\n");
    fprintf(log_file, "\n");
    fflush(log_file);
}

/**
    Vypíše chybovou zprávu na obrazovku a do souboru
    a ukončí program.
*/
void err(const char *msg) {
    print("Chyba (%i, %s): %s", errno, strerror(errno), msg);
    exit(EXIT_FAILURE);
}
