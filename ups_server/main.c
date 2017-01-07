/* 
 * Modul main definuje funkci pro spuštění programu.
 * 
 * Author: Petr Kozler
 */

#include "config.h"
#include "tcp_server.h"
#include "cmd_arg.h"

#include <stdlib.h>

/**
 * Vstupní bod programu.
 * 
 * @param argc počet argumentů příkazové řádky
 * @param argv argumenty příkazové řádky
 * @return status
 */
int main(int argc, char **argv) {
    args_t args = parse_args(argc, argv);
    initialize(args);
    
    return EXIT_SUCCESS;
}