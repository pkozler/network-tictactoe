/* 
 * Author: Petr Kozler
 */

#ifndef STRING_BUILDER_H
#define STRING_BUILDER_H

#include <stdint.h>

/**
 * A mutable sequence of characters.
 *
 * @author Petr Kozler (A13B0359P)
 */

typedef struct {
    char *buffer; // string buffer
    int32_t count; // current appended character count
    int32_t capacity; // current buffer capacity
} string_builder_t;

string_builder_t *create_string_builder();
void delete_string_builder(string_builder_t *ptr);
string_builder_t *append_string(string_builder_t *ptr, char *str);
char *get_string(string_builder_t *ptr);


#endif /* STRING_BUILDER_H */

