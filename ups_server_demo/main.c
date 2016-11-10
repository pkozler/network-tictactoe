/* 
 * Author: Petr Kozler
 * 
 * Modul main.c představuje hlavní modul programu.
 */

#include "arg_parser.h"
#include "server.h"

#include <stdio.h>
#include <stdlib.h>

/**
 * Spustí hlavní rutinu serveru.
 * 
 * @param argc počet argumentů příkazové řádky
 * @param argv argumenty příkazové řádky (IP adresa a port pro naslouchání)
 * 
 * @return návratová hodnota programu
 */
int main(int argc, char** argv) {
    server_t server = parse_args(argc, argv);
    start_server(server.host, server.port);
    
    return (EXIT_SUCCESS);
}
