package configuration;

/**
 * Třída pro uložení konfigurace programu.
 */
public final class Config {
	
	/** výchozí adresa socketu */
	public static final String DEFAULT_HOST = "127.0.0.1";
	/** výchozí port socketu */
	public static final int DEFAULT_PORT = 10001;
	/** timeout komunikačních funkcí */
	public static final int SOCKET_TIMEOUT_MILLIS = 3000;
	/** prodleva časovače */
	public static final int TIMER_DELAY_MILLIS = 300;
	/** výchozí šířka okna */
	public static final int DEFAULT_WINDOW_WIDTH = 640;
	/** výchozí výška okna */
	public static final int DEFAULT_WINDOW_HEIGHT = 480;
	/** minimální počet hráčů ve hře */
	public static final byte MIN_PLAYERS_SIZE = 2;
	/** maximální počet hráčů ve hře */
	public static final byte MAX_PLAYERS_SIZE = 8;
	/** minimální velikost hracího pole */
	public static final byte MIN_MATRIX_SIZE = 3;
	/** maximální velikost hracího pole */
	public static final byte MAX_MATRIX_SIZE = 16;
	/** hlavička zprávy */
	public static final int MSG_CODE = 1;
	/** hlavička potvrzení */
	public static final int ACK_CODE = -1;
	
	/**
	 * Privátní konstruktor, aby se zamezilo vytvoření instance.
	 */
	private Config() {
		// skutečně žádný kód
	}

}
