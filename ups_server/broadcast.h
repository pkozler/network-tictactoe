/* 
 * Author: Petr Kozler
 */

#ifndef BROADCAST_H
#define BROADCAST_H

#include "message.h"
#include <stdint.h>

void send_to_all_clients(int32_t msgc, message_t **msgv);

#endif /* BROADCAST_H */

