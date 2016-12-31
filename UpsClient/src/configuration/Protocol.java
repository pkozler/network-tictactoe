package configuration;

import communication.tokens.ClientMessageErrorArg;
import communication.tokens.ClientMessageType;
import communication.tokens.ServerMessageType;

/**
 *
 * @author Petr Kozler
 */
public class Protocol {
    
    /*
     * Dostupné požadavky klienta a počty argumentů:
     */

    public static final ClientMessageType MSG_LOGIN_CLIENT = new ClientMessageType("login-client", 1);
    public static final ClientMessageType MSG_LOGOUT_CLIENT = new ClientMessageType("logout-client");
    public static final ClientMessageType MSG_CREATE_GAME = new ClientMessageType("create-game", 4);
    public static final ClientMessageType MSG_JOIN_GAME = new ClientMessageType("join-game", 1);
    public static final ClientMessageType MSG_LEAVE_GAME = new ClientMessageType("leave-game");
    public static final ClientMessageType MSG_PLAY_GAME = new ClientMessageType("play-game", 2);
    public static final int MSG_LOGIN_CLIENT_ID_ARGC = 2;
    public static final int MSG_CREATE_GAME_ID_ARGC = 2;

    /*
     * Ošetřované chyby požadavků klienta:
     */

    public static final ClientMessageErrorArg MSG_ERR_INVALID_ARG_COUNT = new ClientMessageErrorArg("invalid-arg-count");
    public static final ClientMessageErrorArg MSG_ERR_ALREADY_LOGGED = new ClientMessageErrorArg("already-logged");
    public static final ClientMessageErrorArg MSG_ERR_INVALID_NAME = new ClientMessageErrorArg("invalid-name");
    public static final ClientMessageErrorArg MSG_ERR_EXISTING_NAME = new ClientMessageErrorArg("existing-name");
    public static final ClientMessageErrorArg MSG_ERR_NOT_LOGGED = new ClientMessageErrorArg("not-logged");
    public static final ClientMessageErrorArg MSG_ERR_INVALID_PLAYER_COUNT = new ClientMessageErrorArg("invalid-player-count", 4);
    public static final ClientMessageErrorArg MSG_ERR_INVALID_BOARD_SIZE = new ClientMessageErrorArg("invalid-board-size", 4);
    public static final ClientMessageErrorArg MSG_ERR_INVALID_CELL_COUNT = new ClientMessageErrorArg("invalid-cell-count", 4);
    public static final ClientMessageErrorArg MSG_ERR_INVALID_ID = new ClientMessageErrorArg("invalid-id");
    public static final ClientMessageErrorArg MSG_ERR_EXISTING_ID = new ClientMessageErrorArg("existing-id");
    public static final ClientMessageErrorArg MSG_ERR_ID_NOT_FOUND = new ClientMessageErrorArg("id-not-found");
    public static final ClientMessageErrorArg MSG_ERR_ROOM_FULL = new ClientMessageErrorArg("room-full");
    public static final ClientMessageErrorArg MSG_ERR_ALREADY_IN_ROOM = new ClientMessageErrorArg("already-in-room");
    public static final ClientMessageErrorArg MSG_ERR_NOT_IN_ROOM = new ClientMessageErrorArg("not-in-room");
    public static final ClientMessageErrorArg MSG_ERR_ROUND_NOT_STARTED = new ClientMessageErrorArg("round-not-started");
    public static final ClientMessageErrorArg MSG_ERR_CANNOT_PLAY_IN_ROUND = new ClientMessageErrorArg("cannot-play-in-round");
    public static final ClientMessageErrorArg MSG_ERR_CANNOT_PLAY_NOW = new ClientMessageErrorArg("cannot-play-now");
    public static final ClientMessageErrorArg MSG_ERR_CELL_OCCUPIED = new ClientMessageErrorArg("cell-occupied");
    public static final ClientMessageErrorArg MSG_ERR_CELL_OUT_OF_BOARD = new ClientMessageErrorArg("cell-out-of-board");
    public static final ClientMessageErrorArg MSG_ERR_INVALID_POSITION = new ClientMessageErrorArg("invalid-position");

    /*
     * Dostupné odpovědi serveru a počty argumentů:
     */

    public static final ServerMessageType MSG_PLAYER_LIST = new ServerMessageType("player-list", 1, true);
    public static final ServerMessageType MSG_PLAYER_LIST_ITEM = new ServerMessageType("player-item", 3, false);
    public static final ServerMessageType MSG_GAME_LIST = new ServerMessageType("game-list", 1, true);
    public static final ServerMessageType MSG_GAME_LIST_ITEM = new ServerMessageType("game-item", 8, false);
    public static final ServerMessageType MSG_GAME_DETAIL = new ServerMessageType("game-detail", 3, true);
    public static final ServerMessageType MSG_GAME_PLAYER = new ServerMessageType("game-player", 3, false);

    /*
     * Ostatní konstanty aplikačního protokolu:
     */

    public static final int MSG_ACK_ARGC = 1;
    public static final int MSG_ERR_ARGC = 2;
    public static final int BOARD_CELL_SEED_SIZE = 1;
    public static final String NORMAL_CELL_SYMBOL = "_";
    public static final String WINNING_CELL_SYMBOL = "W";
    public static final String MSG_TRUE = "true";
    public static final String MSG_FALSE = "false";
    public static final String SEPARATOR = "/";
    
}
