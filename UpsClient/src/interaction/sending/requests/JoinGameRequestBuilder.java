package interaction.sending.requests;

import communication.TcpMessage;
import configuration.Protocol;
import interaction.sending.ARequestBuilder;

/**
 *
 * @author Petr Kozler
 */
public class JoinGameRequestBuilder extends ARequestBuilder {

    private final int GAME_ID;
    
    public JoinGameRequestBuilder(int gameId) {
        GAME_ID = gameId;
        
        message = new TcpMessage(Protocol.MSG_JOIN_GAME.KEYWORD, Integer.toString(GAME_ID));
    }
    
    @Override
    public String getStatus() {
        return String.format("Odeslán požadavek na vstup do herní místnosti s ID %d", GAME_ID);
    }

}
