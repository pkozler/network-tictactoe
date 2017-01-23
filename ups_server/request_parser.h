/* 
 * Hlavičkový soubor request_parser deklaruje funkci pro přijetí
 * a zpracování požadavku klienta.
 * 
 * Author: Petr Kozler
 */

#ifndef REQUEST_PARSER_H
#define REQUEST_PARSER_H

#include "player.h"

void parse_received_message(player_t *player);
void handle_disconnect(player_t *player);

#endif /* REQUEST_PARSER_H */
