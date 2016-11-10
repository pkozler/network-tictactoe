package communication.containers;

/**
 *
 * @author Petr Kozler
 */
public class PlayerInfo implements Comparable<PlayerInfo> {
    
    public final int ID;
    public final String NICK;
    private int totalScore;
    
    public PlayerInfo(int id, String nick, int totalScore) {
        ID = id;
        NICK = nick;
        this.totalScore = totalScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + this.ID;
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
        final PlayerInfo other = (PlayerInfo) obj;
        if (this.ID != other.ID) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
            return String.format("<html>%d: %s<br/>skóre: %d</html>", 
                            ID, NICK, totalScore);
    }

    @Override
    public int compareTo(PlayerInfo o) {
        if (totalScore != o.totalScore) {
            return o.totalScore - totalScore;
        }
        
        return ID - o.ID;
    }
    
}
