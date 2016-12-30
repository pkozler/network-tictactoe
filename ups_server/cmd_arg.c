/* 
 * Author: Petr Kozler
 * 
 * Modul arg_parser.c definuje funkce pro zpracování argumentů programu.
 */

#include "cmd_arg.h"
#include "config.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <stdbool.h>
#include <netinet/in.h>

/**
 * Určí, zda předaný řetězec představuje platnou celočíselnou hodnotu.
 * 
 * @param str řetězec
 * 
 * @return true, pokud je řetězec celé číslo, jinak false
 */
bool is_integer(const char *str) {
    int i = 0;
    
    while (str[i] != '\0') {
        if (!isdigit(str[i])) {
            return false;
        }
        
        i++;
    }
    
    return true;
}

/**
 * Určí, zda předaný řetězec začíná předaným prefixem.
 * 
 * @param str řetězec
 * @param pre prefix
 * 
 * @return true, pokud řetězec začíná prefixem, jinak false
 */
bool starts_with(const char *str, const char *pre) {
    size_t lenpre = strlen(pre);
    size_t lenstr = strlen(str);
    
    return (lenstr < lenpre) ? false : (strncmp(pre, str, lenpre) == 0);
}

/**
 * Zpracuje předané argumenty příkazové řádky.
 * 
 * @param argc počet argumentů příkazové řádky
 * @param argv argumenty příkazové řádky (IP adresa a port pro naslouchání)
 * 
 * @return návratová hodnota programu
 */
args_t parse_args(int argc, char** argv) {
    args_t args;
    args.host = DEFAULT_HOST;
    args.port = DEFAULT_PORT;
    args.log_file = DEFAULT_LOG_FILE;
    args.queue_length = DEFAULT_QUEUE_LENGTH;
    
    int i = 0;
    char *arg;
    char *tmp;

    while (i < argc && starts_with(argv[i], "-")) {
        arg = argv[i++];

        if (!strcmp(HOST_OPTION, arg)) {
            if (i < argc) {
                args.host = argv[i++];
            } else {
                printf("%s vyžaduje hostname nebo IP adresu serveru",
                        HOST_OPTION);
            }
        }
        else if (!strcmp(PORT_OPTION, arg)) {
            if (i < argc) {
                tmp = argv[i++];

                if (is_integer(tmp)) {
                    args.port = strtol(tmp, NULL, 10);
                }
                else {
                    printf("%s není číslo, nastavena výchozí hodnota %d",
                            PORT_OPTION, DEFAULT_PORT);
                    args.port = DEFAULT_PORT;
                }

                if (args.port > MAX_PORT || args.port < MIN_PORT) {
                    printf("%s není v rozsahu %d - %d", PORT_OPTION, MIN_PORT, MAX_PORT);
                }
            }
            else {
                printf("%s vyžaduje číslo portu (%d - %d)", PORT_OPTION, MIN_PORT, MAX_PORT);
            }
        }
        else if (!strcmp(LOG_OPTION, arg)) {
            if (i < argc) {
                args.log_file = argv[i++];
            }
            else {
                printf("%s vyžaduje název souboru pro logování včetně cesty",
                        LOG_OPTION);
            }
        }
        else if (!strcmp(QUEUE_OPTION, arg)) {
            if (i < argc) {
                tmp = argv[i++];

                if (is_integer(tmp)) {
                    args.queue_length = strtol(tmp, NULL, 10);
                }
                else {
                    printf("%s není číslo, nastavena výchozí hodnota %d",
                            QUEUE_OPTION, DEFAULT_QUEUE_LENGTH);
                    args.queue_length = DEFAULT_QUEUE_LENGTH;
                }

                if (args.queue_length > MAX_QUEUE_LENGTH || args.queue_length < MIN_QUEUE_LENGTH) {
                    printf("%s není v rozsahu %d - %d", QUEUE_OPTION, MIN_QUEUE_LENGTH, MAX_QUEUE_LENGTH);
                }
            }
            else {
                printf("%s vyžaduje délku fronty (%d - %d)", QUEUE_OPTION, MIN_QUEUE_LENGTH, MAX_QUEUE_LENGTH);
            }
        }
        else {
            printf("server: neplatný argument %s", arg);
        }
    }
    
    return args;
}

/**
 * Vypíše nápovědu ke spuštění programu.
 */
void usage() {
    printf("TCP server hry \"Piškvorky pro více hráčů\" (Verze 2.0)\n");
    printf("Seminární práce z předmětu \"Úvod do počítačových sítí\" (KIV/UPS)\n");
    printf("Autor: Petr Kozler (A13B0359P), 2017\n\n");
    printf("Použití:   server [%s <host>] [%s <port>] [%s <logovací soubor>] [%s <délka fronty>]\n",
            HOST_OPTION, PORT_OPTION, LOG_OPTION, QUEUE_OPTION);
    printf("Popis parametrů:\n");
    printf("   <host> ... IP adresa pro naslouchání (\"%s\"  pro naslouchání na všech adresách)\n",
            DEFAULT_HOST);
    printf("   <port> ... celé číslo v rozsahu %d - %d\n",
            MIN_PORT, MAX_PORT);
    printf("   <logovací soubor> ... název souboru pro logování včetně cesty\n");
    printf("   <délka fronty> ... délka fronty pro příchozí spojení v rozsahu %d - %d\n",
            MIN_QUEUE_LENGTH, MAX_QUEUE_LENGTH);
    printf("Příklad:   server %s %s %s %d\n",
            HOST_OPTION, DEFAULT_HOST, PORT_OPTION, DEFAULT_PORT);
}
