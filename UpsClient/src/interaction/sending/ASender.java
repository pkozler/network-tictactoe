package interaction.sending;

import communication.ConnectionManager;
import javax.swing.SwingWorker;

/**
 *
 * @author Petr Kozler
 */
public abstract class ASender<T, V> extends SwingWorker<T, V> {
    
    protected final ConnectionManager CONNECTION_MANAGER;
    
    public ASender(ConnectionManager connectionManager) {
        CONNECTION_MANAGER = connectionManager;
    }
    
}
