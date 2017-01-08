package interaction.receiving;

import communication.TcpClient;
import communication.TcpMessage;
import visualisation.components.GameListPanel;
import visualisation.components.PlayerListPanel;
import visualisation.components.StatusBarPanel;

/**
 * Abstraktní třída AResponseParser představuje obecný parser odpovědí
 * na požadavky přijatých ze serveru.
 * 
 * @author Petr Kozler
 */
public abstract class AResponseParser extends AParser {
    
    /**
     * panel seznamu hráčů
     */
    protected final PlayerListPanel PLAYER_LIST_PANEL;
    
    /**
     * panel seznamu her
     */
    protected final GameListPanel GAME_LIST_PANEL;
    
    /**
     * chyba při odmítnutí požadavku
     */
    protected String messageErrorKeyword;
    
    /**
     * Vytvoří parser odpovědi.
     * 
     * @param client objekt klienta
     * @param playerListPanel anel seznamu hráčů
     * @param gameListPanel panel seznamu her
     * @param statusBarPanel panel stavového řádku
     * @param message zpráva
     */
    public AResponseParser(TcpClient client, PlayerListPanel playerListPanel,
            GameListPanel gameListPanel, StatusBarPanel statusBarPanel, TcpMessage message) {
        super(client, message);
        PLAYER_LIST_PANEL = playerListPanel;
        GAME_LIST_PANEL = gameListPanel;
        messageErrorKeyword = null;
    }
    
}
