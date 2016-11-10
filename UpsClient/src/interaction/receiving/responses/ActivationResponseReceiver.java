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
public class ActivationResponseReceiver extends AReceiver<MessageErrorArg, Object> {

    public ActivationResponseReceiver(ConnectionManager connectionManager, Message message) {
        super(connectionManager, message);
    }

    @Override
    protected MessageErrorArg doInBackground() throws Exception {
        boolean accepted = MESSAGE.getNextBoolArg();
        
        if (accepted) {
            CONNECTION_MANAGER.activate(MESSAGE.getNextIntArg(1));
            
            return null;
        }
        
        return Config.MSG_ACTIVATE_CLIENT.getErrorType(MESSAGE.getNextArg());
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
