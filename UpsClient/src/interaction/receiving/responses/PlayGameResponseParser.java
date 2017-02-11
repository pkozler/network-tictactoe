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
import interaction.sending.requests.PlayGameRequestBuilder;

/**
 * Třída PlayGameResponseParser představuje parser odpovědi serveru
 * na požadavek na herní tah.
 * 
 * @author Petr Kozler
 */
public class PlayGameResponseParser extends AResponseParser {

    /**
     * požadavek
     */
    protected PlayGameRequestBuilder builder;
    
    /**
     * Vytvoří parser pro zpracování odpovědi serveru na požadavek na provedení tahu.
     * 
     * @param client objekt klienta
     * @param message zpráva
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    public PlayGameResponseParser(TcpClient client, Message message)
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
        
        return String.format("Hráč provedl herní tah na souřadnicích: [%d; %d]",
                builder.X, builder.Y);
    }

    /**
     * Vrátí popis chyby při neplatném požadavku na změnu stavu hry.
     * 
     * @return popis chyby
     */
    @Override
    public String getResponseError() {
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_NOT_IN_ROOM.KEYWORD)) {
            return "Hráč se nenachází v herní místnosti se zadaným ID";
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_ROUND_NOT_STARTED.KEYWORD)) {
            return "Hráč nemůže táhnout před zahájením kola";
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_NOT_ON_TURN.KEYWORD)) {
            return "Hráč není na tahu";
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_CELL_OUT_OF_BOARD.KEYWORD)) {
            return "Hráč provedl tah na pozici mimo hranice hracího pole";
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_CELL_OCCUPIED.KEYWORD)) {
            return "Hráč se pokusil táhnout na již obsazené políčko";
        }
        
        return super.getResponseError();
    }

    /**
     * Přiřadí odpovídající požadavek na změnu stavu hry.
     * 
     * @param builder požadavek
     * @throws ResponseWithoutRequestException
     * @throws WrongResponseTypeException 
     */
    @Override
    public void assignRequest(ARequestBuilder builder)
            throws ResponseWithoutRequestException, WrongResponseTypeException {
        super.assignRequest(builder);
        
        if (!(builder instanceof PlayGameRequestBuilder)) {
            throw new WrongResponseTypeException();
        }
        
        this.builder = (PlayGameRequestBuilder) builder;
    }

}
