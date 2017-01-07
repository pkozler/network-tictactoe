/* 
 * Hlavičkový soubor cmd_arg obsahuje deklaraci funkce pro zpracování
 * parametrů programu a definuje strukturu pro jejich uchování.
 * 
 * Author: Petr Kozler
 */

#ifndef CMD_ARG_H
#define CMD_ARG_H

#include <stdint.h>

/**
 * Struktura s parametry serveru.
 */
typedef struct {
    char *host; // IP adresa pro naslouchání
    int32_t port; // port pro naslouchání
    char *log_file; // název logovacího souboru
    int32_t queue_length; // délka fronty pro příchozí spojení
} args_t;

args_t parse_args(int argc, char** argv);

void usage();

#endif /* CMD_ARG_H */

