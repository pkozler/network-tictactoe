package interaction;

import communication.Message;
import communication.TcpClient;
import communication.containers.MissingListHeaderException;
import communication.tokens.InvalidMessageArgsException;
import communication.tokens.MissingMessageArgsException;
import configuration.Protocol;
import interaction.receiving.AParser;
import interaction.receiving.AUpdateParser;
import interaction.receiving.updates.CurrentGameDetailUpdateParser;
import interaction.receiving.updates.GameListUpdateParser;
import interaction.receiving.updates.PlayerListUpdateParser;

/**
 * Třída ListUpdateHandler představuje pomocnou komponentu
 * pro zpracování notifikací serveru a změnách stavu.
 * 
 * @author Petr Kozler
 */
public class ListUpdateHandler {
    
    /**
     * objekt klienta
     */
    public final TcpClient CLIENT;
    
    /**
     * parser aktualizace seznamu hráčů
     */
    private PlayerListUpdateParser playerListParser;
    
    /**
     * parser aktualizace seznamu her
     */
    private GameListUpdateParser gameListParser;
    
    /**
     * parser aktualizace herní místnosti
     */
    private CurrentGameDetailUpdateParser gameDetailParser;
    
    /**
     * Vytvoří objekt pro zpracování notifikací o změnách.
     * 
     * @param client objekt klienta
     */
    public ListUpdateHandler(TcpClient client) {
        CLIENT = client;
    }
    
    /**
     * Vytvoří nový nebo vybere existující parser pro zpracování přijaté
     * notifikace serveru, který následně vrátí.
     * 
     * @param message zpráva
     * @return parser
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException
     * @throws MissingListHeaderException 
     */
    public AParser handleUpdate(Message message) throws
            InvalidMessageArgsException, MissingMessageArgsException,
            MissingListHeaderException {
        // výběr parseru pro hlavičku seznamu
        AUpdateParser parser = selectForHeader(message);
        
        if (parser != null) {
            return parser;
        }
        
        // výběr parseru pro položku seznamu
        return selectForItems(message);
    }
    
    /**
     * Vybere příslušný parser podle typu přijaté zprávy, jedná-li se
     * o hlavičku seznamu zpráv tvořících notifikaci serveru o změně stavu,
     * a vytvoří instanci tohoto parseru pro zpracování seznamu.
     * 
     * @param message zpráva
     * @return parser
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    private AUpdateParser selectForHeader(Message message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        if (message.isTypeOf(Protocol.MSG_PLAYER_LIST)) {
            playerListParser = new PlayerListUpdateParser(CLIENT, message);
            
            return playerListParser;
        }
        
        if (message.isTypeOf(Protocol.MSG_GAME_LIST)) {
            gameListParser = new GameListUpdateParser(CLIENT, message);
            
            return gameListParser;
        }
        
        if (message.isTypeOf(Protocol.MSG_GAME_DETAIL)) {
            gameDetailParser = new CurrentGameDetailUpdateParser(CLIENT, message);
            
            return gameDetailParser;
        }
        
        return null;
    }
    
    /**
     * Vybere vytvořenou instanci parseru podle typu přijaté zprávy, jedná-li se
     * o položku seznamu zpráv tvořících notifikaci serveru o změně stavu,
     * následně předá tuto zprávu vybrané instanci parseru ke zpracování,
     * a pokud tato zpráva byla poslední položkou seznamu, odstraní vybranou
     * instanci parseru.
     * 
     * @param message zpráva
     * @return parser
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException
     * @throws MissingListHeaderException 
     */
    private AUpdateParser selectForItems(Message message)
            throws InvalidMessageArgsException, MissingMessageArgsException,
            MissingListHeaderException {
        if (message.isTypeOf(Protocol.MSG_PLAYER_LIST_ITEM)) {
            if (playerListParser == null) {
                throw new MissingListHeaderException();
            }
            
            if (!playerListParser.hasAllItems()) {
                playerListParser.parseNextItemMessage(message);
                
                return playerListParser;
            }
            
            AUpdateParser parser = playerListParser;
            playerListParser = null;
            
            return parser;
        }
        
        if (message.isTypeOf(Protocol.MSG_GAME_LIST_ITEM)) {
            if (gameListParser == null) {
                throw new MissingListHeaderException();
            }
            
            if (!gameListParser.hasAllItems()) {
                gameListParser.parseNextItemMessage(message);
                
                return gameListParser;
            }
            
            AUpdateParser parser = gameListParser;
            gameListParser = null;
            
            return parser;
        }
        
        if (message.isTypeOf(Protocol.MSG_GAME_PLAYER)) {
            if (gameDetailParser == null) {
                throw new MissingListHeaderException();
            }
            
            if (!gameDetailParser.hasAllItems()) {
                gameDetailParser.parseNextItemMessage(message);
                
                return gameDetailParser;
            }
            
            AUpdateParser parser = gameDetailParser;
            gameDetailParser = null;
            
            return parser;
        }
        
        return null;
    }
    
}
