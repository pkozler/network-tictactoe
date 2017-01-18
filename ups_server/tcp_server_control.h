/* 
 * Hlavičkový soubor tcp_server_control deklaruje funkce pro ovládání aktuální
 * instance serveru a výpis informací.
 * 
 * Author: Petr Kozler
 */

#ifndef TCP_SERVER_CONTROL_H
#define TCP_SERVER_CONTROL_H

#include <stdbool.h>

void print_stats();
bool is_server_running();
void start_server();
void stop_server();
void print_args();
void print_stats();

#endif /* TCP_SERVER_CONTROL_H */

