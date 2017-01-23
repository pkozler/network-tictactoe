package interaction.receiving;

import communication.TcpClient;
import communication.Message;
import communication.tokens.ResponseWithoutRequestException;
import communication.tokens.WrongResponseTypeException;
import interaction.sending.ARequestBuilder;

/**
 * Abstraktní třída AResponseParser představuje obecný parser odpovědí
 * na požadavky přijatých ze serveru.
 * 
 * @author Petr Kozler
 */
public abstract class AResponseParser extends AParser {
    
    /**
     * chyba při odmítnutí požadavku
     */
    protected String messageErrorKeyword;
    
    /**
     * Vytvoří parser odpovědi.
     * 
     * @param client objekt klienta
     * @param message zpráva
     */
    public AResponseParser(TcpClient client, Message message) {
        super(client, message);
    }
    
    public String getResponseError() {
        return "Chyba - neplatný typ odmítnutí požadavku: " + messageErrorKeyword;
    }
    
    public void assignRequest(ARequestBuilder builder)
            throws ResponseWithoutRequestException, WrongResponseTypeException {
        if (builder == null) {
            throw new ResponseWithoutRequestException();
        }
    }
    
}
