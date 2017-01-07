/* 
 * Hlavičkový soubor tcp_server deklaruje funkci pro inicializaci datových struktur
 * serveru a spuštění navazování spojení s klienty.
 * 
 * Author: Petr Kozler
 */

#ifndef TCP_SERVER_H
#define TCP_SERVER_H

#include "cmd_arg.h"

void initialize(args_t args);

#endif /* TCP_SERVER_H */