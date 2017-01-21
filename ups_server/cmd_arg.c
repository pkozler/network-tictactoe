/* 
 * Modul cmd_arg definuje funkce pro zpracování argumentů programu.
 * 
 * Author: Petr Kozler
 */

#include "cmd_arg.h"
#include "config.h"
#include "string_utils.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <stdbool.h>
#include <netinet/in.h>

/**
 * Zpracuje předané argumenty příkazové řádky.
 * 
 * @param argc počet argumentů příkazové řádky
 * @param argv argumenty příkazové řádky (IP adresa a port pro naslouchání)
 * @return návratová hodnota programu
 */
args_t parse_args(int argc, char** argv) {
    if (argc <= 1) {
        usage();
    }
    
    args_t args;
    args.host = DEFAULT_HOST;
    bool has_port = false;
    
    int i = 1;
    char *arg;

    while (i < argc && starts_with(argv[i], "-")) {
        arg = argv[i++];
        
        if (!strcmp(HOST_OPTION, arg)) {
            if (i < argc) {
                args.host = argv[i++];
            }
            else {
                printf("%s vyžaduje IP adresu pro naslouchání, použita výchozí hodnota\n", HOST_OPTION);
            }
        }
        else if (!strcmp(PORT_OPTION, arg)) {
            int port = 0;
            
            if (i < argc) {
                if (is_integer(argv[i])) {
                    port = strtol(argv[i], NULL, 10);
                    
                    if (port < MIN_PORT || port > MAX_PORT) {
                        printf("%s není v rozsahu %d - %d\n",
                                PORT_OPTION, MIN_PORT, MAX_PORT);
                    }
                    else {
                        has_port = true;
                    }
                }
                else {
                    printf("%s není číslo\n", PORT_OPTION);
                }
                
                i++;
            }
            else {
                printf("%s vyžaduje číslo portu v rozsahu %d - %d\n",
                        PORT_OPTION, MIN_PORT, MAX_PORT);
            }

            if (has_port) {
                args.port = port;
            }
            else {
                args.port = DEFAULT_PORT;
                printf("Použita výchozí hodnota %d\n", DEFAULT_PORT);
                has_port = true;
            }
        }
        else {
            printf("Neplatný parametr %s\n", arg);
        }
    }
    
    if (!has_port) {
        printf("Nebyl zadán parametr %s\n", PORT_OPTION);
        usage();
    }
    
    return args;
}

/**
 * Vypíše nápovědu ke spuštění programu.
 */
void usage() {
    const char *name = "server";
    
    printf("\n");
    printf("TCP server hry \"Piškvorky pro více hráčů\" (Verze 2.0)\n");
    printf("Seminární práce z předmětu \"Úvod do počítačových sítí\" (KIV/UPS)\n");
    printf("Autor: Petr Kozler (A13B0359P), 2017\n");
    printf("\n");
    printf("Použití:    %s [%s <host>] %s <port>\n",
            name, HOST_OPTION, PORT_OPTION);
    printf("Popis parametrů:\n");
    printf("    <host> ... IP adresa pro naslouchání\n");
    printf("        (výchozí hodnota: \"%s\" - naslouchání na všech IP)\n",
            DEFAULT_HOST);
    printf("    <port> ... celé číslo v rozsahu %d - %d\n",
            MIN_PORT, MAX_PORT);
    printf("Příklad:    %s %s %d\n",
            name, PORT_OPTION, DEFAULT_PORT);
    printf("\n");
    
    exit(EXIT_SUCCESS);
}
