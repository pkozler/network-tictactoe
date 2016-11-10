package interaction.receiving;

import communication.ConnectionManager;
import communication.Message;
import javax.swing.SwingWorker;

/**
 *
 * @author Petr Kozler
 */
public abstract class AReceiver<T, V> extends SwingWorker<T, V> {
    
    protected final ConnectionManager CONNECTION_MANAGER;
    protected final Message MESSAGE;
    
    public AReceiver(ConnectionManager connectionManager, Message message) {
        CONNECTION_MANAGER = connectionManager;
        MESSAGE = message;
    }
    
}
