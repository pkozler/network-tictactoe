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
 * Třída PlayGameResponseParser představuje parser odpovědi serveru
 * na požadavek na herní tah.
 * 
 * @author Petr Kozler
 */
public class PlayGameResponseParser extends AResponseParser {

    /**
     * Vytvoří parser pro zpracování odpovědi serveru na požadavek na provedení tahu.
     * 
     * @param client objekt klienta
     * @param playerListPanel panel seznamu hráčů
     * @param gameListPanel panel seznamu her
     * @param statusBarPanel panel stavového řádku
     * @param message zpráva
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    public PlayGameResponseParser(TcpClient client, PlayerListPanel playerListPanel,
            GameListPanel gameListPanel, StatusBarPanel statusBarPanel, TcpMessage message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        super(client, playerListPanel, gameListPanel, statusBarPanel, message);
        
        if (MESSAGE.getNextBoolArg()) {
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
    public String updateClient() {
        if (messageErrorKeyword == null) {
            return String.format("Hráč provedl herní tah");
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_NOT_IN_ROOM.KEYWORD)) {
            return "Hráč se nenachází v herní místnosti se zadaným ID";
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_ROUND_NOT_STARTED.KEYWORD)) {
            return "Hráč nemůže táhnout před zahájením kola";
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_CANNOT_PLAY_IN_ROUND.KEYWORD)) {
            return "Hráč již nemůže táhnout v tomto kole";
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_CANNOT_PLAY_NOW.KEYWORD)) {
            return "Hráč není na tahu";
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_INVALID_POSITION.KEYWORD)) {
            return "Hráč provedl tah na neplatnou pozici";
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_CELL_OUT_OF_BOARD.KEYWORD)) {
            return "Hráč provedl tah na pozici mimo hranice hracího pole";
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_CELL_OCCUPIED.KEYWORD)) {
            return "Hráč se pokusil táhnout na již obsazené políčko";
        }
        
        return null;
    }

}
