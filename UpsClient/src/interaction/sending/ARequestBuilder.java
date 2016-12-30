package interaction.sending;

import communication.TcpMessage;

/**
 *
 * @author Petr Kozler
 */
public abstract class ARequestBuilder {
    
    protected TcpMessage message;
    
    public TcpMessage getMessage() {
        return message;
    }
    
    public abstract String getStatus(); 
    
}
