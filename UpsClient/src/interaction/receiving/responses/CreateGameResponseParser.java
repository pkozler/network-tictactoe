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
 * Třída CreateGameResponseParser představuje parser odpovědi serveru
 * na požadavek na vytvoření hry.
 * 
 * @author Petr Kozler
 */
public class CreateGameResponseParser extends AResponseParser {

    /**
     * ID hry
     */
    protected int gameId;
    
    /**
     * Vytvoří parser pro zpracování odpovědi serveru na požadavek na vytvoření hry.
     * 
     * @param client objekt klienta
     * @param playerListPanel panel seznamu hráčů
     * @param gameListPanel panel seznamu her
     * @param statusBarPanel panel stavového řádku
     * @param message zpráva
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    public CreateGameResponseParser(TcpClient client, PlayerListPanel playerListPanel,
            GameListPanel gameListPanel, StatusBarPanel statusBarPanel, TcpMessage message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        super(client, playerListPanel, gameListPanel, statusBarPanel, message);
        
        if (MESSAGE.getNextBoolArg()) {
            CLIENT.joinGame(MESSAGE.getNextIntArg(1));
            gameId = CLIENT.getGameId();
            
            return;
        }
        
        String error = MESSAGE.getNextArg();

        if (!(Protocol.MSG_ERR_INVALID_PLAYER_COUNT.KEYWORD.equals(messageErrorKeyword)
                || Protocol.MSG_ERR_INVALID_BOARD_SIZE.KEYWORD.equals(messageErrorKeyword)
                || Protocol.MSG_ERR_INVALID_CELL_COUNT.KEYWORD.equals(messageErrorKeyword))) {
            messageErrorKeyword = error;
            
            return;
        }

        int minValue = MESSAGE.getNextIntArg(1);
        int maxValue = MESSAGE.getNextIntArg(1);
        
        if (minValue > maxValue) {
            throw new InvalidMessageArgsException();
        }

        messageErrorKeyword = error;
    }

    /**
     * Vrátí výsledek požadavku a obnoví seznam her.
     * 
     * @return výsledek
     */
    @Override
    public String getStatusAndUpdateGUI() {
        if (messageErrorKeyword == null) {
            return String.format("Hráč vytvořil herní místnost ID %d", gameId);
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_INVALID_NAME)) {
            return "Zadaný název herní místnosti je neplatný";
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_EXISTING_NAME)) {
            return "Herní místnost se zadaným názvem již existuje";
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_INVALID_BOARD_SIZE)) {
            return "Zadaný rozměr hracího pole je neplatný";
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_INVALID_PLAYER_COUNT)) {
            return "Zadaný počet hráčů je neplatný";
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_INVALID_CELL_COUNT)) {
            return "Zadaný počet políček potřebných k obsazení je neplatný";
        }
        
        return null;
    }

}
