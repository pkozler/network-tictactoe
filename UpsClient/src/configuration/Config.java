package configuration;

/**
 * Knihovní třída Config obsahuje definice konfiguračních konstant klienta.
 * 
 * @author Petr Kozler
 */
public final class Config {
    
    /*
     * Základní konfigurační konstanty klienta:
     */

    public static final String HELP_OPTION = "-?"; // argument příkazové řádky značící výpis nápovědy
    public static final String HOST_OPTION = "-h"; // argument příkazové řádky značící volbu IP adresy
    public static final String PORT_OPTION = "-p"; // argument příkazové řádky značící volbu čísla portu
    public static final String DEFAULT_HOST = "localhost"; // výchozí adresa serveru
    public static final int MIN_PORT = 0; // nejnižší povolené číslo portu
    public static final int MAX_PORT = 65535; // nejvyšší povolené číslo portu
    public static final int DEFAULT_PORT = 10000; // výchozí port pro naslouchání
    public static final int SOCKET_TIMEOUT_MILLIS = 5000; // timeout příjmu/odeslání zprávy klienta v milisekundách
    public static final int PING_PERIOD_MILLIS = 1000; // perioda pro testování odezvy serveru
    public static final int MAX_MESSAGE_LENGTH = 65535; // maximální povolená délka zprávy
    public static final int MAX_NAME_LENGTH = 16; // maximální povolená délka jména hráče nebo názvu hry
    public static final byte MIN_BOARD_SIZE = 3; // minimální povolený rozměr hracího pole
    public static final byte MAX_BOARD_SIZE = 16; // maximální povolený rozměr hracího pole
    public static final byte MIN_PLAYERS_SIZE = 2; // minimální povolený počet hráčů ve hře
    public static final byte MAX_PLAYERS_SIZE = 4; // maximální povolený počet hráčů ve hře
    public static final byte MIN_CELL_COUNT = 3; // minimální povolený počet políček potřebných k obsazení
    public static final byte MAX_CELL_COUNT = 8; // maximální povolený počet políček potřebných k obsazení

    /*
     * Konfigurační konstanty grafického uživatelského rozhraní:
     */

    public static final int DEFAULT_WINDOW_WIDTH = 1200; // výchozí šířka okna
    public static final int DEFAULT_WINDOW_HEIGHT = 700; // výchozí výška okna
    
    /**
     * Privátní konstruktor pro zamezení vytvoření instance.
     */
    private Config() {
        // žádný kód
    }
    
}
