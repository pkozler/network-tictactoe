package interaction.sending.requests;

import communication.ConnectionManager;
import communication.Message;
import configuration.Config;
import interaction.sending.ASender;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author Petr Kozler
 */
public class LeaveGameRequestSender extends ASender {

    public LeaveGameRequestSender(ConnectionManager connectionManager) {
        super(connectionManager);
    }
    
    @Override
    protected Object doInBackground() throws Exception {
        Message message = new Message(Config.MSG_LEAVE_GAME.KEYWORD);
        CONNECTION_MANAGER.sendMessage(message);
        
        return null;
    }

    @Override
    protected void done() {
        try {
            get();
            
            // OK - TODO zobrazit v GUI
        }
        catch (InterruptedException | ExecutionException ex) {
            // CHYBA - TODO zobrazit v GUI
        }
    }
    
}
