/* 
 * Author: Petr Kozler
 */

#ifndef PROTOCOL_H
#define PROTOCOL_H

/*
 * Dostupné požadavky klienta a počty argumentů:
 */

#define MSG_LOGIN_CLIENT "login-client" // typ zprávy: přihlášení připojeného klienta
#define MSG_LOGIN_CLIENT_ARGC 1 // počet argumentů: přihlášení připojeného klienta
#define MSG_LOGIN_CLIENT_ID_ARGC 2 // počet argumentů: odpověď serveru pro přihlášení (obsahuje ID)
#define MSG_LOGOUT_CLIENT "logout-client" // typ zprávy: odhlášení připojeného klienta
#define MSG_LOGOUT_CLIENT_ARGC 0 // počet argumentů: odhlášení odpojeného klienta
#define MSG_CREATE_GAME "create-game" // typ zprávy: vytvoření hry klientem
#define MSG_CREATE_GAME_ARGC 4 // počet argumentů: vytvoření hry klientem
#define MSG_CREATE_GAME_ID_ARGC 2 // počet argumentů: odpověď serveru pro vytvoření hry (obsahuje ID)
#define MSG_JOIN_GAME "join-game" // typ zprávy: připojení klienta do hry
#define MSG_JOIN_GAME_ARGC 1 // počet argumentů: připojení klienta do hry
#define MSG_LEAVE_GAME "leave-game" // typ zprávy: odpojení klienta ze hry
#define MSG_LEAVE_GAME_ARGC 0 // počet argumentů: odpojení klienta ze hry
#define MSG_START_GAME "start-game" // typ zprávy: zahájení nového kola hry
#define MSG_START_GAME_ARGC 0 // počet argumentů: zahájení nového kola hry
#define MSG_PLAY_GAME "play-game" // typ zprávy: tah klienta ve hře
#define MSG_PLAY_GAME_ARGC 2 // počet argumentů: tah klienta ve hře

/*
 * Ošetřované chyby požadavků klienta:
 */

#define MSG_ERR_INVALID_ARG_COUNT "invalid-arg-count" // typ chyby: nesprávný počet argumentů zprávy
#define MSG_ERR_ALREADY_LOGGED "already-logged" // typ chyby: uživatel byl již přihlášen
#define MSG_ERR_INVALID_NAME "invalid-name" // typ chyby: neplatné jméno
#define MSG_ERR_EXISTING_NAME "existing-name" // typ chyby: existující jméno
#define MSG_ERR_NOT_LOGGED "not-logged" // typ chyby: uživatel nebyl dosud přihlášen
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
#define MSG_ERR_NOT_ENOUGH_PLAYERS "not-enough-players" // typ chyby: nedostatek hráčů pro zahájení kola
#define MSG_ERR_ROUND_ALREADY_STARTED "round-already-started" // typ chyby: kolo bylo již zahájeno
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
#define MSG_GAME_LIST_ITEM_ARGC 6 // počet argumentů: položka seznamu her
#define MSG_GAME_DETAIL "game-detail" // typ zprávy: změna ve stavu hry
#define MSG_GAME_DETAIL_ARGC 11 // počet argumentů: změna ve stavu hry
#define MSG_GAME_PLAYER "game-player" // typ zprávy: položka seznamu aktuálních hráčů hry
#define MSG_GAME_PLAYER_ARGC 3 // počet argumentů: položka seznamu aktuálních hráčů hry

/*
 * Ostatní konstanty aplikačního protokolu:
 */

#define MSG_ACK_ARGC 1 // počet argumentů běžné potvrzovací zprávy
#define MSG_ERR_ARGC 2 // počet argumentů běžné chybové zprávy
#define BOARD_CELL_SEED_SIZE 1 // délka řetězce představujícího označení hráče na daném políčku ve znacích
#define MSG_TRUE "true" // logická pravda, značí mj. pozitivní přijetí zprávy (požadavek klienta je v pořádku)
#define MSG_FALSE "false" // logická nepravda, značí mj. negativní přijetí zprávy (v požadavku klienta je chyba)
#define SEPARATOR ";" // 1znakový oddělovač tokenů (označení typu a argumentů) zprávy

#endif /* PROTOCOL_H */
