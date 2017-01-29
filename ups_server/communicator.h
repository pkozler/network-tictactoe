/* 
 * Hlavičkový soubor communicator deklaruje funkce pro výměnu zpráv
 * mezi serverem a klienty.
 * 
 * Author: Petr Kozler
 */

#ifndef COMMUNICATOR_H
#define COMMUNICATOR_H

#include "message.h"
#include "client_socket.h"
#include <stdbool.h>

message_t *receive_message(client_socket_t *socket);
char *build_message_string(message_t *msg);
bool send_message_string(char *msg_str, client_socket_t *socket);
bool send_message(message_t *msg, client_socket_t *socket);

#endif /* COMMUNICATOR_H */
