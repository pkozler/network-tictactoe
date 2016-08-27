/* 
 * Author: Petr Kozler
 */

#ifndef CONSOLE_H
#define CONSOLE_H

#include <stdbool.h>
#include <pthread.h>

// vlákno pro načítání příkazů uživatele z konzole při probíhající komunikaci
pthread_t g_cmd_thread;

int32_t parse_host(char *host_arg);
int32_t parse_port(char *port_arg);
char *parse_log(char *log_arg);
void start_prompt();
void shutdown_prompt();

#endif /* CONSOLE_H */