package communication.tokens;

/**
 *
 * @author Petr Kozler
 */
public class ServerMessageType extends AMessageType {
    
    private boolean list;
    
    public ServerMessageType(String keyword, int argCount, boolean list) {
        super(keyword, argCount);
        this.list = list;
    }
    
    public ServerMessageType(String keyword, boolean list) {
        super(keyword);
        this.list = list;
    }

    public boolean isList() {
        return list;
    }

}
