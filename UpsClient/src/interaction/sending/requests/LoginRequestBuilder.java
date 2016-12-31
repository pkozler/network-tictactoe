package interaction.sending.requests;

import communication.TcpMessage;
import configuration.Protocol;
import interaction.sending.ARequestBuilder;

/**
 *
 * @author Petr Kozler
 */
public class LoginRequestBuilder extends ARequestBuilder {
    
    private final String NICKNAME;

    public LoginRequestBuilder(String nickname) {
        this.NICKNAME = nickname;
        
        message = new TcpMessage(Protocol.MSG_LOGIN_CLIENT.KEYWORD, NICKNAME);
    }

    @Override
    public String getStatus() {
        return String.format("Odeslán požadavek na přihlášení klienta pod přezdívkou %s", NICKNAME);
    }

}
