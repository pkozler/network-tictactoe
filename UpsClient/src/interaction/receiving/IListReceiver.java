package interaction.receiving;

import communication.Message;
import communication.tokens.InvalidMessageArgsException;
import communication.tokens.MissingMessageArgsException;

/**
 *
 * @author Petr Kozler
 */
public interface IListReceiver {
    
    public boolean hasNextItem();
    
    public void addNextItem(Message itemMessage) throws InvalidMessageArgsException, MissingMessageArgsException;
    
}
