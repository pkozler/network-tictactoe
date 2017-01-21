/* 
 * Hlavičkový soubor printer deklaruje funkce pro výpis logů do konzole.
 * 
 * Author: Petr Kozler
 */

#ifndef LOGGER_H
#define LOGGER_H

#include <stdarg.h>
#include <stdbool.h>

void print_out(const char *format, ...);
void print_recv(const char *msg, int sock, bool success);
void print_send(const char *msg, int sock, bool success);
void print_err(const char *format, ...);

#endif /* LOGGER_H */

