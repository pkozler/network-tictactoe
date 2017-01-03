package communication.tokens;

/**
 *
 * @author Petr Kozler
 */
public class ServerMessageType extends AMessageStringToken {
    
    public final boolean IS_LIST;
    
    public ServerMessageType(String keyword, int argCount, boolean isList) {
        super(keyword, argCount);
        IS_LIST = isList;
    }
    
    public ServerMessageType(String keyword, int argCount) {
        this(keyword, argCount, false);
    }

    public ServerMessageType(String keyword, boolean isList) {
        this(keyword, 1, isList);
    }

}
