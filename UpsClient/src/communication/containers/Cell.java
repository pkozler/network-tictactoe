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

    public void setPlayerIndex(byte playerIndex) {
        this.playerIndex = playerIndex;
    }

    public boolean isWinning() {
        return winning;
    }

    public void setWinning(boolean winning) {
        this.winning = winning;
    }
    
}
