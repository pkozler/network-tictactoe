/* 
 * Author: Petr Kozler
 */

#ifndef MESSAGE_H
#define MESSAGE_H

#include <stdint.h>
#include <stdbool.h>

/**
 * Definice struktury zpráv vyměňovaných mezi klienty a serverem v rámci
 * textového aplikačního protokolu postaveného nad transportním protokolem TCP
 * pro usnadnění sestavování odesílaných a zpracovávání přijatých zpráv.
 */
typedef struct {
    char *type; // typ zprávy
    int32_t argc; // počet argumentů
    char **argv; // argumenty zprávy
    int32_t counter; // čítač argumentů
} message_t;

bool put_string_arg(message_t *msg, char *arg);
char *get_string_arg(message_t *msg);
bool put_byte_arg(message_t *msg, int8_t arg);
int8_t get_byte_arg(message_t *msg);
bool put_int_arg(message_t *msg, int32_t arg);
int32_t get_int_arg(message_t *msg);
message_t *create_message(char *msg_type, int32_t msg_argc);
void delete_message(message_t *msg);

#endif /* MESSAGE_H */
