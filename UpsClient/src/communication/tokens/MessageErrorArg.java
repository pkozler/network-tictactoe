package communication.tokens;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Petr Kozler
 */
public class MessageErrorArg extends AMessageStringToken {
    
    protected Map<ClientMessageType, String> descriptions = new HashMap<>();

    public MessageErrorArg(String keyword, int argCount) {
        super(keyword, argCount);
    }
    
    public MessageErrorArg(String keyword) {
        super(keyword);
    }
    
    public String getDescriptionByMessageType(ClientMessageType messageType, Object ... args) {
        String str = descriptions.get(messageType);
        
        return String.format(str, args);
    }
    
    public void associateWithMessageType(ClientMessageType messageType, String errorDescription) {
        messageType.addErrorType(this);
        descriptions.put(messageType, errorDescription);
    }

}
