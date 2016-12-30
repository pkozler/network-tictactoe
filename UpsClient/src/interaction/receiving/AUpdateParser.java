package interaction.receiving;

import communication.TcpClient;
import communication.TcpMessage;
import communication.tokens.InvalidMessageArgsException;
import communication.tokens.MissingMessageArgsException;

/**
 *
 * @author Petr Kozler
 */
public abstract class AUpdateParser extends AParser {
    
    public AUpdateParser(TcpClient client, TcpMessage message) {
        super(client, message);
    }
    
    public abstract boolean hasNextItemMessage();
    
    public abstract void parseNextItemMessage(TcpMessage itemMessage)
            throws InvalidMessageArgsException, MissingMessageArgsException;
    
}
