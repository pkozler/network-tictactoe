package interaction.sending.requests;

import communication.Message;
import configuration.Config;
import interaction.sending.ARequestBuilder;

/**
 *
 * @author Petr Kozler
 */
public class ActivationRequestBuilder extends ARequestBuilder {
    
    private final String NICKNAME;

    public ActivationRequestBuilder(String nickname) {
        this.NICKNAME = nickname;
        
        message = new Message(Config.MSG_ACTIVATE_CLIENT.KEYWORD, NICKNAME);
    }

    @Override
    public String getStatus() {
        return String.format("Odeslán požadavek na přihlášení klienta pod přezdívkou %s", NICKNAME);
    }

}
