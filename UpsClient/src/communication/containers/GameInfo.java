package communication;

/**
 *
 * @author Petr Kozler
 */
public class GameInfo {
    public final int ID;
    public final String NAME;
    public final int BOARD_SIZE;
    public final int CELL_COUNT;
    public final int PLAYER_COUNT;
    public final int PLAYER_COUNTER;
    
    public GameInfo(int id, String name, int boardSize, int playerCount, int cellCount, int playerCounter) {
        ID = id;
        NAME = name;
        BOARD_SIZE = boardSize;
        CELL_COUNT = cellCount;
        PLAYER_COUNT = playerCount;
        PLAYER_COUNTER = playerCounter;
    }
}
