package interaction.receiving;

import communication.ConnectionManager;
import communication.Message;

/**
 *
 * @author Petr Kozler
 */
public abstract class AParser {
    
    protected final ConnectionManager CONNECTION_MANAGER;
    protected final Message MESSAGE;
    
    public AParser(ConnectionManager connectionManager, Message message) {
        CONNECTION_MANAGER = connectionManager;
        MESSAGE = message;
    }
    
    public abstract String getStatusAndUpdateGUI();
    
}
