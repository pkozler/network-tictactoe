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
} message_t;

char *byte_to_string(int8_t i);
int8_t string_to_byte(char *str);
char *int_to_string(int32_t i);
int32_t string_to_int(char *str);
message_t *create_message(char *msg_type, int32_t msg_argc);
message_t *receive_message(int sock);
bool send_message(message_t *msg, int sock);
void delete_message(message_t *msg);

#endif /* MESSAGE_H */

