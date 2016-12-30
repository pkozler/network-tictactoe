/* 
 * Author: Petr Kozler
 */

#include "string_builder.h"
#include <stdlib.h>
#include <string.h>

#define STR_DEFAULT_CAPACITY 16
#define STR_RESIZE_COEF 2

/**
 * Constructs a string builder with no characters in it and an initial
 * capacity of 16 characters.
 */
string_builder_t *create_string_builder() {
    string_builder_t *sb = (string_builder_t *) malloc(sizeof (string_builder_t));
    sb->count = 1;
    sb->capacity = STR_DEFAULT_CAPACITY;
    // allocating the inner buffer to the default capacity
    sb->buffer = (char *) malloc(sb->capacity * sizeof (char));
    sb->buffer[0] = '\0';

    return sb;
}

/*
    Destructs the StringBuilder.
 */
void delete_string_builder(string_builder_t *ptr) {
    free(ptr->buffer);
    free(ptr);
}

/**
 * Appends the specified string to the specified character sequence.
 *
 * @param ptr The allocated structure
 * @param str a string.
 * @return a pointer to the specified structure.
 */
string_builder_t *append_string(string_builder_t *ptr, char *str) {
    int32_t str_len = strlen(str);
    
    // increasing the buffer capacity if the new string length reaches the current capacity
    if (ptr->count + str_len >= ptr->capacity) {
        ptr->capacity += str_len * STR_RESIZE_COEF;
        // reallocating the memory (expanding the allocated memory by the resize coefficient)
        ptr->buffer = realloc(ptr->buffer, ptr->capacity * sizeof (char));
    }

    // concatenating the new string
    strcat(ptr->buffer, str);
    ptr->count += str_len;

    return ptr;
}

/**
 * Returns a string representing the data in the specified sequence.
 *
 * @param ptr The allocated structure
 * @return a string representation of the specified sequence of characters.
 */
char *get_string(string_builder_t *ptr) {
    return ptr->buffer;
}