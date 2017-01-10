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
 * Třída StartGameResponseParser představuje parser odpovědi serveru
 * na požadavek na zahájení nového kola hry.
 * 
 * @author Petr Kozler
 */
public class StartGameResponseParser extends AResponseParser {

    /**
     * ID hry
     */
    protected int gameId;
    
    /**
     * Vytvoří parser pro zpracování odpovědi serveru na požadavek na zahájení nového kola hry.
     * 
     * @param client objekt klienta
     * @param playerListPanel panel seznamu hráčů
     * @param gameListPanel panel seznamu her
     * @param statusBarPanel panel stavového řádku
     * @param message zpráva
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    public StartGameResponseParser(TcpClient client, PlayerListPanel playerListPanel,
            GameListPanel gameListPanel, StatusBarPanel statusBarPanel, TcpMessage message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        super(client, playerListPanel, gameListPanel, statusBarPanel, message);
        
        if (MESSAGE.getNextBoolArg()) {
            gameId = CLIENT.getGameId();

            return;
        }
        
        messageErrorKeyword = MESSAGE.getNextArg();
    }

    /**
     * Vrátí výsledek požadavku a obnoví stav hry.
     * 
     * @return výsledek
     */
    @Override
    public String getStatusAndUpdateGUI() {
        if (messageErrorKeyword == null) {
            return String.format("Hráč zahájil nové kolo hry v místnosti s ID %d", gameId);
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_NOT_IN_ROOM)) {
            return "Hráč se nenachází v herní místnosti";
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_ROUND_ALREADY_STARTED)) {
            return "Nové kolo hry bylo již zahájeno";
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_NOT_ENOUGH_PLAYERS)) {
            return "V herní místnosti není dostatek hráčů pro zahájení nového kola hry";
        }
        
        return null;
    }

}
