/* 
 * Hlavičkový soubor broadcaster obsahuje deklarace funkcí pro rozeslání
 * zpráv většímu počtu klientů najednou.
 * 
 * Author: Petr Kozler
 */

#ifndef BROADCASTER_H
#define BROADCASTER_H

#include "message.h"
#include "message_list.h"
#include "player.h"
#include <stdint.h>

void send_message_to_all(message_t *message);
void send_message_to_selected(message_t *message,
        player_t **clients, int32_t client_count);
void send_message_list_to_all(message_list_t *messages);
void send_message_list_to_selected(message_list_t *messages,
        player_t **clients, int32_t client_count);

#endif /* BROADCASTER_H */
