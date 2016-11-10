package interaction.receiving.responses;

import communication.ConnectionManager;
import communication.Message;
import communication.tokens.MessageErrorArg;
import configuration.Config;
import interaction.receiving.AReceiver;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author Petr Kozler
 */
public class JoinGameResponseReceiver extends AReceiver<MessageErrorArg, Object> {

    public JoinGameResponseReceiver(ConnectionManager connectionManager, Message message) {
        super(connectionManager, message);
    }

    @Override
    protected MessageErrorArg doInBackground() throws Exception {
        boolean accepted = MESSAGE.getNextBoolArg();
        
        if (accepted) {
            // TODO připojit se do hry (začít přijímat status hry a odesílat tahy)
            
            return null;
        }
        
        return Config.MSG_JOIN_GAME.getErrorType(MESSAGE.getNextArg());
    }

    @Override
    protected void done() {
        try {
            MessageErrorArg messageError = get();
            
            if (messageError == null) {
                // OK
                return;
            }
            
            // CHYBA
        }
        catch (InterruptedException | ExecutionException ex) {
            // TODO zobrazit v GUI
        }
    }
    
}
