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
public class JoinGameRequestSender extends ASender {

    private final int GAME_ID;
    
    public JoinGameRequestSender(ConnectionManager connectionManager, int gameId) {
        super(connectionManager);
        GAME_ID = gameId;
    }
    
    @Override
    protected Object doInBackground() throws Exception {
        Message message = new Message(Config.MSG_JOIN_GAME.KEYWORD, Integer.toString(GAME_ID));
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
