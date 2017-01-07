package communication.containers;

/**
 * Třída JoinedPlayer 
 * 
 * @author Petr Kozler
 */
public class JoinedPlayer implements Comparable<JoinedPlayer> {
    
    public final PlayerInfo PLAYER_INFO;
    private byte currentGameIndex;
    private int currentGameScore;

    public JoinedPlayer(PlayerInfo playerInfo, byte currentGameIndex, int currentGameScore) {
        PLAYER_INFO = playerInfo;
        this.currentGameIndex = currentGameIndex;
        this.currentGameScore = currentGameScore;
    }
    
    public byte getCurrentGameIndex() {
        return currentGameIndex;
    }

    public int getCurrentGameScore() {
        return currentGameScore;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + this.PLAYER_INFO.hashCode();
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
        final JoinedPlayer other = (JoinedPlayer) obj;
        if (!this.PLAYER_INFO.equals(other.PLAYER_INFO)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
            return String.format("<html>%d: %s<br/>%d. na tahu<br/>skóre za hru / celkem: %d/%d</html>", 
                            PLAYER_INFO.ID, PLAYER_INFO.NICK, currentGameIndex, currentGameScore, PLAYER_INFO.getTotalScore());
    }
    
    @Override
    public int compareTo(JoinedPlayer o) {
        if (currentGameScore != o.currentGameScore) {
            return o.currentGameScore - currentGameScore;
        }
        
        return currentGameIndex - o.currentGameIndex;
    }
    
}
