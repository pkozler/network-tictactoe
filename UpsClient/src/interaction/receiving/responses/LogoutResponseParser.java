package interaction.receiving.responses;

import communication.TcpClient;
import communication.TcpMessage;
import communication.tokens.InvalidMessageArgsException;
import communication.tokens.MissingMessageArgsException;
import configuration.Protocol;
import interaction.receiving.AResponseParser;
import visualisation.components.GameListPanel;
import visualisation.components.PlayerListPanel;
import visualisation.components.StatusBarPanel;

/**
 * Třída LogoutResponseParser představuje parser odpovědi serveru
 * na požadavek na odhlášení.
 * 
 * @author Petr Kozler
 */
public class LogoutResponseParser extends AResponseParser {

    /**
     * Vytvoří parser pro zpracování odpovědi serveru na požadavek na odhlášení.
     * 
     * @param client objekt klienta
     * @param playerListPanel panel seznamu hráčů
     * @param gameListPanel panel seznamu her
     * @param statusBarPanel panel stavového řádku
     * @param message zpráva
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    public LogoutResponseParser(TcpClient client, PlayerListPanel playerListPanel,
            GameListPanel gameListPanel, StatusBarPanel statusBarPanel, TcpMessage message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        super(client, playerListPanel, gameListPanel, statusBarPanel, message);
        
        if (MESSAGE.getNextBoolArg()) {
            return;
        }
        
        messageErrorKeyword = MESSAGE.getNextArg();
    }

    /**
     * Vrátí výsledek požadavku a obnoví seznam hráčů.
     * 
     * @return výsledek
     */
    @Override
    public String updateClient() {
        if (messageErrorKeyword == null) {
            return null;
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_ALREADY_LOGGED.KEYWORD)) {
            return "Hráč ještě není přihlášen";
        }
        
        return "Hráč byl odhlášen ze serveru";
    }

}
