/* 
 * Author: Petr Kozler
 */

#ifndef LOGGER_H
#define LOGGER_H

#include <stdarg.h>

void print_log(const char *format, ...);
void start_logging(char *log_file_name);
void shutdown_logging();

#endif /* LOGGER_H */

