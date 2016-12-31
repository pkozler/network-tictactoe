/* 
 * Author: Petr Kozler
 */

#ifndef TCP_SERVER_CONTROL_H
#define TCP_SERVER_CONTROL_H

#include <stdbool.h>

bool is_server_running();
void start_server();
void stop_server();
void print_args();
void print_stats();

#endif /* TCP_SERVER_CONTROL_H */

