package configuration;

import communication.tokens.ClientMessageErrorArg;
import communication.tokens.ClientMessageType;
import communication.tokens.ServerMessageType;

/**
 * Knihovní třída Protocol obsahuje definice konstant představujících
 * textové řetězce označující jednotlivé typy zpráv aplikačního protokolu.
 * 
 * @author Petr Kozler
 */
public final class Protocol {
    
    /*
     * Dostupné požadavky klienta a počty argumentů:
     */

    public static final ClientMessageType MSG_LOGIN_CLIENT = new ClientMessageType(
            "login-client", 1, 2); // typ zprávy: přihlášení připojeného klienta
    public static final ClientMessageType MSG_LOGOUT_CLIENT = new ClientMessageType(
            "logout-client"); // typ zprávy: odhlášení připojeného klienta
    public static final ClientMessageType MSG_CREATE_GAME = new ClientMessageType(
            "create-game", 4, 2); // typ zprávy: vytvoření hry klientem
    public static final ClientMessageType MSG_JOIN_GAME = new ClientMessageType(
            "join-game", 1); // typ zprávy: připojení klienta do hry
    public static final ClientMessageType MSG_LEAVE_GAME = new ClientMessageType(
            "leave-game"); // typ zprávy: odpojení klienta ze hry
    public static final ClientMessageType MSG_START_GAME = new ClientMessageType(
            "start-game"); // typ zprávy: zahájení nového kola hry
    public static final ClientMessageType MSG_PLAY_GAME = new ClientMessageType(
            "play-game", 2); // typ zprávy: tah klienta ve hře

    /*
     * Ošetřované chyby požadavků klienta:
     */

    public static final ClientMessageErrorArg MSG_ERR_INVALID_ARG_COUNT = new ClientMessageErrorArg(
            "invalid-arg-count"); // typ chyby: nesprávný počet argumentů zprávy
    public static final ClientMessageErrorArg MSG_ERR_ALREADY_LOGGED = new ClientMessageErrorArg(
            "already-logged"); // typ chyby: uživatel byl již přihlášen
    public static final ClientMessageErrorArg MSG_ERR_INVALID_NAME = new ClientMessageErrorArg(
            "invalid-name"); // typ chyby: neplatné jméno
    public static final ClientMessageErrorArg MSG_ERR_EXISTING_NAME = new ClientMessageErrorArg(
            "existing-name"); // typ chyby: existující jméno
    public static final ClientMessageErrorArg MSG_ERR_NOT_LOGGED = new ClientMessageErrorArg(
            "not-logged"); // typ chyby: uživatel nebyl dosud přihlášen
    public static final ClientMessageErrorArg MSG_ERR_INVALID_PLAYER_COUNT = new ClientMessageErrorArg(
            "invalid-player-count", 4); // typ chyby: neplatný počet hráčů hry
    public static final ClientMessageErrorArg MSG_ERR_INVALID_BOARD_SIZE = new ClientMessageErrorArg(
            "invalid-board-size", 4); // typ chyby: neplatný rozměr pole hry
    public static final ClientMessageErrorArg MSG_ERR_INVALID_CELL_COUNT = new ClientMessageErrorArg(
            "invalid-cell-count", 4); // typ chyby: neplatný počet políček k obsazení ve hře
    public static final ClientMessageErrorArg MSG_ERR_INVALID_ID = new ClientMessageErrorArg(
            "invalid-id"); // typ chyby: neplatné ID
    public static final ClientMessageErrorArg MSG_ERR_EXISTING_ID = new ClientMessageErrorArg(
            "existing-id"); // typ chyby: ID existuje
    public static final ClientMessageErrorArg MSG_ERR_ID_NOT_FOUND = new ClientMessageErrorArg(
            "id-not-found"); // typ chyby: položka se zadaným ID neexistuje
    public static final ClientMessageErrorArg MSG_ERR_ROOM_FULL = new ClientMessageErrorArg(
            "room-full"); // typ chyby: herní místnost plná
    public static final ClientMessageErrorArg MSG_ERR_ALREADY_IN_ROOM = new ClientMessageErrorArg(
            "already-in-room"); // typ chyby: hráč již připojen ve hře
    public static final ClientMessageErrorArg MSG_ERR_NOT_IN_ROOM = new ClientMessageErrorArg(
            "not-in-room"); // typ chyby: hráč není připojen ve hře
    public static final ClientMessageErrorArg MSG_ERR_NOT_ENOUGH_PLAYERS = new ClientMessageErrorArg(
            "not-enough-players"); // typ chyby: nedostatek hráčů pro zahájení kola
    public static final ClientMessageErrorArg MSG_ERR_ROUND_ALREADY_STARTED = new ClientMessageErrorArg(
            "round-already-started"); // typ chyby: kolo bylo již zahájeno
    public static final ClientMessageErrorArg MSG_ERR_ROUND_NOT_STARTED = new ClientMessageErrorArg(
            "round-not-started"); // typ chyby: hra ještě nebyla zahájena
    public static final ClientMessageErrorArg MSG_ERR_NOT_ON_TURN = new ClientMessageErrorArg(
            "not_on_turn"); // typ chyby: hráč není na řadě
    public static final ClientMessageErrorArg MSG_ERR_CELL_OCCUPIED = new ClientMessageErrorArg(
            "cell-occupied"); // typ chyby: tah na obsazenou pozici
    public static final ClientMessageErrorArg MSG_ERR_CELL_OUT_OF_BOARD = new ClientMessageErrorArg(
            "cell-out-of-board"); // typ chyby: tah mimo hranice herního pole

    /*
     * Dostupné odpovědi serveru a počty argumentů:
     */

    public static final ServerMessageType MSG_PLAYER_LIST = new ServerMessageType(
            "player-list", true); // typ zprávy: změna v seznamu hráčů
    public static final ServerMessageType MSG_PLAYER_LIST_ITEM = new ServerMessageType(
            "player-item", 4); // typ zprávy: položka seznamu hráčů
    public static final ServerMessageType MSG_GAME_LIST = new ServerMessageType(
            "game-list", true); // typ zprávy: změna v seznamu her
    public static final ServerMessageType MSG_GAME_LIST_ITEM = new ServerMessageType(
            "game-item", 6); // typ zprávy: položka seznamu her
    public static final ServerMessageType MSG_GAME_DETAIL = new ServerMessageType(
            "game-detail", 14, true); // typ zprávy: změna ve stavu hry
    public static final ServerMessageType MSG_GAME_PLAYER = new ServerMessageType(
            "game-player", 4); // typ zprávy: položka seznamu aktuálních hráčů hry

    /*
     * Ostatní konstanty aplikačního protokolu:
     */

    public static final int BOARD_CELL_SEED_SIZE = 1; // délka řetězce představujícího označení hráče na daném políčku ve znacích
    public static final String MSG_TRUE = "true"; // logická pravda, značí mj. pozitivní přijetí zprávy (požadavek klienta je v pořádku)
    public static final String MSG_FALSE = "false"; // logická nepravda, značí mj. negativní přijetí zprávy (v požadavku klienta je chyba)
    public static final String SEPARATOR = ";"; // 1znakový oddělovač tokenů (označení typu a argumentů) zprávy
    
    /**
     * Privátní konstruktor pro zamezení vytvoření instance.
     */
    private Protocol() {
        // žádný kód
    }
    
}
