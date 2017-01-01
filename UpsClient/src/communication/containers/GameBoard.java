package communication.containers;

/**
 *
 * @author Petr Kozler
 */
public class GameBoard {
    
    public final GameInfo GAME_INFO;
    private byte winnerIndex;
    private byte currentIndex;
    private Cell[][] cells;

    public GameBoard(GameInfo gameInfo, byte winnerIndex, byte currentIndex, Cell[][] cells) {
        GAME_INFO = gameInfo;
        this.winnerIndex = winnerIndex;
        this.currentIndex = currentIndex;
        this.cells = cells;
    }

    public byte getWinnerIndex() {
        return winnerIndex;
    }

    public byte getCurrentIndex() {
        return currentIndex;
    }

    public Cell[][] getCells() {
        return cells;
    }

}
