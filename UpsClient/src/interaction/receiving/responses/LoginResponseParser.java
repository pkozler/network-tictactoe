package interaction.receiving.responses;

import communication.TcpClient;
import communication.TcpMessage;
import communication.tokens.InvalidMessageArgsException;
import communication.tokens.MissingMessageArgsException;
import configuration.Protocol;
import interaction.receiving.AResponseParser;
import visualisation.components.GameListPanel;
import visualisation.components.StatusBarPanel;

/**
 *
 * @author Petr Kozler
 */
public class LoginResponseParser extends AResponseParser {

    protected int id;
    protected String nick;
    
    public LoginResponseParser(TcpClient client,
            GameListPanel gameListPanel, StatusBarPanel statusBarPanel, TcpMessage message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        super(client, gameListPanel, statusBarPanel, message);
        
        if (MESSAGE.getNextBoolArg()) {
            CLIENT.logIn(MESSAGE.getNextIntArg(1));
            id = CLIENT.getPlayerId();
            nick = CLIENT.getPlayerNick();
            
            return;
        }
        
        messageError = Protocol.MSG_LOGIN_CLIENT.getErrorType(MESSAGE.getNextArg());
    }
    
    @Override
    public String getStatusAndUpdateGUI() {
        if (messageError == null) {
            GAME_LIST_PANEL.setButtons(true);
            
            return String.format("Hráč byl přihlášen k serveru pod ID %d s přezdívkou: %s", id, nick);
        }
        
        if (messageError.equals(Protocol.MSG_ERR_INVALID_NAME)) {
            return "Zadaná přezdívka hráče je neplatná";
        }
        
        if (messageError.equals(Protocol.MSG_ERR_EXISTING_NAME)) {
            return "Hráč se zadanou přezdívkou již existuje";
        }
        
        return null;
    }

}
