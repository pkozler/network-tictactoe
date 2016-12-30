/* 
 * Author: Petr Kozler
 * 
 * Modul arg_parser.c definuje funkce pro zpracování argumentů programu.
 */

#include "arg_parser.h"
#include "defs.h"

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
server_t parse_args(int argc, char** argv) {
    server_t args;
    args.host = DEFAULT_HOST;
    args.port = DEFAULT_PORT;
    
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

                if (args.port >= MAX_PORT) {
                    printf("%s není v rozsahu <%d", PORT_OPTION, MAX_PORT);
                }
            }
            else {
                printf("%s vyžduje číslo portu (<%d)", PORT_OPTION, MAX_PORT);
            }
        }
        else {
            printf("tcp_server: neplatný argument %s", arg);
        }
    }
    
    return args;
}

/**
 * Vypíše nápovědu ke spuštění programu.
 */
void usage() {
    const char *port_option = PORT_OPTION;
    const char *host_option = HOST_OPTION;
    
    printf("Použití: tcp_server [%s <číslo portu>] [%s <IP adresa pro naslouchání>]",
                port_option, host_option);
}