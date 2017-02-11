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
import interaction.sending.requests.LoginRequestBuilder;

/**
 * Třída LoginResponseParser představuje parser odpovědi serveru
 * na požadavek na přihlášení.
 * 
 * @author Petr Kozler
 */
public class LoginResponseParser extends AResponseParser {

    /**
     * požadavek
     */
    protected LoginRequestBuilder builder;
    
    /**
     * ID hráče obdržené po přihlášení
     */
    protected int playerId;
    
    /**
     * Vytvoří parser pro zpracování odpovědi serveru na požadavek na přihlášení.
     * 
     * @param client objekt klienta
     * @param message zpráva
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    public LoginResponseParser(TcpClient client, Message message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        super(client, message);
        
        if (MESSAGE.getNextBoolArg()) {
            playerId = MESSAGE.getNextIntArg(0);
            
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
        
        CLIENT.logIn(playerId);
        
        return String.format("Hráč byl přihlášen k serveru pod id %d, nickname hráče: \"%s\"",
                playerId, builder.NICKNAME);
    }

    /**
     * Vrátí popis chyby při neplatném požadavku na změnu v seznamu hráčů.
     * 
     * @return popis chyby
     */
    @Override
    public String getResponseError() {
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_ALREADY_LOGGED.KEYWORD)) {
            return "Hráč je již přihlášen";
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_INVALID_NAME.KEYWORD)) {
            return "Zadaná přezdívka hráče je neplatná";
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_EXISTING_NAME.KEYWORD)) {
            return "Hráč se zadanou přezdívkou již existuje";
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
        
        if (!(builder instanceof LoginRequestBuilder)) {
            throw new WrongResponseTypeException();
        }
        
        this.builder = (LoginRequestBuilder) builder;
    }

}
