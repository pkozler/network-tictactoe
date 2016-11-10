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
public class LeaveGameResponseReceiver extends AReceiver<MessageErrorArg, Object> {

    public LeaveGameResponseReceiver(ConnectionManager connectionManager, Message message) {
        super(connectionManager, message);
    }

    @Override
    protected MessageErrorArg doInBackground() throws Exception {
        boolean accepted = MESSAGE.getNextBoolArg();
        
        if (accepted) {
            // TODO opustit hru (přestat přijímat status hry a odesílat tahy)
            
            return null;
        }
        
        return Config.MSG_LEAVE_GAME.getErrorType(MESSAGE.getNextArg());
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
