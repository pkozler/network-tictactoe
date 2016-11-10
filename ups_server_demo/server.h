/* 
 * Author: Petr Kozler
 * 
 * Hlavičkový soubor server.h obsahuje deklaraci funkce pro spuštění rutiny
 * pro navazování spojení.
 */

#ifndef SERVER_H
#define SERVER_H

#include <stdint.h>

void start_server(char *host, int32_t port);

#endif /* SERVER_H */

