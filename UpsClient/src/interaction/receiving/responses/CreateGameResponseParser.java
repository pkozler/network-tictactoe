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
import interaction.sending.requests.CreateGameRequestBuilder;

/**
 * Třída CreateGameResponseParser představuje parser odpovědi serveru
 * na požadavek na vytvoření hry.
 * 
 * @author Petr Kozler
 */
public class CreateGameResponseParser extends AResponseParser {

    /**
     * požadavek
     */
    protected CreateGameRequestBuilder builder;
    
    /**
     * ID přiřazené k vytvořené hře
     */
    protected int newGameId;
    
    /**
     * Vytvoří parser pro zpracování odpovědi serveru na požadavek na vytvoření hry.
     * 
     * @param client objekt klienta
     * @param message zpráva
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    public CreateGameResponseParser(TcpClient client, Message message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        super(client, message);
        
        if (MESSAGE.getNextBoolArg()) {
            newGameId = MESSAGE.getNextIntArg(0);
            
            return;
        }
        
        String error = MESSAGE.getNextArg();

        if (!(Protocol.MSG_ERR_INVALID_PLAYER_COUNT.KEYWORD.equals(messageErrorKeyword)
                || Protocol.MSG_ERR_INVALID_BOARD_SIZE.KEYWORD.equals(messageErrorKeyword)
                || Protocol.MSG_ERR_INVALID_CELL_COUNT.KEYWORD.equals(messageErrorKeyword))) {
            messageErrorKeyword = error;
            
            return;
        }

        int minValue = MESSAGE.getNextIntArg(1);
        int maxValue = MESSAGE.getNextIntArg(1);
        
        if (minValue > maxValue) {
            throw new InvalidMessageArgsException();
        }

        messageErrorKeyword = error;
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
        
        CLIENT.joinGame(newGameId);
        
        return String.format("Hráč vytvořil herní místnost s ID %d, název místnosti: \"%s\"",
                newGameId, builder.NAME);
    }

    /**
     * Vrátí popis chyby při neplatném požadavku na změnu v seznamu her.
     * 
     * @return popis chyby
     */
    @Override
    public String getResponseError() {
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_INVALID_NAME.KEYWORD)) {
            return "Zadaný název herní místnosti je neplatný";
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_EXISTING_NAME.KEYWORD)) {
            return "Herní místnost se zadaným názvem již existuje";
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_INVALID_BOARD_SIZE.KEYWORD)) {
            return "Zadaný rozměr hracího pole je neplatný";
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_INVALID_PLAYER_COUNT.KEYWORD)) {
            return "Zadaný počet hráčů je neplatný";
        }
        
        if (messageErrorKeyword.equals(Protocol.MSG_ERR_INVALID_CELL_COUNT.KEYWORD)) {
            return "Zadaný počet políček potřebných k obsazení je neplatný";
        }
        
        return super.getResponseError();
    }

    /**
     * Přiřadí odpovídající požadavek na změnu v seznamu her.
     * 
     * @param builder požadavek
     * @throws ResponseWithoutRequestException
     * @throws WrongResponseTypeException 
     */
    @Override
    public void assignRequest(ARequestBuilder builder)
            throws ResponseWithoutRequestException, WrongResponseTypeException {
        super.assignRequest(builder);
        
        if (!(builder instanceof CreateGameRequestBuilder)) {
            throw new WrongResponseTypeException();
        }
        
        this.builder = (CreateGameRequestBuilder) builder;
    }

}
