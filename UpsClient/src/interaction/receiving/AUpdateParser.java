package interaction.receiving;

import communication.TcpClient;
import communication.TcpMessage;
import communication.tokens.InvalidMessageArgsException;
import communication.tokens.MissingMessageArgsException;

/**
 * Abstraktní třída AUpdateParser 
 * 
 * @author Petr Kozler
 */
public abstract class AUpdateParser extends AParser {
    
    /**
     * 
     * 
     * @param client
     * @param message 
     */
    public AUpdateParser(TcpClient client, TcpMessage message) {
        super(client, message);
    }
    
    /**
     * 
     * 
     * @return 
     */
    public abstract boolean hasNextItemMessage();
    
    /**
     * 
     * 
     * @param itemMessage
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    public abstract void parseNextItemMessage(TcpMessage itemMessage)
            throws InvalidMessageArgsException, MissingMessageArgsException;
    
}
