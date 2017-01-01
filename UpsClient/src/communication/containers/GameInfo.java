package communication.containers;

/**
 *
 * @author Petr Kozler
 */
public class GameInfo implements Comparable<GameInfo> {
    
    public final int ID;
    public final String NAME;
    public final byte PLAYER_COUNT;
    public final byte BOARD_SIZE;
    public final byte CELL_COUNT;
    private byte playerCounter;
    
    public GameInfo(int id, String name, byte boardSize, byte playerCount, byte cellCount,
            byte playerCounter) {
        ID = id;
        NAME = name;
        BOARD_SIZE = boardSize;
        CELL_COUNT = cellCount;
        PLAYER_COUNT = playerCount;
        this.playerCounter = playerCounter;
    }

    public byte getPlayerCounter() {
        return playerCounter;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + this.ID;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GameInfo other = (GameInfo) obj;
        if (this.ID != other.ID) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
            return String.format("<html>%d: %s<br/>pole %d*%d - hraje se na %d<br/>hráčů v místnosti: %d/%d</html>", 
                            ID, NAME, BOARD_SIZE, BOARD_SIZE, CELL_COUNT, playerCounter, PLAYER_COUNT);
    }

    @Override
    public int compareTo(GameInfo o) {
        byte freeSlotCountA = (byte) (PLAYER_COUNT - playerCounter);
        byte freeSlotCountB = (byte) (o.PLAYER_COUNT - o.playerCounter);
        
        if (freeSlotCountA != freeSlotCountB) {
            return freeSlotCountB - freeSlotCountA;
        }
        
        return ID - o.ID;
    }
    
}
