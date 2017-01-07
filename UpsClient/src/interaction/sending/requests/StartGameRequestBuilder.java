package interaction.sending.requests;

import communication.TcpMessage;
import configuration.Protocol;
import interaction.sending.ARequestBuilder;

/**
 * Třída StartGameRequestBuilder 
 * 
 * @author Petr Kozler
 */
public class StartGameRequestBuilder extends ARequestBuilder {

    /**
     * 
     * 
     */
    public StartGameRequestBuilder() {
        message = new TcpMessage(Protocol.MSG_LEAVE_GAME.KEYWORD);
    }
    
    /**
     * 
     * 
     * @return 
     */
    @Override
    public String getStatus() {
        return "Odeslán požadavek na zahájení nového kola hry";
    }

}
