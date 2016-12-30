package interaction.receiving;

import communication.TcpClient;
import communication.TcpMessage;
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
    
    public AResponseParser(TcpClient client,
            GameListPanel gameListPanel, StatusBarPanel statusBarPanel, TcpMessage message) {
        super(client, message);
        GAME_LIST_PANEL = gameListPanel;
        messageError = null;
    }
    
}