package communication.tokens;

/**
 *
 * @author Petr Kozler
 */
public abstract class AMessageType extends AMessageStringToken {

    public AMessageType(String keyword, int argCount) {
        super(keyword, argCount);
    }
    
    public AMessageType(String keyword) {
        super(keyword);
    }

}
