package interaction.sending;

import communication.TcpMessage;

/**
 * Abstraktní třída ARequestBuilder 
 * 
 * @author Petr Kozler
 */
public abstract class ARequestBuilder {
    
    /**
     * 
     */
    protected TcpMessage message;
    
    /**
     * 
     * 
     * @return 
     */
    public TcpMessage getMessage() {
        return message;
    }
    
    /**
     * 
     * 
     * @return 
     */
    public abstract String getStatus(); 
    
}
