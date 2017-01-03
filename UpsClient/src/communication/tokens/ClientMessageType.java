package communication.tokens;

/**
 *
 * @author Petr Kozler
 */
public class ClientMessageType extends AMessageStringToken {
    
    public final int RESPONSE_ARG_COUNT;
    
    public ClientMessageType(String keyword, int argCount, int responseArgCount) {
        super(keyword, argCount);
        RESPONSE_ARG_COUNT = responseArgCount;
    }
    
    public ClientMessageType(String keyword, int argCount) {
        this(keyword, argCount, 1);
    }
    
    public ClientMessageType(String keyword) {
        this(keyword, 0);
    }
    
}
