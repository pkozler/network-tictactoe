package communication.containers;

/**
 * Třída GameInfo slouží jako přepravka pro uchování informací
 * o položce seznamu her.
 * 
 * @author Petr Kozler
 */
public class GameInfo implements Comparable<GameInfo> {
    
    /**
     * ID hry
     */
    public final int ID;
    
    /**
     * název
     */
    public final String NAME;
    
    /**
     * maximální počet hráčů
     */
    public final byte PLAYER_COUNT;
    
    /**
     * rozměr pole
     */
    public final byte BOARD_SIZE;
    
    /**
     * počet políček k obsazení
     */
    public final byte CELL_COUNT;
    
    /**
     * aktuální počet hráčů
     */
    private byte playerCounter;
    
    /**
     * Vytvoří seznam her.
     * 
     * @param id ID hry
     * @param name název
     * @param boardSize maximální počet hráčů
     * @param playerCount rozměr pole
     * @param cellCount počet políček k obsazení
     * @param playerCounter aktuální počet hráčů
     */
    public GameInfo(int id, String name, byte boardSize, byte playerCount, byte cellCount,
            byte playerCounter) {
        ID = id;
        NAME = name;
        BOARD_SIZE = boardSize;
        CELL_COUNT = cellCount;
        PLAYER_COUNT = playerCount;
        this.playerCounter = playerCounter;
    }

    /**
     * Vrátí aktuální počet hráčů.
     * 
     * @return aktuální počet hráčů
     */
    public byte getPlayerCounter() {
        return playerCounter;
    }

    /**
     * Vrátí hashcode.
     * 
     * @return hashcode
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + this.ID;
        return hash;
    }

    /**
     * Otestuje, zda se dvě položky shodují.
     * 
     * @param obj druhá položka
     * @return true, pokud se položky shodují, jinak false
     */
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
    
    /**
     * Vrátí textovou reprezentaci položky.
     * 
     * @return textová reprezentace položky
     */
    @Override
    public String toString() {
            return String.format("<html>%d: %s<br/>pole %d*%d - hraje se na %d<br/>hráčů v místnosti: %d/%d</html>", 
                            ID, NAME, BOARD_SIZE, BOARD_SIZE, CELL_COUNT, playerCounter, PLAYER_COUNT);
    }

    /**
     * Porovná dvě položky.
     * 
     * @param o druhá položka
     * @return výsledek porovnání
     */
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
