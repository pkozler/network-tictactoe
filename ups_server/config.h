/* 
 * Author: Petr Kozler
 */

#ifndef CONFIG_H
#define CONFIG_H

/*
 * Základní konfigurační konstanty serveru:
 */

#define ANY_IP_STR "*" // řetězec označující libovolnou IP adresu pro naslouchání
#define MIN_PORT 1 // nejnižší povolené číslo portu
#define MAX_PORT 65535 // nejvyšší povolené číslo portu
#define DEFAULT_PORT 10001 // výchozí port pro naslouchání
#define QUEUE_LEN 8 // délka fronty pro příchozí spojení
#define SOCKET_TIMEOUT_SEC 5 // timeout připojení klienta v sekundách
#define PING_PERIOD_MICROS 1000000 // perioda testování odezvy klienta v mikrosekundách

/*
 * Dostupné požadavky klienta a počty argumentů:
 */



/*
 * Ošetřované chyby požadavků klienta:
 */



/*
 * Dostupné odpovědi serveru a počty argumentů:
 */



/*
 * Ošetřované chyby odpovědí serveru:
 */



/*
 * Ostatní konstanty podporované aplikačním protokolem:
 */

#define MSG_OK "ok" // pozitivní přijetí zprávy (požadavek klienta je v pořádku)
#define MSG_FAIL "fail" // negativní přijetí zprávy (v požadavku klienta je chyba)
#define SEPARATOR ";" // oddělovač tokenů (označení typu a argumentů) řetězce zprávy
 
#endif /* CONFIG_H */