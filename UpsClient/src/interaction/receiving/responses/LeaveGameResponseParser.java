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
import interaction.sending.requests.LeaveGameRequestBuilder;

/**
 * Třída LeaveGameResponseParser představuje parser odpovědi serveru
 * na požadavek na opuštění hry.
 * 
 * @author Petr Kozler
 */
public class LeaveGameResponseParser extends AResponseParser {

    protected LeaveGameRequestBuilder builder;
    
    /**
     * Vytvoří parser pro zpracování odpovědi serveru na požadavek na opuštění hry.
     * 
     * @param client objekt klienta
     * @param message zpráva
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    public LeaveGameResponseParser(TcpClient client, Message message)
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
        
        CLIENT.leaveGame();
        
        return String.format("Hráč opustil herní místnost");
    }

    @Override
    public String getResponseError() {
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_NOT_IN_ROOM.KEYWORD)) {
            return "Hráč se nenachází v herní místnosti";
        }
        
        return super.getResponseError();
    }

    @Override
    public void assignRequest(ARequestBuilder builder)
            throws ResponseWithoutRequestException, WrongResponseTypeException {
        super.assignRequest(builder);
        
        if (!(builder instanceof LeaveGameRequestBuilder)) {
            throw new WrongResponseTypeException();
        }
        
        this.builder = (LeaveGameRequestBuilder) builder;
    }

}
