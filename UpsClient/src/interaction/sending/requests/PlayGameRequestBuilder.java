package interaction.sending.requests;

import communication.Message;
import configuration.Config;
import interaction.sending.ARequestBuilder;

/**
 *
 * @author Petr Kozler
 */
public class PlayGameRequestBuilder extends ARequestBuilder {

    private final byte X;
    private final byte Y;
    
    public PlayGameRequestBuilder(byte x, byte y) {
        X = x;
        Y = y;
        
        message = new Message(Config.MSG_PLAY_GAME.KEYWORD,
            Byte.toString(X), Byte.toString(Y));
    }
    
    @Override
    public String getStatus() {
        return String.format("Odeslán požadavek na herní tah na souřadnicích [%d; %d]", X, Y);
    }

}
