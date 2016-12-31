/* 
 * Author: Petr Kozler
 */

#ifndef TCP_COMMUNICATOR_H
#define TCP_COMMUNICATOR_H

#include "message.h"
#include <stdbool.h>

message_t *receive_message(int sock);
bool send_message(message_t *msg, int sock);

#endif /* TCP_COMMUNICATOR_H */

