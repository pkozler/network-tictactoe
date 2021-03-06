package interaction.receiving.responses;

import communication.TcpClient;
import communication.Message;
import communication.tokens.InvalidMessageArgsException;
import communication.tokens.MissingMessageArgsException;
import communication.tokens.ResponseWithoutRequestException;
import communication.tokens.WrongResponseTypeException;
import configuration.Protocol;
import interaction.receiving.AResponseParser;
import interaction.sending.ARequestBuilder;
import interaction.sending.requests.LogoutRequestBuilder;

/**
 * Třída LogoutResponseParser představuje parser odpovědi serveru
 * na požadavek na odhlášení.
 * 
 * @author Petr Kozler
 */
public class LogoutResponseParser extends AResponseParser {

    /**
     * požadavek
     */
    protected LogoutRequestBuilder builder;
    
    /**
     * Vytvoří parser pro zpracování odpovědi serveru na požadavek na odhlášení.
     * 
     * @param client objekt klienta
     * @param message zpráva
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    public LogoutResponseParser(TcpClient client, Message message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        super(client, message);
        
        if (MESSAGE.getNextBoolArg()) {
            return;
        }
        
        messageErrorKeyword = MESSAGE.getNextArg();
    }

    /**
     * Vrátí výsledek požadavku a obnoví seznam hráčů.
     * 
     * @return výsledek
     */
    @Override
    public String updateClient() {
        if (messageErrorKeyword != null) {
            return getResponseError();
        }
        
        CLIENT.logOut();
        
        return "Hráč byl odhlášen ze serveru";
    }

    /**
     * Vrátí popis chyby při neplatném požadavku na změnu v seznamu hráčů.
     * 
     * @return popis chyby
     */
    @Override
    public String getResponseError() {
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_NOT_LOGGED.KEYWORD)) {
            return "Hráč ještě není přihlášen";
        }
        
        return super.getResponseError();
    }

    /**
     * Přiřadí odpovídající požadavek na změnu v seznamu hráčů.
     * 
     * @param builder požadavek
     * @throws ResponseWithoutRequestException
     * @throws WrongResponseTypeException 
     */
    @Override
    public void assignRequest(ARequestBuilder builder)
            throws ResponseWithoutRequestException, WrongResponseTypeException {
        super.assignRequest(builder);
        
        if (!(builder instanceof LogoutRequestBuilder)) {
            throw new WrongResponseTypeException();
        }
        
        this.builder = (LogoutRequestBuilder) builder;
    }

}
