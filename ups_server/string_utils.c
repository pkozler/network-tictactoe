/* 
 * Author: Petr Kozler
 */

#include "string_utils.h"
#include "config.h"
#include "protocol.h"
#include <stdlib.h>
#include <string.h>

/**
 * Určí, zda předaný řetězec představuje platnou celočíselnou hodnotu.
 * 
 * @param str řetězec
 * 
 * @return true, pokud je řetězec celé číslo, jinak false
 */
bool is_integer(const char *str) {
    int i = 0;
    
    while (str[i] != '\0') {
        if (!isdigit(str[i])) {
            return false;
        }
        
        i++;
    }
    
    return true;
}

/**
 * Určí, zda předaný řetězec začíná předaným prefixem.
 * 
 * @param str řetězec
 * @param pre prefix
 * 
 * @return true, pokud řetězec začíná prefixem, jinak false
 */
bool starts_with(const char *str, const char *pre) {
    size_t lenpre = strlen(pre);
    size_t lenstr = strlen(str);
    
    return (lenstr < lenpre) ? false : (strncmp(pre, str, lenpre) == 0);
}

char *bool_to_string(bool i) {
    char *str = (char *) malloc(sizeof(char) * (BYTE_BUF_LEN + 1));
    snprintf(str, BYTE_BUF_LEN + 1, "%d", i);
    
    return i ? strdup(MSG_TRUE) : strdup(MSG_FALSE);
}

bool string_to_bool(char *str) {
    return strncmp(MSG_TRUE, str, BOOL_STR_LEN);
}

char *byte_to_string(int8_t i) {
    char *str = (char *) malloc(sizeof(char) * (BYTE_BUF_LEN + 1));
    snprintf(str, BYTE_BUF_LEN + 1, "%d", i);
    
    return str;
}

int8_t string_to_byte(char *str) {
    return (int8_t) strtol(str, NULL, 10);
}

char *int_to_string(int32_t i) {
    char *str = (char *) malloc(sizeof(char) * (INT_BUF_LEN + 1));
    snprintf(str, INT_BUF_LEN + 1, "%d", i);
    
    return str;
}

int32_t string_to_int(char *str) {
    return (int32_t) strtol(str, NULL, 10);
}