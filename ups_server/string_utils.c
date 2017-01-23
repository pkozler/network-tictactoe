/* 
 * Modul string_utils definuje pomocné funkce pro manipulaci s řetězci.
 * 
 * Author: Petr Kozler
 */

#include "string_utils.h"
#include "config.h"
#include "protocol.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

/**
 * Zkopíruje zadaný řetězec.
 * 
 * @param s řetězec
 * @return kopie řetězce
 */
char *copy_string(const char *s) {
    if (s == NULL) {
        return NULL;
    }
    
    int len = strlen(s) + 1;
    char *d = (char *) malloc(sizeof(char) * len);
    
    if (d == NULL) {
        return NULL;
    }
    
    strncpy(d, s, len);
    
    return d;
}

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

/**
 * Převede logickou hodnotu na řetězec.
 * 
 * @param i logická hodnota
 * @return řetězec
 */
char *bool_to_string(bool i) {
    return copy_string(i ? MSG_TRUE : MSG_FALSE);
}

/**
 * Převede řetězec na logickou hodnotu.
 * 
 * @param str řetězec
 * @return logická hodnota
 */
bool string_to_bool(char *str) {
    return !strncmp(MSG_TRUE, str, BOOL_STR_LEN);
}

/**
 * Převede 8-bitové celé číslo hodnotu na řetězec.
 * 
 * @param i 8-bitové celé číslo
 * @return řetězec
 */
char *byte_to_string(int8_t i) {
    char *str = (char *) calloc(BYTE_BUF_LEN + 1, sizeof(char));
    snprintf(str, BYTE_BUF_LEN + 1, "%d", i);
    
    return str;
}

/**
 * Převede řetězec na 8-bitové celé číslo.
 * 
 * @param str řetězec
 * @return 8-bitové celé číslo
 */
int8_t string_to_byte(char *str) {
    return (int8_t) strtol(str, NULL, 10);
}

/**
 * Převede 32-bitové celé číslo hodnotu na řetězec.
 * 
 * @param i 32-bitové celé číslo
 * @return řetězec
 */
char *int_to_string(int32_t i) {
    char *str = (char *) calloc(INT_BUF_LEN + 1, sizeof(char));
    snprintf(str, INT_BUF_LEN + 1, "%d", i);
    
    return str;
}

/**
 * Převede řetězec na 32-bitové celé číslo.
 * 
 * @param str řetězec
 * @return 32-bitové celé číslo
 */
int32_t string_to_int(char *str) {
    return (int32_t) strtol(str, NULL, 10);
}