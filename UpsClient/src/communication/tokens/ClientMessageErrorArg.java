package communication.tokens;

/**
 * Třída ClientMessageErrorArg 
 * 
 * @author Petr Kozler
 */
public class ClientMessageErrorArg extends AMessageStringToken {
    
    /**
     * 
     * 
     * @param keyword
     * @param argCount 
     */
    public ClientMessageErrorArg(String keyword, int argCount) {
        super(keyword, argCount);
    }
    
    /**
     * 
     * 
     * @param keyword 
     */
    public ClientMessageErrorArg(String keyword) {
        this(keyword, 0);
    }
    
}
