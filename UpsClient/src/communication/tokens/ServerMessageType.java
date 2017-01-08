package communication.tokens;

/**
 * Třída ServerMessageType představuje token řetězce zprávy označující
 * typ odpovědi serveru.
 * 
 * @author Petr Kozler
 */
public class ServerMessageType extends AMessageStringToken {
    
    /**
     * příznak hlavičky seznamu
     */
    public final boolean IS_LIST;
    
    /**
     * Vytvoří token notifikace se zadaným počtem argumentů a příznakem hlavičky.
     * 
     * @param keyword klíčové slovo
     * @param argCount počet argumentů
     * @param isList příznak hlavičky
     */
    public ServerMessageType(String keyword, int argCount, boolean isList) {
        super(keyword, argCount);
        IS_LIST = isList;
    }
    
    /**
     * Vytvoří token notifikace se zadaným počtem argumentů.
     * 
     * @param keyword klíčové slovo
     * @param argCount počet argumentů
     */
    public ServerMessageType(String keyword, int argCount) {
        this(keyword, argCount, false);
    }

    /**
     * Vytvoří token notifikace s příznakem hlavičky.
     * 
     * @param keyword klíčové slovo
     * @param isList příznak hlavičky
     */
    public ServerMessageType(String keyword, boolean isList) {
        this(keyword, 1, isList);
    }

}
