/* 
 * Hlavičkový soubor game_list_sender deklaruje funkce pro odesílání
 * seznamu her.
 * 
 * Author: Petr Kozler
 */

#ifndef GAME_LIST_SENDER_H
#define GAME_LIST_SENDER_H

#include "message_list.h"

message_list_t *game_list_to_message_list();
void broadcast_game_list();

#endif /* GAME_LIST_SENDER_H */
