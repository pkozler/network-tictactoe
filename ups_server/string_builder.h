/*
 * Author: Petr Kozler
 */

#ifndef STRING_BUILDER_H
#define STRING_BUILDER_H

#include <stdarg.h>
#include <stdint.h>

char *create_string(int32_t max_str_len, char *buf, const char *format, ...);

#endif /* STRING_BUILDER_H */
