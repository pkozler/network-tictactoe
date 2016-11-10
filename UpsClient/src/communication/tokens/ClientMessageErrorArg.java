package communication.tokens;

/**
 *
 * @author Petr Kozler
 */
public class ClientMessageErrorArg extends AMessageStringToken {
    
    public ClientMessageErrorArg(String keyword, int argCount) {
        super(keyword, argCount);
    }
    
    public ClientMessageErrorArg(String keyword) {
        super(keyword);
    }
    
}
