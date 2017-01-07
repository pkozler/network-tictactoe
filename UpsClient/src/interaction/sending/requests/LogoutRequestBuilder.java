package interaction.sending.requests;

import communication.TcpMessage;
import configuration.Protocol;
import interaction.sending.ARequestBuilder;

/**
 * Třída LogoutRequestBuilder 
 * 
 * @author Petr Kozler
 */
public class LogoutRequestBuilder extends ARequestBuilder {

    /**
     * 
     * 
     */
    public LogoutRequestBuilder() {
        message = new TcpMessage(Protocol.MSG_LOGOUT_CLIENT.KEYWORD);
    }
    
    /**
     * 
     * 
     * @return 
     */
    @Override
    public String getStatus() {
        return "Odeslán požadavek na odhlášení klienta";
    }

}
