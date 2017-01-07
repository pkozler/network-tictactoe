package communication.tokens;

/**
 * Třída ServerMessageType 
 * 
 * @author Petr Kozler
 */
public class ServerMessageType extends AMessageStringToken {
    
    /**
     * 
     */
    public final boolean IS_LIST;
    
    /**
     * 
     * 
     * @param keyword
     * @param argCount
     * @param isList 
     */
    public ServerMessageType(String keyword, int argCount, boolean isList) {
        super(keyword, argCount);
        IS_LIST = isList;
    }
    
    /**
     * 
     * 
     * @param keyword
     * @param argCount 
     */
    public ServerMessageType(String keyword, int argCount) {
        this(keyword, argCount, false);
    }

    /**
     * 
     * 
     * @param keyword
     * @param isList 
     */
    public ServerMessageType(String keyword, boolean isList) {
        this(keyword, 1, isList);
    }

}
