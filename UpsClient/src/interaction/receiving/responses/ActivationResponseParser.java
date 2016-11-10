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
public class ActivationResponseParser extends AResponseParser {

    protected int id;
    protected String nick;
    
    public ActivationResponseParser(ConnectionManager connectionManager,
            GameListPanel gameListPanel, StatusBarPanel statusBarPanel, Message message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        super(connectionManager, gameListPanel, statusBarPanel, message);
        
        if (MESSAGE.getNextBoolArg()) {
            CONNECTION_MANAGER.logIn(MESSAGE.getNextIntArg(1));
            id = CONNECTION_MANAGER.getPlayerId();
            nick = CONNECTION_MANAGER.getPlayerNick();
            
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
