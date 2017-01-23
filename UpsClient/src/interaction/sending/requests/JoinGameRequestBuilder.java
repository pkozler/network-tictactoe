package interaction.sending.requests;

import communication.Message;
import configuration.Protocol;
import interaction.sending.ARequestBuilder;

/**
 * Třída JoinGameRequestBuilder představuje požadavek klienta
 * na připojení ke hře.
 * 
 * @author Petr Kozler
 */
public class JoinGameRequestBuilder extends ARequestBuilder {

    /**
     * ID hry
     */
    public final int GAME_ID;
    
    /**
     * Sestaví požadavek klienta na připojení ke hře.
     * 
     * @param gameId ID hry
     */
    public JoinGameRequestBuilder(int gameId) {
        GAME_ID = gameId;
        
        message = new Message(Protocol.MSG_JOIN_GAME.KEYWORD, Integer.toString(GAME_ID));
    }
    
    /**
     * Vrátí výsledek operace odeslání požadavku na připojení ke hře.
     * 
     * @return výsledek
     */
    @Override
    public String getStatus() {
        return String.format("Odeslán požadavek na vstup do herní místnosti s ID: %d", GAME_ID);
    }

}
