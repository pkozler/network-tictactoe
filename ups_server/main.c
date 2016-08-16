#include "config.h"
#include "connection.h"
#include <stdlib.h>
#include <stdio.h>

#define MAX_ARGS 4

void print_help_and_exit() {
    printf("TCP server hry \"Piškvorky pro více hráčů\" (Verze 2.0)\n");
    printf("Seminární práce z předmětu \"Úvod do počítačových sítí\" (KIV/UPS)\n");
    printf("Autor: Petr Kozler (A13B0359P), 2017\n\n");
    printf("Použití:   server [<host> [<port> [<log>]]]\n");
    printf("Popis parametrů:\n");
    printf("   <host> ... IP adresa pro naslouchání (\"%s\"  pro jakoukoliv adresu)\n", ANY_IP_STR);
    printf("   <port> ... celé číslo v rozsahu %d - %d\n", MIN_PORT, MAX_PORT);
    printf("   <log> ... název souboru pro logování včetně cesty\n");
    printf("Příklad:   server %s %d\n", ANY_IP_STR, DEFAULT_PORT);

    exit(EXIT_SUCCESS);
}

int main(int argc, char **argv)
{
    if (argc > MAX_ARGS) {
        print_help_and_exit();
    }
    
    argv = realloc(argv, MAX_ARGS * sizeof(char *));
    
    int i;
    for (i = argc; i < MAX_ARGS; i++) {
        argv[i] = NULL;
    }
    
    start_server(argv[0], argv[1], argv[2]);
    
    return EXIT_SUCCESS;
}