package communication.tokens;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Petr Kozler
 */
public class ClientMessageType extends AMessageType {
    
    protected Set<MessageErrorArg> errorTypes = new HashSet<>();

    public ClientMessageType(String keyword, int argCount) {
        super(keyword, argCount);
    }
    
    public ClientMessageType(String keyword) {
        super(keyword);
    }
    
    public void addErrorType(MessageErrorArg errorType) {
        errorTypes.add(errorType);
    }
    
    public MessageErrorArg getErrorType(String keyword) throws InvalidMessageArgsException {
        for (MessageErrorArg errorType : errorTypes) {
            if (errorType.KEYWORD.equals(keyword)) {
                return errorType;
            }
        }
        
        throw new InvalidMessageArgsException();
    }
    
}
