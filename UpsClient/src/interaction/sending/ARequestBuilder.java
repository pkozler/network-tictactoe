package interaction.sending;

import communication.Message;

/**
 *
 * @author Petr Kozler
 */
public abstract class ARequestBuilder {
    
    protected Message message;
    
    public Message getMessage() {
        return message;
    }
    
    public abstract String getStatus(); 
    
}
