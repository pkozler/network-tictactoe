/* 
 * Author: Petr Kozler
 */

#include "message.h"
#include "string_utils.h"
#include <stdlib.h>

bool put_string_arg(message_t *msg, char *arg) {
    if (msg->counter == msg->argc) {
        return false;
    }
    
    msg->argv[msg->counter++] = arg;
    
    return true;
}

char *get_string_arg(message_t *msg) {
    char *arg = msg->argv[msg->argc - msg->counter];
    msg->counter--;
    
    return arg;
}

bool put_byte_arg(message_t *msg, int8_t arg) {
    if (msg->counter == msg->argc) {
        return false;
    }
    
    msg->argv[msg->counter++] = byte_to_string(arg);
    
    return true;
}

int8_t get_byte_arg(message_t *msg) {
    int8_t arg = string_to_byte(msg->argv[msg->argc - msg->counter]);
    msg->counter--;
    
    return arg;
}

bool put_int_arg(message_t *msg, int32_t arg) {
    if (msg->counter == msg->argc) {
        return false;
    }
    
    msg->argv[msg->counter++] = int_to_string(arg);
    
    return true;
}

int32_t get_int_arg(message_t *msg) {
    int32_t arg = string_to_int(msg->argv[msg->argc - msg->counter]);
    msg->counter--;
    
    return arg;
}

/**
 * Vytvoří novou strukturu zprávy zadaného typu se zadaným počtem parametrů
 * určenou k odeslání klientovi.
 * 
 * @param msg_type typ zprávy
 * @param msg_argc počet parametrů
 * @return alokovaná struktura zprávy
 */
message_t *create_message(char *msg_type, int32_t msg_argc) {
    message_t *message = (message_t *) malloc(sizeof(message_t));
    
    message->type = msg_type;
    message->argc = msg_argc;
    message->counter = 0;
    
    if (message->argc > 0) {
        message->argv = (char **) malloc(sizeof(char *) * msg_argc);
    }
    
    return message;
}

/**
 * Odstraní vytvořenou strukturu zprávy.
 * 
 * @param msg alokovaná struktura zprávy
 */
void delete_message(message_t *msg) {
    int32_t i;
    
    for (i = 0; i < msg->argc; i++) {
        free(msg->argv[i]);
    }
    
    if (msg->argc > 0) {
        free(msg->argv);
    }
    
    free(msg);
}
