package interaction.receiving.responses;

import communication.ConnectionManager;
import communication.Message;
import communication.tokens.InvalidMessageArgsException;
import communication.tokens.MessageErrorArg;
import configuration.Config;
import interaction.receiving.AReceiver;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author Petr Kozler
 */
public class CreateGameResponseReceiver extends AReceiver<MessageErrorArg, Object> {

    public CreateGameResponseReceiver(ConnectionManager connectionManager, Message message) {
        super(connectionManager, message);
    }

    @Override
    protected MessageErrorArg doInBackground() throws Exception {
        boolean accepted = MESSAGE.getNextBoolArg();
        
        if (accepted) {
            int gameId = MESSAGE.getNextIntArg(1);
            // TODO zaznamenat přijaté ID hry a připojit se do hry
            
            return null;
        }
        
        MessageErrorArg errorType = Config.MSG_CREATE_GAME.getErrorType(MESSAGE.getNextArg());

        if (!(Config.MSG_ERR_INVALID_PLAYER_COUNT.equals(errorType)
                || Config.MSG_ERR_INVALID_BOARD_SIZE.equals(errorType)
                || Config.MSG_ERR_INVALID_CELL_COUNT.equals(errorType))) {
            return errorType;
        }

        int minValue = MESSAGE.getNextIntArg(1);
        int maxValue = MESSAGE.getNextIntArg(1);
        
        if (minValue > maxValue) {
            throw new InvalidMessageArgsException();
        }

        return errorType;
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
