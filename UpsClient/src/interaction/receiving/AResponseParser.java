package interaction.receiving;

import communication.ConnectionManager;
import communication.Message;
import communication.tokens.ClientMessageErrorArg;
import visualisation.components.GameListPanel;
import visualisation.components.StatusBarPanel;

/**
 *
 * @author Petr Kozler
 */
public abstract class AResponseParser extends AParser {
    
    protected final GameListPanel GAME_LIST_PANEL;
    
    protected ClientMessageErrorArg messageError;
    
    public AResponseParser(ConnectionManager connectionManager,
            GameListPanel gameListPanel, StatusBarPanel statusBarPanel, Message message) {
        super(connectionManager, message);
        GAME_LIST_PANEL = gameListPanel;
        messageError = null;
    }
    
}
