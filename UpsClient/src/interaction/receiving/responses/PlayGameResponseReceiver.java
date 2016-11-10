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
public class PlayGameResponseReceiver extends AReceiver<MessageErrorArg, Object> {

    public PlayGameResponseReceiver(ConnectionManager connectionManager, Message message) {
        super(connectionManager, message);
    }

    @Override
    protected MessageErrorArg doInBackground() throws Exception {
        boolean accepted = MESSAGE.getNextBoolArg();
        
        if (accepted) {
            return null;
        }
        
        return Config.MSG_PLAY_GAME.getErrorType(MESSAGE.getNextArg());
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
