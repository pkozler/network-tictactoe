package interaction.sending.requests;

import communication.TcpMessage;
import configuration.Protocol;
import interaction.sending.ARequestBuilder;

/**
 * Třída CreateGameRequestBuilder 
 * 
 * @author Petr Kozler
 */
public class CreateGameRequestBuilder extends ARequestBuilder {

    /**
     * 
     */
    private final String NAME;
    
    /**
     * 
     */
    private final byte PLAYER_COUNT;
    
    /**
     * 
     */
    private final byte BOARD_SIZE;
    
    /**
     * 
     */
    private final byte CELL_COUNT;
    
    /**
     * 
     * 
     * @param name
     * @param playerCount
     * @param boardSize
     * @param cellCount 
     */
    public CreateGameRequestBuilder(String name,
            byte playerCount, byte boardSize, byte cellCount) {
        NAME = name;
        PLAYER_COUNT = playerCount;
        BOARD_SIZE = boardSize;
        CELL_COUNT = cellCount;
        
        message = new TcpMessage(Protocol.MSG_CREATE_GAME.KEYWORD, NAME,
                Byte.toString(PLAYER_COUNT), Byte.toString(BOARD_SIZE), Byte.toString(CELL_COUNT));
    }
    
    /**
     * 
     * 
     * @return 
     */
    @Override
    public String getStatus() {
        return String.format(
            "Odeslán požadavek na vytvoření herní místnosti o maximálním počtu hráčů %d, s hracím polem o rozměru %d, hrou na %d políček a pojmenovanou: %s",
                PLAYER_COUNT, BOARD_SIZE, CELL_COUNT, NAME);
    }

}
