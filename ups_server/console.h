/* 
 * Author: Petr Kozler
 */

#ifndef CONSOLE_H
#define CONSOLE_H

#include <stdbool.h>

bool running;

int32_t parse_host(char *host_arg);
int32_t parse_port(char *port_arg);
char *parse_log(char *log_arg);
void start_prompt();

#endif /* CONSOLE_H */