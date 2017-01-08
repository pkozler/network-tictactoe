package interaction.sending.requests;

import communication.TcpMessage;
import configuration.Protocol;
import interaction.sending.ARequestBuilder;

/**
 * Třída StartGameRequestBuilder představuje požadavek klienta
 * na zahájení nového kola hry.
 * 
 * @author Petr Kozler
 */
public class StartGameRequestBuilder extends ARequestBuilder {

    /**
     * Sestaví požadavek klienta na zahájení herního kola.
     */
    public StartGameRequestBuilder() {
        message = new TcpMessage(Protocol.MSG_LEAVE_GAME.KEYWORD);
    }
    
    /**
     * Vrátí výsledek operace odeslání požadavku na zahájení nového kola hry.
     * 
     * @return výsledek
     */
    @Override
    public String getStatus() {
        return "Odeslán požadavek na zahájení nového kola hry";
    }

}
