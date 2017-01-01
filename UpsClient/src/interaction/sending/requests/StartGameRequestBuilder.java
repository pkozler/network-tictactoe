package interaction.sending.requests;

import communication.TcpMessage;
import configuration.Protocol;
import interaction.sending.ARequestBuilder;

/**
 *
 * @author Petr Kozler
 */
public class StartGameRequestBuilder extends ARequestBuilder {

    public StartGameRequestBuilder() {
        message = new TcpMessage(Protocol.MSG_LEAVE_GAME.KEYWORD);
    }
    
    @Override
    public String getStatus() {
        return "Odeslán požadavek na zahájení nového kola hry";
    }

}
