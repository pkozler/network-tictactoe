package interaction.sending.requests;

import communication.TcpMessage;
import configuration.Protocol;
import interaction.sending.ARequestBuilder;

/**
 * Třída PlayGameRequestBuilder představuje požadavek klienta
 * na provedení tahu hry.
 * 
 * @author Petr Kozler
 */
public class PlayGameRequestBuilder extends ARequestBuilder {

    /**
     * souřadnice X tahu
     */
    private final byte X;
    
    /**
     * souřadnice Y tahu
     */
    private final byte Y;
    
    /**
     * Sestaví požadavek klienta na provedení tahu.
     * 
     * @param x souřadnice X tahu
     * @param y souřadnice Y tahu
     */
    public PlayGameRequestBuilder(byte x, byte y) {
        X = x;
        Y = y;
        
        message = new TcpMessage(Protocol.MSG_PLAY_GAME.KEYWORD,
            Byte.toString(X), Byte.toString(Y));
    }
    
    /**
     * Vrátí výsledek operace odeslání požadavku na provedení tahu.
     * 
     * @return výsledek
     */
    @Override
    public String getStatus() {
        return String.format("Odeslán požadavek na herní tah na souřadnicích [%d; %d]", X, Y);
    }

}
