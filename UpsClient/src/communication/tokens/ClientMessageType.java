package communication.tokens;

/**
 * Třída ClientMessageType představuje token řetězce zprávy označující
 * typ požadavku klienta.
 * 
 * @author Petr Kozler
 */
public class ClientMessageType extends AMessageStringToken {
    
    /**
     * počet argumentů odpovědi
     */
    public final int RESPONSE_ARG_COUNT;
    
    /**
     * Vytvoří token požadavku se zadaným počtem argumentů požadavku i odpovědi.
     * 
     * @param keyword klíčové slovo
     * @param argCount počet argumentů
     * @param responseArgCount počet argumentů odpovědi
     */
    public ClientMessageType(String keyword, int argCount, int responseArgCount) {
        super(keyword, argCount);
        RESPONSE_ARG_COUNT = responseArgCount;
    }
    
    /**
     * Vytvoří token požadavku se zadaným počtem argumentů.
     * 
     * @param keyword klíčové slovo
     * @param argCount počet argumentů
     */
    public ClientMessageType(String keyword, int argCount) {
        this(keyword, argCount, 1);
    }
    
    /**
     * Vytvoří token požadavku.
     * 
     * @param keyword klíčové slovo
     */
    public ClientMessageType(String keyword) {
        this(keyword, 0);
    }
    
}
