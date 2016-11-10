package application;

/**
 * Třída ClientException představuje výjimku, která může nastat při provádění
 * síťových operací, jako je vytváření a rušení spojení se serverem
 * nebo příjem a odesílání zpráv.
 * 
 * @author Petr Kozler
 */
public class ClientException extends Exception {
    
    /**
     * Chybová zpráva.
     */
    protected String errorMessage;
    
    /**
     * Vytvoří objekt výjimky nesoucí chybovou zprávu s předanými argumenty
     * v předaném formátu.
     * 
     * @param format formát chybové zprávy
     * @param args argumenty chybové zprávy
     */
    public ClientException(String format, Object... args) {
        errorMessage = String.format(format, args);
    }

    /**
     * Vrátí chybovou zprávu.
     * 
     * @return chybová zpráva
     */
    public String getErrorMessage() {
        return errorMessage;
    }
    
}
