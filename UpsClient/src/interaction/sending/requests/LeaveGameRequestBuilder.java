package interaction.sending.requests;

import communication.Message;
import configuration.Config;
import interaction.sending.ARequestBuilder;

/**
 *
 * @author Petr Kozler
 */
public class LeaveGameRequestBuilder extends ARequestBuilder {

    public LeaveGameRequestBuilder() {
        message = new Message(Config.MSG_LEAVE_GAME.KEYWORD);
    }
    
    @Override
    public String getStatus() {
        return "Odeslán požadavek na opuštění aktuální herní místnosti";
    }

}
