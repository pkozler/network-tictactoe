/* 
 * Author: Petr Kozler
 */

#include "config.h"
#include "tcp_server.h"
#include "cmd_arg.h"

#include <stdlib.h>

int main(int argc, char **argv) {
    args_t args = parse_args(argc, argv);
    initialize(args);
    
    return EXIT_SUCCESS;
}