package interaction.receiving.responses;

import communication.ConnectionManager;
import communication.Message;
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
public class JoinGameResponseParser extends AResponseParser {

    protected int gameId;
    
    public JoinGameResponseParser(ConnectionManager connectionManager,
            GameListPanel gameListPanel, StatusBarPanel statusBarPanel, Message message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        super(connectionManager, gameListPanel, statusBarPanel, message);
        
        if (MESSAGE.getNextBoolArg()) {
            CONNECTION_MANAGER.joinGame(MESSAGE.getNextIntArg(1));
            gameId = CONNECTION_MANAGER.getGameId();
            
            return;
        }
        
        messageError = Config.MSG_JOIN_GAME.getErrorType(MESSAGE.getNextArg());
    }

    @Override
    public String getStatusAndUpdateGUI() {
        if (messageError == null) {
            return String.format("Hráč vstoupil do herní místnosti s ID %d", gameId);
        }
        
        if (messageError.equals(Config.MSG_ERR_INVALID_ID)) {
            return "Zadané ID herní místnosti je neplatné";
        }
        
        if (messageError.equals(Config.MSG_ERR_ID_NOT_FOUND)) {
            return "Herní místnost se zadaným ID neexistuje";
        }
        
        if (messageError.equals(Config.MSG_ERR_ALREADY_IN_ROOM)) {
            return "Hráč se již nachází v herní místnosti se zadaným ID";
        }
        
        if (messageError.equals(Config.MSG_ERR_ROOM_FULL)) {
            return "Herní místnost je již plně obsazena";
        }
        
        return null;
    }

}
