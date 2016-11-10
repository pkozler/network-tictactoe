/* 
 * Author: Petr Kozler
 * 
 * Hlavičkový soubor defs.h definuje základní konfigurační konstanty
 * používané v různých částech programu.
 */

#ifndef DEFS_H
#define DEFS_H

#include "linked_list.h"

#define PORT_OPTION "-port" // klíč parametru čísla portu
#define HOST_OPTION "-host" // klíč parametru IP adresy
#define DEFAULT_PORT 7654 // výchozí číslo portu
#define MAX_PORT 65535 // maximální povolené číslo portu
#define DEFAULT_HOST "0.0.0.0" // výchozí IP pro naslouchání (INADDR_ANY)
#define QUEUE_LENGTH 8 // délka fronty pro čekání na spojení
#define LOGIN_ACK_STR "OK" // řetězec potvrzení loginu
#define LOGIN_ACK_LENGTH 2 // délka řetězce potvrzení loginu
#define CLOSE_MSG_STR "close()" // řetězec odhlášení klienta
#define CLOSE_MSG_LENGTH 7 // délka řetězce odhlášení klienta
#define LOG_LINE_LENGTH 1024 // velikost bufferu pro výpis hlášení
#define CLIENT_STR_LENGTH 256 // velikost bufferu pro sestavení podpisu klienta
#define MSG_CNT 10 // limit počtu zpráv klienta na jedno přihlášení
#define SOCKET_TIMEOUT_SEC 3 // timeout socketu

linked_list_t *client_list;

#endif /* DEFS_H */

