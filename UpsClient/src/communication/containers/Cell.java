package communication.containers;

/**
 *
 * @author Petr Kozler
 */
public class Cell {
    
    private byte playerIndex;
    private boolean winning;

    public Cell(byte playerIndex, boolean winning) {
        this.playerIndex = playerIndex;
        this.winning = winning;
    }

    public byte getPlayerIndex() {
        return playerIndex;
    }

    public boolean isWinning() {
        return winning;
    }

}
