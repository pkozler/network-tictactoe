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
 *
 * @author Petr Kozler
 */
public class ListUpdateHandler {
    
    /**
     * objekt klienta
     */
    public final TcpClient CLIENT;
    
    private PlayerListUpdateParser playerListParser;
    
    private GameListUpdateParser gameListParser;
    
    private CurrentGameDetailUpdateParser gameDetailParser;
    
    public ListUpdateHandler(TcpClient client) {
        CLIENT = client;
    }
    
    public AParser handleUpdate(Message message) throws
            InvalidMessageArgsException, MissingMessageArgsException,
            MissingListHeaderException {
        AUpdateParser parser = selectForHeader(message);
        
        if (parser != null) {
            return parser;
        }
        
        return selectForItems(message);
    }
    
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
