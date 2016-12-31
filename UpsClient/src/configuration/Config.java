package configuration;

/**
 *
 * @author Petr Kozler
 */
public class Config {
    
    /*
     * Základní konfigurační konstanty klienta:
     */

    public static final String HOST_OPTION = "-h";
    public static final String PORT_OPTION = "-p";
    public static final String DEFAULT_HOST = "127.0.0.1";
    public static final int MIN_PORT = 0;
    public static final int MAX_PORT = 65535;
    public static final int DEFAULT_PORT = 10001;
    public static final int SOCKET_TIMEOUT_MILLIS = 3000;
    public static final int PING_PERIOD_MILLIS = 1000;
    public static final byte MIN_BOARD_SIZE = 3;
    public static final byte MAX_BOARD_SIZE = 12;
    public static final byte MIN_PLAYERS_SIZE = 2;
    public static final byte MAX_PLAYERS_SIZE = 4;
    public static final byte MIN_CELL_COUNT = 2;
    public static final byte MAX_CELL_COUNT = 12;
    public static final int MAX_NAME_LENGTH = 16;
    public static final int DEFAULT_WINDOW_WIDTH = 1024;
    public static final int DEFAULT_WINDOW_HEIGHT = 768;
    
}
