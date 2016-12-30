package interaction.receiving.responses;

import communication.TcpClient;
import communication.TcpMessage;
import communication.tokens.InvalidMessageArgsException;
import communication.tokens.MissingMessageArgsException;
import configuration.Config;
import interaction.receiving.AResponseParser;
import visualisation.components.GameListPanel;
import visualisation.components.StatusBarPanel;

/**
 *
 * @author Petr Kozler
 */
public class PlayGameResponseParser extends AResponseParser {

    protected int gameId;
    
    public PlayGameResponseParser(TcpClient client,
            GameListPanel gameListPanel, StatusBarPanel statusBarPanel, TcpMessage message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        super(client, gameListPanel, statusBarPanel, message);
        
        if (MESSAGE.getNextBoolArg()) {
            gameId = CLIENT.getGameId();
            
            return;
        }
        
        messageError = Config.MSG_PLAY_GAME.getErrorType(MESSAGE.getNextArg());
    }

    @Override
    public String getStatusAndUpdateGUI() {
        if (messageError == null) {
            
            return String.format("Hráč odehrál tah v herní místnosti s ID %d", gameId);
        }
        
        if (messageError.equals(Config.MSG_ERR_NOT_IN_ROOM)) {
            return "Hráč se nenachází v herní místnosti se zadaným ID";
        }
        
        if (messageError.equals(Config.MSG_ERR_ROUND_NOT_STARTED)) {
            return "Hráč nemůže táhnout před zahájením kola";
        }
        
        if (messageError.equals(Config.MSG_ERR_CANNOT_PLAY_IN_ROUND)) {
            return "Hráč již nemůže táhnout v tomto kole";
        }
        
        if (messageError.equals(Config.MSG_ERR_CANNOT_PLAY_NOW)) {
            return "Hráč není na tahu";
        }
        
        if (messageError.equals(Config.MSG_ERR_INVALID_POSITION)) {
            return "Hráč provedl tah na neplatnou pozici";
        }
        
        if (messageError.equals(Config.MSG_ERR_CELL_OUT_OF_BOARD)) {
            return "Hráč provedl tah na pozici mimo hranice hracího pole";
        }
        
        if (messageError.equals(Config.MSG_ERR_CELL_OCCUPIED)) {
            return "Hráč se pokusil táhnout na již obsazené políčko";
        }
        
        return null;
    }

}
