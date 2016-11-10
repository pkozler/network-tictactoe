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
#define SOCKET_TIMEOUT_SEC 1 // timeout příjmu/odeslání zprávy klienta v sekundách
#define MAX_TIMEOUTS 5 // maximální počet timeoutů v řadě před zrušením spojení
#define MAX_NAME_LENGTH 16
#define MIN_BOARD_SIZE 3
#define MAX_BOARD_SIZE 12
#define MIN_PLAYERS_SIZE 2
#define MAX_PLAYERS_SIZE 4
#define MIN_CELL_COUNT 2
#define MAX_CELL_COUNT 12
#define LOG_LINE_LENGTH 1024 // velikost bufferu pro výpis hlášení

/*
 * Dostupné požadavky klienta a počty argumentů:
 */

#define MSG_ACTIVATE_CLIENT "activate-client" // typ zprávy: aktivace připojeného klienta
#define MSG_ACTIVATE_CLIENT_ARGC 1 // počet argumentů: aktivace připojeného klienta
#define MSG_ACTIVATE_CLIENT_ID_ARGC 2 // počet argumentů: odpověď serveru pro aktivaci (obsahuje ID)
#define MSG_DEACTIVATE_CLIENT "deactivate-client" // typ zprávy: deaktivace odpojeného klienta
#define MSG_DEACTIVATE_CLIENT_ARGC 0 // počet argumentů: deaktivace odpojeného klienta
#define MSG_CREATE_GAME "create-game" // typ zprávy: vytvoření hry klientem
#define MSG_CREATE_GAME_ARGC 4 // počet argumentů: vytvoření hry klientem
#define MSG_CREATE_GAME_ID_ARGC 2 // počet argumentů: odpověď serveru pro vytvoření hry (obsahuje ID)
#define MSG_JOIN_GAME "join-game" // typ zprávy: připojení klienta do hry
#define MSG_JOIN_GAME_ARGC 1 // počet argumentů: připojení klienta do hry
#define MSG_LEAVE_GAME "leave-game" // typ zprávy: odpojení klienta ze hry
#define MSG_LEAVE_GAME_ARGC 0 // počet argumentů: odpojení klienta ze hry
#define MSG_PLAY_GAME "play-game" // typ zprávy: tah klienta ve hře
#define MSG_PLAY_GAME_ARGC 2 // počet argumentů: tah klienta ve hře

/*
 * Ošetřované chyby požadavků klienta:
 */

#define MSG_ERR_INVALID_ARG_COUNT "invalid-arg-count" // typ chyby: nesprávný počet argumentů zprávy
#define MSG_ERR_ALREADY_ACTIVE "already-active" // typ chyby: uživatel byl již aktivován
#define MSG_ERR_INVALID_NAME "invalid-name" // typ chyby: neplatné jméno
#define MSG_ERR_EXISTING_NAME "existing-name" // typ chyby: existující jméno
#define MSG_ERR_NOT_ACTIVE "not-active" // typ chyby: uživatel nebyl dosud aktivován
#define MSG_ERR_INVALID_PLAYER_COUNT "invalid-player-count" // typ chyby: neplatný počet hráčů hry
#define MSG_ERR_INVALID_PLAYER_COUNT_ARGC 4 // počet argumentů: neplatný počet hráčů hry
#define MSG_ERR_INVALID_BOARD_SIZE "invalid-board-size" // typ chyby: neplatný rozměr pole hry
#define MSG_ERR_INVALID_BOARD_SIZE_ARGC 4 // počet argumentů: neplatný rozměr pole hry
#define MSG_ERR_INVALID_CELL_COUNT "invalid-cell-count" // typ chyby: neplatný počet políček k obsazení ve hře
#define MSG_ERR_INVALID_CELL_COUNT_ARGC 4 // počet argumentů: neplatný počet políček k obsazení ve hře
#define MSG_ERR_INVALID_ID "invalid-id" // typ chyby: neplatné ID
#define MSG_ERR_EXISTING_ID "existing-id" // typ chyby: ID existuje
#define MSG_ERR_ID_NOT_FOUND "id-not-found" // typ chyby: položka se zadaným ID neexistuje
#define MSG_ERR_ROOM_FULL "room-full" // typ chyby: herní místnost plná
#define MSG_ERR_ALREADY_IN_ROOM "already-in-room" // typ chyby: hráč již připojen ve hře
#define MSG_ERR_NOT_IN_ROOM "not-in-room" // typ chyby: hráč není připojen ve hře
#define MSG_ERR_ROUND_NOT_STARTED "round-not-started" // typ chyby: hra ještě nebyla zahájena
#define MSG_ERR_CANNOT_PLAY_IN_ROUND "cannot-play-in-round" // typ chyby: hráč nemůže v daném kole hrát
#define MSG_ERR_CANNOT_PLAY_NOW "cannot-play-now" // typ chyby: hráč není na řadě
#define MSG_ERR_CELL_OCCUPIED "cell-occupied" // typ chyby: tah na obsazenou pozici
#define MSG_ERR_CELL_OUT_OF_BOARD "cell-out-of-board" // typ chyby: tah mimo hranice herního pole
#define MSG_ERR_INVALID_POSITION "invalid-position" // typ chyby: tah na neplatnou pozici

/*
 * Dostupné odpovědi serveru a počty argumentů:
 */

#define MSG_PLAYER_LIST "player-list" // typ zprávy: změna v seznamu hráčů
#define MSG_PLAYER_LIST_ARGC 1 // počet argumentů: změna v seznamu hráčů
#define MSG_PLAYER_LIST_ITEM "player-item" // typ zprávy: položka seznamu hráčů
#define MSG_PLAYER_LIST_ITEM_ARGC 3 // počet argumentů: položka seznamu hráčů
#define MSG_GAME_LIST "game-list" // typ zprávy: změna v seznamu her
#define MSG_GAME_LIST_ARGC 1 // počet argumentů: změna v seznamu her
#define MSG_GAME_LIST_ITEM "game-item" // typ zprávy: položka seznamu her
#define MSG_GAME_LIST_ITEM_ARGC 8 // počet argumentů: položka seznamu her
#define MSG_GAME_DETAIL "game-detail" // typ zprávy: změna ve stavu hry
#define MSG_GAME_DETAIL_ARGC 3 // počet argumentů: změna ve stavu hry
#define MSG_GAME_PLAYER "game-player" // typ zprávy: položka seznamu aktuálních hráčů hry
#define MSG_GAME_PLAYER_ARGC 3 // počet argumentů: položka seznamu aktuálních hráčů hry

/*
 * Ostatní konstanty aplikačního protokolu:
 */

#define MSG_ACK_ARGC 1 // počet argumentů běžné potvrzovací zprávy
#define MSG_ERR_ARGC 2 // počet argumentů běžné chybové zprávy
#define BOARD_CELL_SEED_SIZE 1 // délka řetězce představujícího označení hráče na daném políčku v bajtech
#define NORMAL_CELL_SYMBOL "_" // označení políčka, které nepatří mezi políčka vítěze, v řetězci zprávy
#define WINNING_CELL_SYMBOL "W" // označení políčka, které je políčkem vítěze, v řetězci zprávy
#define MSG_TRUE "true" // logická pravda, značí mj. pozitivní přijetí zprávy (požadavek klienta je v pořádku)
#define MSG_FALSE "false" // logická nepravda, značí mj. negativní přijetí zprávy (v požadavku klienta je chyba)
#define SEPARATOR "/" // 1znakový oddělovač tokenů (označení typu a argumentů) zprávy
 
#endif /* CONFIG_H */