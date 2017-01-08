package communication.tokens;

/**
 * Třída ClientMessageErrorArg představuje token řetězce zprávy označující
 * název chyby při odmítnutí požadavku serverem.
 * 
 * @author Petr Kozler
 */
public class ClientMessageErrorArg extends AMessageStringToken {
    
    /**
     * Vytvoří token chyby se zadaným počtem argumentů.
     * 
     * @param keyword klíčové slovo
     * @param argCount počet argumentů
     */
    public ClientMessageErrorArg(String keyword, int argCount) {
        super(keyword, argCount);
    }
    
    /**
     * Vytvoří token chyby.
     * 
     * @param keyword klíčové slovo
     */
    public ClientMessageErrorArg(String keyword) {
        this(keyword, 0);
    }
    
}
