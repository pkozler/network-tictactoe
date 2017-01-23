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
import interaction.sending.requests.JoinGameRequestBuilder;

/**
 * Třída JoinGameResponseParser představuje parser odpovědi serveru
 * na požadavek na připojení se ke hře.
 * 
 * @author Petr Kozler
 */
public class JoinGameResponseParser extends AResponseParser {

    protected JoinGameRequestBuilder builder;
    
    /**
     * Vytvoří parser pro zpracování odpovědi serveru na požadavek na připojení se ke hře.
     * 
     * @param client objekt klienta
     * @param message zpráva
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    public JoinGameResponseParser(TcpClient client, Message message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        super(client, message);
        
        if (MESSAGE.getNextBoolArg()) {
            return;
        }
        
        messageErrorKeyword = MESSAGE.getNextArg();
    }

    /**
     * Vrátí výsledek požadavku a obnoví seznam her.
     * 
     * @return výsledek
     */
    @Override
    public String updateClient() {
        if (messageErrorKeyword != null) {
            return getResponseError();
        }
        
        CLIENT.setCurrentGameId(builder.GAME_ID);
        
        return String.format("Hráč vstoupil do herní místnosti s ID: %d", builder.GAME_ID);
    }

    @Override
    public String getResponseError() {
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_INVALID_ID.KEYWORD)) {
            return "Zadané ID herní místnosti je neplatné";
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_ID_NOT_FOUND.KEYWORD)) {
            return "Herní místnost se zadaným ID neexistuje";
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_ALREADY_IN_ROOM.KEYWORD)) {
            return "Hráč se již nachází v herní místnosti";
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_ROOM_FULL.KEYWORD)) {
            return "Herní místnost je již plně obsazena";
        }
        
        return super.getResponseError();
    }

    @Override
    public void assignRequest(ARequestBuilder builder)
            throws ResponseWithoutRequestException, WrongResponseTypeException {
        super.assignRequest(builder);
        
        if (!(builder instanceof JoinGameRequestBuilder)) {
            throw new WrongResponseTypeException();
        }
        
        this.builder = (JoinGameRequestBuilder) builder;
    }

}
