/* 
 * Hlavičkový soubor server_control deklaruje funkce pro ovládání aktuální
 * instance serveru a výpis informací.
 * 
 * Author: Petr Kozler
 */

#ifndef SERVER_CONTROL_H
#define SERVER_CONTROL_H

#include <stdbool.h>

// ukazatel na funkci pro výpis výsledků zpracování příkazu uživatele v konzoli
typedef void (*print_func_t)();

void print_stats();
bool is_server_running();
void start_server();
void stop_server();
void print_args();
void print_stats();
void print_commands();
void print_exit_question();
void print_unknown_cmd();
void print_cmd_result(print_func_t print_func);

#endif /* SERVER_CONTROL_H */

