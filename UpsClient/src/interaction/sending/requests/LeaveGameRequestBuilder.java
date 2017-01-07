package interaction.sending.requests;

import communication.TcpMessage;
import configuration.Protocol;
import interaction.sending.ARequestBuilder;

/**
 * Třída LeaveGameRequestBuilder 
 * 
 * @author Petr Kozler
 */
public class LeaveGameRequestBuilder extends ARequestBuilder {

    /**
     * 
     * 
     */
    public LeaveGameRequestBuilder() {
        message = new TcpMessage(Protocol.MSG_LEAVE_GAME.KEYWORD);
    }
    
    /**
     * 
     * 
     * @return 
     */
    @Override
    public String getStatus() {
        return "Odeslán požadavek na opuštění aktuální herní místnosti";
    }

}
