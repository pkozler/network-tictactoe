package communication.containers;

/**
 * Třída JoinedPlayer slouží jako přepravka pro uchování informací
 * o položce seznamu hráče v dané herní místnosti.
 * 
 * @author Petr Kozler
 */
public class JoinedPlayer implements Comparable<JoinedPlayer> {
    
    /**
     * základní informace o hráči
     */
    public final PlayerInfo PLAYER_INFO;
    
    /**
     * pořadí v aktuální hře
     */
    private byte currentGameIndex;
    
    /**
     * skóre v aktuální hře
     */
    private int currentGameScore;

    /**
     * Vytvoří seznam hráčů v herní místnosti.
     * 
     * @param playerInfo základní informace o hráči
     * @param currentGameIndex pořadí v aktuální hře
     * @param currentGameScore skóre v aktuální hře
     */
    public JoinedPlayer(PlayerInfo playerInfo, byte currentGameIndex, int currentGameScore) {
        PLAYER_INFO = playerInfo;
        this.currentGameIndex = currentGameIndex;
        this.currentGameScore = currentGameScore;
    }
    
    /**
     * Vrátí pořadí v aktuální hře.
     * 
     * @return pořadí v aktuální hře
     */
    public byte getCurrentGameIndex() {
        return currentGameIndex;
    }

    /**
     * Vrátí skóre v aktuální hře
     * 
     * @return skóre v aktuální hře
     */
    public int getCurrentGameScore() {
        return currentGameScore;
    }

    /**
     * Vrátí hashcode.
     * 
     * @return hashcode
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + this.PLAYER_INFO.hashCode();
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
        final JoinedPlayer other = (JoinedPlayer) obj;
        if (!this.PLAYER_INFO.equals(other.PLAYER_INFO)) {
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
            return String.format("<html>%d: %s<br/>%d. na tahu<br/>skóre za hru / celkem: %d/%d</html>", 
                            PLAYER_INFO.ID, PLAYER_INFO.NICK, currentGameIndex, currentGameScore, PLAYER_INFO.getTotalScore());
    }
    
    /**
     * Porovná dvě položky.
     * 
     * @param o druhá položka
     * @return výsledek porovnání
     */
    @Override
    public int compareTo(JoinedPlayer o) {
        if (currentGameScore != o.currentGameScore) {
            return o.currentGameScore - currentGameScore;
        }
        
        return currentGameIndex - o.currentGameIndex;
    }
    
}
