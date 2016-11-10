/* 
 * Author: Petr Kozler
 * 
 * Hlavičkový soubor arg_parser.h obsahuje deklaraci funkce pro zpracování
 * parametrů programu a definuje strukturu pro jejich uchování.
 */

#ifndef ARG_PARSER_H
#define ARG_PARSER_H

#include <stdint.h>

/*
 * Struktura s parametry serveru.
 */
typedef struct {
    char *host; // IP adresa pro naslouchání
    int32_t port; // port pro naslouchání
} server_t;

server_t parse_args(int argc, char** argv);

void usage();

#endif /* ARG_PARSER_H */

