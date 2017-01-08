package interaction.sending;

import communication.TcpMessage;

/**
 * Abstraktní třída ARequestBuilder představuje požadavek klienta
 * určený k odeslání na server.
 * 
 * @author Petr Kozler
 */
public abstract class ARequestBuilder {
    
    /**
     * zpráva
     */
    protected TcpMessage message;
    
    /**
     * Vrátí požadavek klienta.
     * 
     * @return požadavek klienta
     */
    public TcpMessage getMessage() {
        return message;
    }
    
    /**
     * Vrátí výsledek operace odeslání požadavku.
     * 
     * @return výsledek
     */
    public abstract String getStatus(); 
    
}
