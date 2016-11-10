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
public class CreateGameRequestSender extends ASender {

    private final String NAME;
    private final byte PLAYER_COUNT;
    private final byte BOARD_SIZE;
    private final byte CELL_COUNT;
    
    public CreateGameRequestSender(ConnectionManager connectionManager, String name,
            byte playerCount, byte boardSize, byte cellCount) {
        super(connectionManager);
        NAME = name;
        PLAYER_COUNT = playerCount;
        BOARD_SIZE = boardSize;
        CELL_COUNT = cellCount;
    }
    
    @Override
    protected Object doInBackground() throws Exception {
        Message message = new Message(Config.MSG_CREATE_GAME.KEYWORD, NAME,
                Byte.toString(PLAYER_COUNT), Byte.toString(BOARD_SIZE), Byte.toString(CELL_COUNT));
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
