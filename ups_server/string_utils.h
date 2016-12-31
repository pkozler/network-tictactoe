/* 
 * Author: Petr Kozler
 */

#ifndef STRING_UTILS_H
#define STRING_UTILS_H

#include <stdbool.h>

bool is_integer(const char *str);
bool starts_with(const char *str, const char *pre);
char *byte_to_string(int8_t i);
int8_t string_to_byte(char *str);
char *int_to_string(int32_t i);
int32_t string_to_int(char *str);

#endif /* STRING_UTILS_H */

