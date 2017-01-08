package interaction.sending.requests;

import communication.TcpMessage;
import configuration.Protocol;
import interaction.sending.ARequestBuilder;

/**
 * Třída LeaveGameRequestBuilder představuje požadavek klienta
 * na opuštění hry.
 * 
 * @author Petr Kozler
 */
public class LeaveGameRequestBuilder extends ARequestBuilder {

    /**
     * Sestaví požadavek klienta na opuštění hry.
     */
    public LeaveGameRequestBuilder() {
        message = new TcpMessage(Protocol.MSG_LEAVE_GAME.KEYWORD);
    }
    
    /**
     * Vrátí výsledek operace odeslání požadavku na opuštění hry.
     * 
     * @return výsledek
     */
    @Override
    public String getStatus() {
        return "Odeslán požadavek na opuštění aktuální herní místnosti";
    }

}
