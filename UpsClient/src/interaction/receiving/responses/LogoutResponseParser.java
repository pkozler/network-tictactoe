package interaction.receiving.responses;

import communication.TcpClient;
import communication.TcpMessage;
import communication.tokens.InvalidMessageArgsException;
import communication.tokens.MissingMessageArgsException;
import interaction.receiving.AResponseParser;
import visualisation.components.GameListPanel;
import visualisation.components.StatusBarPanel;

/**
 *
 * @author Petr Kozler
 */
public class LogoutResponseParser extends AResponseParser {

    public LogoutResponseParser(TcpClient client,
            GameListPanel gameListPanel, StatusBarPanel statusBarPanel, TcpMessage message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        super(client, gameListPanel, statusBarPanel, message);
        
        if (MESSAGE.getNextBoolArg()) {
            CLIENT.logOut();
        }
    }

    @Override
    public String getStatusAndUpdateGUI() {
        if (messageErrorKeyword != null) {
            return null;
        }
        
        return "Hráč byl odhlášen ze serveru";
    }

}
