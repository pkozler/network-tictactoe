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
public class LeaveGameResponseParser extends AResponseParser {

    protected int gameId;
    
    public LeaveGameResponseParser(ConnectionManager connectionManager,
            GameListPanel gameListPanel, StatusBarPanel statusBarPanel, Message message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        super(connectionManager, gameListPanel, statusBarPanel, message);
        
        if (MESSAGE.getNextBoolArg()) {
            gameId = CONNECTION_MANAGER.getGameId();
            CONNECTION_MANAGER.leaveGame();

            return;
        }
        
        messageError = Config.MSG_LEAVE_GAME.getErrorType(MESSAGE.getNextArg());
    }

    @Override
    public String getStatusAndUpdateGUI() {
        if (messageError == null) {
            return String.format("Hráč opustil herní místnost s ID %d", gameId);
        }
        
        if (messageError.equals(Config.MSG_ERR_NOT_IN_ROOM)) {
            return "Hráč se nenachází v herní místnosti se zadaným ID";
        }
        
        return null;
    }

}
