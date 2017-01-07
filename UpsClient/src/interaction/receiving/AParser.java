package interaction.receiving;

import communication.TcpClient;
import communication.TcpMessage;

/**
 * Abstraktní třída AParser 
 * 
 * @author Petr Kozler
 */
public abstract class AParser {
    
    /**
     * 
     */
    protected final TcpClient CLIENT;
    
    /**
     * 
     */
    protected final TcpMessage MESSAGE;
    
    /**
     * 
     * 
     * @param client
     * @param message 
     */
    public AParser(TcpClient client, TcpMessage message) {
        CLIENT = client;
        MESSAGE = message;
    }
    
    /**
     * 
     * @return 
     */
    public abstract String getStatusAndUpdateGUI();
    
}
