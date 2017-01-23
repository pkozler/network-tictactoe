/* 
 * Hlavičkový soubor config obsahuje definice konfiguračních konstant serveru.
 * 
 * Author: Petr Kozler
 */

#ifndef CONFIG_H
#define CONFIG_H

/*
 * Základní konfigurační konstanty serveru:
 */

#define HELP_OPTION "-?" // argument příkazové řádky značící výpis nápovědy
#define HOST_OPTION "-h" // argument příkazové řádky značící volbu IP adresy
#define PORT_OPTION "-p" // argument příkazové řádky značící volbu čísla portu
#define DEFAULT_HOST "0.0.0.0" // výchozí IP adresa pro naslouchání (INADDR_ANY)
#define MIN_PORT 0 // nejnižší povolené číslo portu
#define MAX_PORT 65535 // nejvyšší povolené číslo portu
#define DEFAULT_PORT 10000 // výchozí port pro naslouchání
#define QUEUE_LENGTH 5 // výchozí délka fronty pro příchozí spojení
#define SOCKET_TIMEOUT_SEC 5 // timeout příjmu/odeslání zprávy klienta v sekundách
#define MAX_MESSAGE_LENGTH 65535 // maximální povolená délka zprávy
#define MAX_NAME_LENGTH 16 // maximální povolená délka jména hráče nebo názvu hry
#define MIN_BOARD_SIZE 3 // minimální povolený rozměr hracího pole
#define MAX_BOARD_SIZE 16 // maximální povolený rozměr hracího pole
#define MIN_PLAYERS_SIZE 2 // minimální povolený počet hráčů ve hře
#define MAX_PLAYERS_SIZE 4 // maximální povolený počet hráčů ve hře
#define MIN_CELL_COUNT 3 // minimální povolený počet políček potřebných k obsazení
#define MAX_CELL_COUNT 8 // maximální povolený počet políček potřebných k obsazení
#define BOOL_STR_LEN 5 // maximální délka řetězce představujícího logickou hodnotu
#define BYTE_BUF_LEN 4 // délka bufferu pro převod 8-bitového čísla na řetězec
#define INT_BUF_LEN 11 // délka bufferu pro převod 32-bitového čísla na řetězec

/*
 * Konfigurační konstanty konzolového uživatelského rozhraní:
 */

#define CMD_MAX_LENGTH 16 // maximální délka řetězce příkazu uživatele
#define ARGS_CMD "args" // příkaz pro výpis parametrů serveru
#define STATS_CMD "stats" // příkaz pro výpis statistik serveru
#define HELP_CMD "help" // příkaz pro výpis všech dostupných příkazů
#define EXIT_CMD "exit" // příkaz pro zastavení komunikace a ukončení programu
#define MAX_STR_LENGHT 65536 // maximální délka jednoho logovaného řetězce

#endif /* CONFIG_H */