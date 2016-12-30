package interaction.receiving;

import communication.TcpClient;
import communication.TcpMessage;

/**
 *
 * @author Petr Kozler
 */
public abstract class AParser {
    
    protected final TcpClient CLIENT;
    protected final TcpMessage MESSAGE;
    
    public AParser(TcpClient client, TcpMessage message) {
        CLIENT = client;
        MESSAGE = message;
    }
    
    public abstract String getStatusAndUpdateGUI();
    
}
