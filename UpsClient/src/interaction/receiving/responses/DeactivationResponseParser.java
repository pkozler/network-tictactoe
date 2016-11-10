package interaction.receiving.responses;

import communication.ConnectionManager;
import communication.Message;
import communication.tokens.InvalidMessageArgsException;
import communication.tokens.MissingMessageArgsException;
import interaction.receiving.AResponseParser;
import visualisation.components.GameListPanel;
import visualisation.components.StatusBarPanel;

/**
 *
 * @author Petr Kozler
 */
public class DeactivationResponseParser extends AResponseParser {

    public DeactivationResponseParser(ConnectionManager connectionManager,
            GameListPanel gameListPanel, StatusBarPanel statusBarPanel, Message message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        super(connectionManager, gameListPanel, statusBarPanel, message);
        
        if (MESSAGE.getNextBoolArg()) {
            CONNECTION_MANAGER.logOut();
        }
    }

    @Override
    public String getStatusAndUpdateGUI() {
        if (messageError != null) {
            return null;
        }
        
        return "Hráč byl odhlášen ze serveru";
    }

}
