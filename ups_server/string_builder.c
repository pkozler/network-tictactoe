/*
 * Author: Petr Kozler
 */

#include "string_builder.h"
#include <stdlib.h>

char *create_string(int32_t max_str_len, char *buf, const char *format, ...) {
    va_list vargs;
    
    va_start(vargs, format);
    
    char *str = NULL;
    
    if (max_str_len > 0) {
        // vytvoření a zformátování řetězce
        str = (char *) malloc(sizeof(char) * max_str_len);
        vsnprintf(str, sizeof(char) * max_str_len, format, vargs);
    }
    
    if (buf != NULL) {
        vsnprintf(buf, sizeof(buf), format, vargs);
    }
    
    va_end(vargs);
    
    return str;
}