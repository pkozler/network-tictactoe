package application;

/**
 * Třída Configuration definuje základní konfigurační konstanty
 * používané v různých částech programu.
 * 
 * @author Petr Kozler
 */
public final class Configuration {
    
    /**
     * klíč parametru čísla portu
     */
    public static final String PORT_OPTION = "-port";
    
    /**
     * klíč parametru IP adresy
     */
    public static final String HOST_OPTION = "-host";
    
    /**
     * výchozí číslo portu serveru
     */
    public static final int DEFAULT_PORT = 7654;
    
    /**
     * maximální povolené číslo portu serveru
     */
    public static final int MAX_PORT = 65535;
    
    /**
     * výchozí hostname serveru
     */
    public static final String DEFAULT_HOST = "localhost";
    
    /**
     * výchozí šířka okna
     */
    public static final int DEFAULT_WIDTH = 640;
    
    /**
     * výchozí výška okna
     */
    public static final int DEFAULT_HEIGHT = 320;
    
    /**
     * řetězec potvrzení loginu serverem
     */
    public static final String LOGIN_ACK_STR = "OK";
    
    /**
     * řetězec odhlášení klienta serverem
     */
    public static final String CLOSE_MSG_STR = "close()";
    
    /**
     * perioda testování odezvy
     */
    public static final int PING_PERIOD_MILLIS = 1000;
    
    /**
     * timeout socketu
     */
    public static final int SOCKET_TIMEOUT_MILLIS = 3000;

}
