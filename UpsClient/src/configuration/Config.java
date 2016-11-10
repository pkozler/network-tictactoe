package configuration;

import communication.tokens.MessageErrorArg;
import communication.tokens.ClientMessageType;
import communication.tokens.ServerMessageType;

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
    public static final int SOCKET_TIMEOUT_MILLIS = 1000;
    public static final int MAX_TIMEOUTS = 5;
    public static final byte MIN_BOARD_SIZE = 3;
    public static final byte MAX_BOARD_SIZE = 12;
    public static final byte MIN_PLAYERS_SIZE = 2;
    public static final byte MAX_PLAYERS_SIZE = 4;
    public static final byte MIN_CELL_COUNT = 2;
    public static final byte MAX_CELL_COUNT = 12;
    public static final int MAX_NAME_LENGTH = 16;
    public static final int DEFAULT_WINDOW_WIDTH = 1024;
    public static final int DEFAULT_WINDOW_HEIGHT = 768;
    
    /*
     * Dostupné požadavky klienta a počty argumentů:
     */

    public static final ClientMessageType MSG_ACTIVATE_CLIENT = new ClientMessageType("activate-client", 1);
    public static final ClientMessageType MSG_DEACTIVATE_CLIENT = new ClientMessageType("deactivate-client");
    public static final ClientMessageType MSG_CREATE_GAME = new ClientMessageType("create-game", 4);
    public static final ClientMessageType MSG_JOIN_GAME = new ClientMessageType("join-game", 1);
    public static final ClientMessageType MSG_LEAVE_GAME = new ClientMessageType("leave-game");
    public static final ClientMessageType MSG_START_GAME = new ClientMessageType("start-game");
    public static final ClientMessageType MSG_PLAY_GAME = new ClientMessageType("play-game", 2);
    public static final int MSG_ACTIVATE_CLIENT_ID_ARGC = 2;
    public static final int MSG_CREATE_GAME_ID_ARGC = 2;

    /*
     * Ošetřované chyby požadavků klienta:
     */

    public static final MessageErrorArg MSG_ERR_INVALID_ARG_COUNT = new MessageErrorArg("invalid-arg-count");
    public static final MessageErrorArg MSG_ERR_ALREADY_ACTIVE = new MessageErrorArg("already-active");
    public static final MessageErrorArg MSG_ERR_INVALID_NAME = new MessageErrorArg("invalid-name");
    public static final MessageErrorArg MSG_ERR_EXISTING_NAME = new MessageErrorArg("existing-name");
    public static final MessageErrorArg MSG_ERR_NOT_ACTIVE = new MessageErrorArg("not-active");
    public static final MessageErrorArg MSG_ERR_INVALID_PLAYER_COUNT = new MessageErrorArg("invalid-player-count", 4);
    public static final MessageErrorArg MSG_ERR_INVALID_BOARD_SIZE = new MessageErrorArg("invalid-board-size", 4);
    public static final MessageErrorArg MSG_ERR_INVALID_CELL_COUNT = new MessageErrorArg("invalid-cell-count", 4);
    public static final MessageErrorArg MSG_ERR_INVALID_ID = new MessageErrorArg("invalid-id");
    public static final MessageErrorArg MSG_ERR_EXISTING_ID = new MessageErrorArg("existing-id");
    public static final MessageErrorArg MSG_ERR_ID_NOT_FOUND = new MessageErrorArg("id-not-found");
    public static final MessageErrorArg MSG_ERR_ROOM_FULL = new MessageErrorArg("room-full");
    public static final MessageErrorArg MSG_ERR_ALREADY_IN_ROOM = new MessageErrorArg("already-in-room");
    public static final MessageErrorArg MSG_ERR_NOT_IN_ROOM = new MessageErrorArg("not-in-room");
    public static final MessageErrorArg MSG_ERR_NOT_ENOUGH_PLAYERS = new MessageErrorArg("not-enough-players");
    public static final MessageErrorArg MSG_ERR_ROUND_NOT_FINISHED = new MessageErrorArg("round-not-finished");
    public static final MessageErrorArg MSG_ERR_ROUND_NOT_STARTED = new MessageErrorArg("round-not-started");
    public static final MessageErrorArg MSG_ERR_CANNOT_PLAY_IN_ROUND = new MessageErrorArg("cannot-play-in-round");
    public static final MessageErrorArg MSG_ERR_CANNOT_PLAY_NOW = new MessageErrorArg("cannot-play-now");
    public static final MessageErrorArg MSG_ERR_CELL_OCCUPIED = new MessageErrorArg("cell-occupied");
    public static final MessageErrorArg MSG_ERR_CELL_OUT_OF_BOARD = new MessageErrorArg("cell-out-of-board");
    public static final MessageErrorArg MSG_ERR_INVALID_POSITION = new MessageErrorArg("invalid-position");

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
    
    /*
     * Propojení požadavků klienta a možných chybových stavů:
     */
    
    // TODO doplnit formátované texty chybových hlášení !!!
    
    static {
        MSG_ERR_INVALID_NAME.associateWithMessageType(MSG_ACTIVATE_CLIENT, "Zadaný nickname hráče není platný.");
        MSG_ERR_EXISTING_NAME.associateWithMessageType(MSG_ACTIVATE_CLIENT, "Hráč se zadaným nickname již existuje.");
        
        MSG_ERR_INVALID_NAME.associateWithMessageType(MSG_CREATE_GAME, "");
        MSG_ERR_EXISTING_NAME.associateWithMessageType(MSG_CREATE_GAME, "");
        MSG_ERR_INVALID_BOARD_SIZE.associateWithMessageType(MSG_CREATE_GAME, "");
        MSG_ERR_INVALID_PLAYER_COUNT.associateWithMessageType(MSG_CREATE_GAME, "");
        MSG_ERR_INVALID_CELL_COUNT.associateWithMessageType(MSG_CREATE_GAME, "");
        
        MSG_ERR_INVALID_ID.associateWithMessageType(MSG_JOIN_GAME, "");
        MSG_ERR_ID_NOT_FOUND.associateWithMessageType(MSG_JOIN_GAME, "");
        MSG_ERR_ALREADY_IN_ROOM.associateWithMessageType(MSG_JOIN_GAME, "");
        MSG_ERR_ROOM_FULL.associateWithMessageType(MSG_JOIN_GAME, "");
        
        MSG_ERR_NOT_IN_ROOM.associateWithMessageType(MSG_LEAVE_GAME, "");
        
        MSG_ERR_NOT_IN_ROOM.associateWithMessageType(MSG_START_GAME, "");
        MSG_ERR_NOT_ENOUGH_PLAYERS.associateWithMessageType(MSG_START_GAME, "");
        MSG_ERR_ROUND_NOT_FINISHED.associateWithMessageType(MSG_START_GAME, "");
        
        MSG_ERR_NOT_IN_ROOM.associateWithMessageType(MSG_START_GAME, "");
        MSG_ERR_ROUND_NOT_STARTED.associateWithMessageType(MSG_START_GAME, "");
        MSG_ERR_CANNOT_PLAY_IN_ROUND.associateWithMessageType(MSG_START_GAME, "");
        MSG_ERR_CANNOT_PLAY_NOW.associateWithMessageType(MSG_START_GAME, "");
        MSG_ERR_INVALID_POSITION.associateWithMessageType(MSG_START_GAME, "");
        MSG_ERR_CELL_OUT_OF_BOARD.associateWithMessageType(MSG_START_GAME, "");
        MSG_ERR_CELL_OCCUPIED.associateWithMessageType(MSG_START_GAME, "");
    }
    
}
