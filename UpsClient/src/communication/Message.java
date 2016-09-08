package communication;

/**
 *
 * @author Petr Kozler
 */
public class Message {
    private String type;
    private String[] args;
    private int current;
    
    public Message(String type, String... args) {
        this.type = type;
        this.args = args;
        current = 0;
    }
    
    public boolean hasType() {
        return type != null;
    }
    
    public boolean hasArgs() {
        return args != null && args.length > 0;
    }
    
    public String getType() {
        return type;
    }
    
    public String[] getArgs() {
        return args;
    }
    
    public boolean hasNextArg() {
        return current < args.length;
    }
    
    public String getNextArg() {
        return args[current++];
    }
}
