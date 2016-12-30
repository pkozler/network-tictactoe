package interaction.sending.requests;

import communication.TcpMessage;
import configuration.Config;
import interaction.sending.ARequestBuilder;

/**
 *
 * @author Petr Kozler
 */
public class DeactivationRequestBuilder extends ARequestBuilder {

    public DeactivationRequestBuilder() {
        message = new TcpMessage(Config.MSG_DEACTIVATE_CLIENT.KEYWORD);
    }
    
    @Override
    public String getStatus() {
        return "Odeslán požadavek na odhlášení klienta";
    }

}