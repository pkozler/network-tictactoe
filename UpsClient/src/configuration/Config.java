package configuration;

/**
 *
 * @author Petr Kozler
 */
public class Config {
    
    /*
     * Základní konfigurační konstanty klienta:
     */

    public static final String DEFAULT_HOST = "127.0.0.1";
    public static final int MIN_PORT = 1;
    public static final int MAX_PORT = 65535;
    public static final int DEFAULT_PORT = 10001;
    public static final int SOCKET_TIMEOUT_MILLIS = 5000;
    public static final int TIMER_PERIOD_MILLIS = 1000;
    public static final int MIN_BOARD_SIZE = 3;
    public static final int MAX_BOARD_SIZE = 12;
    public static final int MIN_PLAYERS_SIZE = 2;
    public static final int MAX_PLAYERS_SIZE = 4;
    public static final int MIN_CELL_COUNT = 2;
    public static final int MAX_CELL_COUNT = 12;

    /*
     * Dostupné požadavky klienta a počty argumentů:
     */

    public static final String MSG_ACTIVATE_CLIENT = "activate-client"; // typ zprávy: aktivace připojeného klienta
    public static final int MSG_ACTIVATE_CLIENT_ARGC = 1; // počet argumentů: aktivace připojeného klienta
    public static final int MSG_ACTIVATE_CLIENT_ID_ARGC = 2; // počet argumentů: odpověď serveru pro aktivaci (obsahuje ID)
    public static final String MSG_DEACTIVATE_CLIENT = "deactivate-client"; // typ zprávy: deaktivace odpojeného klienta
    public static final int MSG_DEACTIVATE_CLIENT_ARGC = 1; // počet argumentů: deaktivace odpojeného klienta
    public static final String MSG_CREATE_GAME = "create-game"; // typ zprávy: vytvoření hry klientem
    public static final int MSG_CREATE_GAME_ARGC = 4; // počet argumentů: vytvoření hry klientem
    public static final int MSG_CREATE_GAME_ID_ARGC = 2; // počet argumentů: odpověď serveru pro vytvoření hry (obsahuje ID)
    public static final String MSG_JOIN_GAME = "join-game"; // typ zprávy: připojení klienta do hry
    public static final int MSG_JOIN_GAME_ARGC = 1; // počet argumentů: připojení klienta do hry
    public static final String MSG_LEAVE_GAME = "leave-game"; // typ zprávy: odpojení klienta ze hry
    public static final int MSG_LEAVE_GAME_ARGC = 0; // počet argumentů: odpojení klienta ze hry
    public static final String MSG_START_GAME = "start-game"; // typ zprávy: zahájení herního kola klientem
    public static final int MSG_START_GAME_ARGC = 0; // počet argumentů: zahájení herního kola klientem
    public static final String MSG_PLAY_GAME = "play-game"; // typ zprávy: tah klienta ve hře
    public static final int MSG_PLAY_GAME_ARGC = 2; // počet argumentů: tah klienta ve hře

    /*
     * Ošetřované chyby požadavků klienta:
     */

    public static final String MSG_ERR_INVALID_ARG_COUNT = "invalid-arg-count"; // typ chyby: nesprávný počet argumentů zprávy
    public static final String MSG_ERR_ALREADY_ACTIVE = "already-active"; // typ chyby: uživatel byl již aktivován
    public static final String MSG_ERR_INVALID_NAME = "invalid-name"; // typ chyby: neplatné jméno
    public static final String MSG_ERR_EXISTING_NAME = "existing-name"; // typ chyby: existující jméno
    public static final String MSG_ERR_NOT_ACTIVE = "not-active"; // typ chyby: uživatel nebyl dosud aktivován
    public static final String MSG_ERR_INVALID_PLAYER_COUNT = "invalid-player-count"; // typ chyby: neplatný počet hráčů hry
    public static final int MSG_ERR_INVALID_PLAYER_COUNT_ARGC = 4; // počet argumentů: neplatný počet hráčů hry
    public static final String MSG_ERR_INVALID_BOARD_SIZE = "invalid-board-size"; // typ chyby: neplatný rozměr pole hry
    public static final int MSG_ERR_INVALID_BOARD_SIZE_ARGC = 4; // počet argumentů: neplatný rozměr pole hry
    public static final String MSG_ERR_INVALID_CELL_COUNT = "invalid-cell-count"; // typ chyby: neplatný počet políček k obsazení ve hře
    public static final int MSG_ERR_INVALID_CELL_COUNT_ARGC = 4; // počet argumentů: neplatný počet políček k obsazení ve hře
    public static final String MSG_ERR_INVALID_ID = "invalid-id"; // typ chyby: neplatné ID
    public static final String MSG_ERR_EXISTING_ID = "existing-id"; // typ chyby: ID existuje
    public static final String MSG_ERR_ID_NOT_FOUND = "id-not-found"; // typ chyby: položka se zadaným ID neexistuje
    public static final String MSG_ERR_ROOM_FULL = "room-full"; // typ chyby: herní místnost plná
    public static final String MSG_ERR_ALREADY_IN_ROOM = "already-in-room"; // typ chyby: hráč již připojen ve hře
    public static final String MSG_ERR_NOT_IN_ROOM = "not-in-room"; // typ chyby: hráč není připojen ve hře
    public static final String MSG_ERR_NOT_ENOUGH_PLAYERS = "not-enough-players"; // typ chyby: nedostatečný počet hráčů pro zahájení hry
    public static final String MSG_ERR_ROUND_NOT_FINISHED = "round-not-finished"; // typ chyby: herní kolo ještě nebylo odehráno
    public static final String MSG_ERR_ROUND_NOT_STARTED = "round-not-started"; // typ chyby: hra ještě nebyla zahájena
    public static final String MSG_ERR_CANNOT_PLAY_IN_ROUND = "cannot-play-in-round"; // typ chyby: hráč nemůže v daném kole hrát
    public static final String MSG_ERR_CANNOT_PLAY_NOW = "cannot-play-now"; // typ chyby: hráč není na řadě
    public static final String MSG_ERR_CELL_OCCUPIED = "cell-occupied"; // typ chyby: tah na obsazenou pozici
    public static final String MSG_ERR_CELL_OUT_OF_BOARD = "cell-out-of-board"; // typ chyby: tah mimo hranice herního pole
    public static final String MSG_ERR_INVALID_POSITION = "invalid-position"; // typ chyby: tah na neplatnou pozici

    /*
     * Dostupné odpovědi serveru a počty argumentů:
     */

    public static final String MSG_SERVER_SHUTDOWN = "server-shutdown"; // typ zprávy: restart serveru
    public static final int MSG_SERVER_SHUTDOWN_ARGC = 0; // počet argumentů: restart serveru
    public static final String MSG_CLIENT_LIST = "client-list"; // typ zprávy: změna v seznamu hráčů
    public static final int MSG_CLIENT_LIST_ARGC = 1; // počet argumentů: změna v seznamu hráčů
    public static final String MSG_CLIENT_LIST_ITEM = "client-item"; // typ zprávy: položka seznamu hráčů
    public static final int MSG_CLIENT_LIST_ITEM_ARGC = 3; // počet argumentů: položka seznamu hráčů
    public static final String MSG_GAME_LIST = "game-list"; // typ zprávy: změna v seznamu her
    public static final int MSG_GAME_LIST_ARGC = 1; // počet argumentů: změna v seznamu her
    public static final String MSG_GAME_LIST_ITEM = "game-item"; // typ zprávy: položka seznamu her
    public static final int MSG_GAME_LIST_ITEM_ARGC = 8; // počet argumentů: položka seznamu her
    public static final String MSG_GAME_STATUS = "game-status"; // typ zprávy: změna ve stavu hry
    public static final int MSG_GAME_STATUS_ARGC = 3; // počet argumentů: změna ve stavu hry
    public static final String MSG_GAME_PLAYER = "game-player"; // typ zprávy: položka seznamu aktuálních hráčů hry
    public static final int MSG_GAME_PLAYER_ARGC = 3; // počet argumentů: položka seznamu aktuálních hráčů hry

    /*
     * Ostatní konstanty aplikačního protokolu:
     */

    public static final int MSG_ACK_ARGC = 1; // počet argumentů běžné potvrzovací zprávy
    public static final int MSG_ERR_ARGC = 2; // počet argumentů běžné chybové zprávy
    public static final int BOARD_CELL_SEED_SIZE = 1; // délka řetězce představujícího označení hráče na daném políčku v bajtech
    public static final String NORMAL_CELL_SYMBOL = "_"; // označení políčka, které nepatří mezi políčka vítěze, v řetězci zprávy
    public static final String WINNING_CELL_SYMBOL = "W"; // označení políčka, které je políčkem vítěze, v řetězci zprávy
    public static final String MSG_OK = "ok"; // pozitivní přijetí zprávy (požadavek klienta je v pořádku)
    public static final String MSG_FAIL = "fail"; // negativní přijetí zprávy (v požadavku klienta je chyba)
    public static final String DELIMITER = "/"; // 1znakový oddělovač tokenů (označení typu a argumentů) zprávy

}
