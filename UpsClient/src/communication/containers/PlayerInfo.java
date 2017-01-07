package communication.containers;

/**
 * Třída PlayerInfo 
 * 
 * @author Petr Kozler
 */
public class PlayerInfo implements Comparable<PlayerInfo> {
    
    /**
     * 
     */
    public final int ID;
    
    /**
     * 
     */
    public final String NICK;
    
    /**
     * 
     */
    private int totalScore;
    
    /**
     * 
     * 
     * @param id
     * @param nick
     * @param totalScore 
     */
    public PlayerInfo(int id, String nick, int totalScore) {
        ID = id;
        NICK = nick;
        this.totalScore = totalScore;
    }
    
    /**
     * 
     * 
     * @return 
     */
    public int getTotalScore() {
        return totalScore;
    }
    
    /**
     * 
     * 
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + this.ID;
        return hash;
    }

    /**
     * 
     * 
     * @param obj
     * @return 
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
        final PlayerInfo other = (PlayerInfo) obj;
        if (this.ID != other.ID) {
            return false;
        }
        return true;
    }
    
    /**
     * 
     * 
     * @return 
     */
    @Override
    public String toString() {
        return String.format("<html>%d: %s</html>", ID, NICK);
    }

    /**
     * 
     * 
     * @param o
     * @return 
     */
    @Override
    public int compareTo(PlayerInfo o) {
        return ID - o.ID;
    }
    
}
