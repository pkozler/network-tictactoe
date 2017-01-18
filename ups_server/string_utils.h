/* 
 * Hlavičkový soubor string_utils deklaruje pomocné funkce pro práce
 * s textovými řetězci.
 * 
 * Author: Petr Kozler
 */

#ifndef STRING_UTILS_H
#define STRING_UTILS_H

#include <stdbool.h>
#include <stdint.h>

char *copy_string (const char *s);
bool is_integer(const char *str);
bool starts_with(const char *str, const char *pre);
char *bool_to_string(bool i);
bool string_to_bool(char *str);
char *byte_to_string(int8_t i);
char *byte_to_string(int8_t i);
int8_t string_to_byte(char *str);
char *int_to_string(int32_t i);
int32_t string_to_int(char *str);

#endif /* STRING_UTILS_H */

