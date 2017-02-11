/* 
 * Modul message_list definuje funkce pro vytvoření a odstranění seznamu zpráv.
 * 
 * Author: Petr Kozler
 */

#include "message_list.h"
#include "communicator.h"
#include <stdlib.h>

/**
 * Vytvoří seznam zpráv.
 * 
 * @param head hlavička seznamu
 * @param msgc počet položek
 * @return seznam zpráv
 */
message_list_t *create_message_list(char *head, int32_t msgc) {
    message_list_t *msg_list = (message_list_t *) malloc(sizeof(message_list_t));
    msg_list->head = head;
    msg_list->msgc = msgc;
    msg_list->msgv = (char **) malloc(sizeof(char *) * msgc);
    
    return msg_list;
}

/**
 * Odstraní seznam zpráv.
 * 
 * @param msg_list seznam zpráv
 */
void delete_message_list(message_list_t *msg_list) {
    free(msg_list->head);
    
    int32_t i;
    for (i = 0; i < msg_list->msgc; i++) {
        free(msg_list->msgv[i]);
    }
    
    if (msg_list->msgc > 0) {
        free(msg_list->msgv);
    }
    
    free(msg_list);
}

/**
 * Odešle seznam zpráv klientovi.
 * 
 * @param messages seznam zpráv
 * @param socket socket klienta
 */
void send_message_list(message_list_t *messages, client_socket_t *socket) {
    if (!(socket->connected)) {
        return;
    }
    
    send_message_string(messages->head, socket);
    
    int32_t i;
    for (i = 0; i < messages->msgc; i++) {
        send_message_string(messages->msgv[i], socket);
    }
}
