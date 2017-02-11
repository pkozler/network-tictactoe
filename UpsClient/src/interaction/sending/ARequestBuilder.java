package interaction.sending;

import communication.Message;

/**
 * Abstraktní třída ARequestBuilder představuje požadavek klienta
 * určený k odeslání na server.
 * 
 * @author Petr Kozler
 */
public abstract class ARequestBuilder {
    
    /**
     * požadavek klienta
     */
    protected Message message;
    
    /**
     * Vrátí požadavek klienta.
     * 
     * @return požadavek klienta
     */
    public Message getMessage() {
        return message;
    }
    
    /**
     * Vrátí výsledek operace odeslání požadavku.
     * 
     * @return výsledek
     */
    public abstract String getStatus(); 
    
}
