/* 
 * Author: Petr Kozler
 */

#ifndef LOGGER_H
#define LOGGER_H

#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>

FILE *g_log_file;

void log(const char *format, ...);

void start_logging(char *log_file_name);

#endif /* LOGGER_H */

