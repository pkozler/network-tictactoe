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
public class PlayGameRequestSender extends ASender {

    private final byte X;
    private final byte Y;
    
    public PlayGameRequestSender(ConnectionManager connectionManager, byte x, byte y) {
        super(connectionManager);
        X = x;
        Y = y;
    }
    
    @Override
    protected Object doInBackground() throws Exception {
        Message message = new Message(Config.MSG_PLAY_GAME.KEYWORD,
            Byte.toString(X), Byte.toString(Y));
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
