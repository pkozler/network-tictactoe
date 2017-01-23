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
import interaction.sending.requests.StartGameRequestBuilder;

/**
 * Třída StartGameResponseParser představuje parser odpovědi serveru
 * na požadavek na zahájení nového kola hry.
 * 
 * @author Petr Kozler
 */
public class StartGameResponseParser extends AResponseParser {

    protected StartGameRequestBuilder builder;
    
    /**
     * Vytvoří parser pro zpracování odpovědi serveru na požadavek na zahájení nového kola hry.
     * 
     * @param client objekt klienta
     * @param message zpráva
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    public StartGameResponseParser(TcpClient client, Message message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        super(client, message);
        
        if (MESSAGE.getNextBoolArg()) {
            return;
        }
        
        messageErrorKeyword = MESSAGE.getNextArg();
    }

    /**
     * Vrátí výsledek požadavku a obnoví stav hry.
     * 
     * @return výsledek
     */
    @Override
    public String updateClient() {
        if (messageErrorKeyword != null) {
            return getResponseError();
        }
        
        return String.format("Hráč zahájil nové kolo hry");
    }

    @Override
    public String getResponseError() {
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_NOT_IN_ROOM.KEYWORD)) {
            return "Hráč se nenachází v herní místnosti";
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_ROUND_ALREADY_STARTED.KEYWORD)) {
            return "Nové kolo hry bylo již zahájeno";
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_NOT_ENOUGH_PLAYERS.KEYWORD)) {
            return "V herní místnosti není dostatek hráčů pro zahájení nového kola hry";
        }
        
        return super.getResponseError();
    }

    @Override
    public void assignRequest(ARequestBuilder builder)
            throws ResponseWithoutRequestException, WrongResponseTypeException {
        super.assignRequest(builder);
        
        if (!(builder instanceof StartGameRequestBuilder)) {
            throw new WrongResponseTypeException();
        }
        
        this.builder = (StartGameRequestBuilder) builder;
    }

}
