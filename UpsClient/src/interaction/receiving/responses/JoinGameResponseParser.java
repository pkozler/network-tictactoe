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
 * Třída JoinGameResponseParser představuje parser odpovědi serveru
 * na požadavek na připojení se ke hře.
 * 
 * @author Petr Kozler
 */
public class JoinGameResponseParser extends AResponseParser {

    /**
     * ID hry
     */
    protected int gameId;
    
    /**
     * Vytvoří parser pro zpracování odpovědi serveru na požadavek na připojení se ke hře.
     * 
     * @param client objekt klienta
     * @param playerListPanel panel seznamu hráčů
     * @param gameListPanel panel seznamu her
     * @param statusBarPanel panel stavového řádku
     * @param message zpráva
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    public JoinGameResponseParser(TcpClient client, PlayerListPanel playerListPanel,
            GameListPanel gameListPanel, StatusBarPanel statusBarPanel, TcpMessage message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        super(client, playerListPanel, gameListPanel, statusBarPanel, message);
        
        if (MESSAGE.getNextBoolArg()) {
            CLIENT.joinGame(MESSAGE.getNextIntArg(1));
            gameId = CLIENT.getGameId();
            
            return;
        }
        
        messageErrorKeyword = MESSAGE.getNextArg();
    }

    /**
     * Vrátí výsledek požadavku a obnoví seznam her.
     * 
     * @return výsledek
     */
    @Override
    public String getStatusAndUpdateGUI() {
        if (messageErrorKeyword == null) {
            return String.format("Hráč vstoupil do herní místnosti s ID %d", gameId);
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_INVALID_ID)) {
            return "Zadané ID herní místnosti je neplatné";
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_ID_NOT_FOUND)) {
            return "Herní místnost se zadaným ID neexistuje";
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_ALREADY_IN_ROOM)) {
            return "Hráč se již nachází v herní místnosti se zadaným ID";
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_ROOM_FULL)) {
            return "Herní místnost je již plně obsazena";
        }
        
        return null;
    }

}
