package interaction.sending.requests;

import communication.TcpMessage;
import configuration.Protocol;
import interaction.sending.ARequestBuilder;

/**
 *
 * @author Petr Kozler
 */
public class LogoutRequestBuilder extends ARequestBuilder {

    public LogoutRequestBuilder() {
        message = new TcpMessage(Protocol.MSG_LOGOUT_CLIENT.KEYWORD);
    }
    
    @Override
    public String getStatus() {
        return "Odeslán požadavek na odhlášení klienta";
    }

}
