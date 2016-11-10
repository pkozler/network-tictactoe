/* 
 * Author: Petr Kozler
 * 
 * Hlavičkový soubor message.h definuje strukturu zprávy a obsahuje deklarace
 * funkcí pro práci se zprávami.
 */

#ifndef MESSAGE_H
#define MESSAGE_H

#include <stdint.h>
#include <stdbool.h>

/*
 * Struktura zprávy.
 */
typedef struct {
    char *bytes; // obsah zprávy
    int32_t length; // délka obsahu
} message_t;

message_t *create_message(int32_t length);

int send_message(message_t *message, int sock);

int receive_message(message_t **message, int sock);

bool delete_message(message_t *message);

#endif /* MESSAGE_H */

