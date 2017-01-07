/* 
 * Hlavičkový soubor player_list_sender deklaruje funkce pro odesílání
 * seznamu hráčů.
 * 
 * Author: Petr Kozler
 */

#ifndef PLAYER_LIST_SENDER_H
#define PLAYER_LIST_SENDER_H

#include "message_list.h"

message_list_t *player_list_to_message_list();
void broadcast_player_list();

#endif /* PLAYER_LIST_SENDER_H */
