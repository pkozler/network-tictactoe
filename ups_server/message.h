/* 
 * Author: Petr Kozler
 */

#ifndef MESSAGE_H
#define MESSAGE_H

#include <stdint.h>

/**
 * Definice struktury zpráv vyměňovaných mezi klienty a serverem v rámci
 * textového aplikačního protokolu postaveného nad transportním protokolem TCP
 * pro usnadnění sestavování odesílaných a zpracovávání přijatých zpráv.
 */
typedef struct message_t {
    char *msg_type; // typ zprávy
    int32_t msg_argc; // počet argumentů
    char **msg_argv; // argumenty zprávy
};

message_t *create_message(char *msg_type, int32_t msg_argc);
message_t *receive_message(int sock);
void send_message(message_t *msg, int sock);

#endif /* MESSAGE_H */

