package interaction.sending.requests;

import communication.Message;
import configuration.Protocol;
import interaction.sending.ARequestBuilder;

/**
 * Třída LoginRequestBuilder představuje požadavek klienta
 * na přihlášení.
 * 
 * @author Petr Kozler
 */
public class LoginRequestBuilder extends ARequestBuilder {
    
    /**
     * přezdívka hráče
     */
    public final String NICKNAME;

    /**
     * Sestaví požadavek klienta na přihlášení.
     * 
     * @param nickname přezdívka hráče
     */
    public LoginRequestBuilder(String nickname) {
        this.NICKNAME = nickname;
        
        message = new Message(Protocol.MSG_LOGIN_CLIENT.KEYWORD, NICKNAME);
    }

    /**
     * Vrátí výsledek operace odeslání požadavku na přihlášení.
     * 
     * @return výsledek
     */
    @Override
    public String getStatus() {
        return String.format("Odeslán požadavek na přihlášení klienta pod přezdívkou: \"%s\"", NICKNAME);
    }

}
