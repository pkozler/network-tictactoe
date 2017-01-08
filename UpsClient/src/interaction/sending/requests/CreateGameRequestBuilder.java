package interaction.sending.requests;

import communication.TcpMessage;
import configuration.Protocol;
import interaction.sending.ARequestBuilder;

/**
 * Třída CreateGameRequestBuilder představuje požadavek klienta
 * na vytvoření hry.
 * 
 * @author Petr Kozler
 */
public class CreateGameRequestBuilder extends ARequestBuilder {

    /**
     * název hry
     */
    private final String NAME;
    
    /**
     * počet hráčů
     */
    private final byte PLAYER_COUNT;
    
    /**
     * rozměr hracího pole
     */
    private final byte BOARD_SIZE;
    
    /**
     * počet políček k obsazení
     */
    private final byte CELL_COUNT;
    
    /**
     * Sestaví požadavek klienta na vytvoření hry.
     * 
     * @param name název hry
     * @param playerCount počet hráčů
     * @param boardSize rozměr hracího pole
     * @param cellCount počet políček k obsazení
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
     * Vrátí výsledek operace odeslání požadavku na vytvoření hry.
     * 
     * @return výsledek
     */
    @Override
    public String getStatus() {
        return String.format(
            "Odeslán požadavek na vytvoření herní místnosti o maximálním počtu hráčů %d, s hracím polem o rozměru %d, hrou na %d políček a pojmenovanou: %s",
                PLAYER_COUNT, BOARD_SIZE, CELL_COUNT, NAME);
    }

}
