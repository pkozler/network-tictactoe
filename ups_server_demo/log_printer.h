/* 
 * Author: Petr Kozler
 * 
 * Hlavičkový soubor err.h obsahuje deklarace funkcí pro výpis hlášení
 * o událostech serveru do konzole.
 */

#ifndef LOG_PRINTER_H
#define LOG_PRINTER_H

#include <stdarg.h>

void out(const char *format, ...);

void err(const char *format, ...);

#endif /* LOG_PRINTER_H */

