package interaction.receiving;

import communication.ConnectionManager;
import communication.Message;
import communication.tokens.InvalidMessageArgsException;
import communication.tokens.MissingMessageArgsException;

/**
 *
 * @author Petr Kozler
 */
public abstract class AUpdateParser extends AParser {
    
    public AUpdateParser(ConnectionManager connectionManager, Message message) {
        super(connectionManager, message);
    }
    
    public abstract boolean hasNextItemMessage();
    
    public abstract void parseNextItemMessage(Message itemMessage)
            throws InvalidMessageArgsException, MissingMessageArgsException;
    
}
