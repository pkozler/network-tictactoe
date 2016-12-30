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
public class ActivationResponseParser extends AResponseParser {

    protected int id;
    protected String nick;
    
    public ActivationResponseParser(TcpClient client,
            GameListPanel gameListPanel, StatusBarPanel statusBarPanel, TcpMessage message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        super(client, gameListPanel, statusBarPanel, message);
        
        if (MESSAGE.getNextBoolArg()) {
            CLIENT.logIn(MESSAGE.getNextIntArg(1));
            id = CLIENT.getPlayerId();
            nick = CLIENT.getPlayerNick();
            
            return;
        }
        
        messageError = Config.MSG_ACTIVATE_CLIENT.getErrorType(MESSAGE.getNextArg());
    }
    
    @Override
    public String getStatusAndUpdateGUI() {
        if (messageError == null) {
            GAME_LIST_PANEL.setButtons(true);
            
            return String.format("Hráč byl přihlášen k serveru pod ID %d s přezdívkou: %s", id, nick);
        }
        
        if (messageError.equals(Config.MSG_ERR_INVALID_NAME)) {
            return "Zadaná přezdívka hráče je neplatná";
        }
        
        if (messageError.equals(Config.MSG_ERR_EXISTING_NAME)) {
            return "Hráč se zadanou přezdívkou již existuje";
        }
        
        return null;
    }

}
