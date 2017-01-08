package interaction.sending.requests;

import communication.TcpMessage;
import configuration.Protocol;
import interaction.sending.ARequestBuilder;

/**
 * Třída LogoutRequestBuilder představuje požadavek klienta
 * na odhlášení.
 * 
 * @author Petr Kozler
 */
public class LogoutRequestBuilder extends ARequestBuilder {

    /**
     * Sestaví požadavek klienta na odhlášení.
     */
    public LogoutRequestBuilder() {
        message = new TcpMessage(Protocol.MSG_LOGOUT_CLIENT.KEYWORD);
    }
    
    /**
     * Vrátí výsledek operace odeslání požadavku na odhlášení.
     * 
     * @return výsledek
     */
    @Override
    public String getStatus() {
        return "Odeslán požadavek na odhlášení klienta";
    }

}
