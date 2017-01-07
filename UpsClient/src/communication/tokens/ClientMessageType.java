package communication.tokens;

/**
 * Třída ClientMessageType 
 * 
 * @author Petr Kozler
 */
public class ClientMessageType extends AMessageStringToken {
    
    /**
     * 
     */
    public final int RESPONSE_ARG_COUNT;
    
    /**
     * 
     * 
     * @param keyword
     * @param argCount
     * @param responseArgCount 
     */
    public ClientMessageType(String keyword, int argCount, int responseArgCount) {
        super(keyword, argCount);
        RESPONSE_ARG_COUNT = responseArgCount;
    }
    
    /**
     * 
     * 
     * @param keyword
     * @param argCount 
     */
    public ClientMessageType(String keyword, int argCount) {
        this(keyword, argCount, 1);
    }
    
    /**
     * 
     * 
     * @param keyword 
     */
    public ClientMessageType(String keyword) {
        this(keyword, 0);
    }
    
}
