package communication.containers;

/**
 * Třída JoinedPlayer slouží jako přepravka pro uchování informací
 * o položce seznamu hráče v dané herní místnosti.
 * 
 * @author Petr Kozler
 */
public class JoinedPlayer implements Comparable<JoinedPlayer> {
    
    /**
     * klíč - ID hráče
     */
    private int id;
    
    /**
     * přezdívka hráče
     */
    private String nickname;
    
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
     * @param id ID hráče
     * @param nickname přezdívka hráče
     * @param currentGameIndex pořadí v aktuální hře
     * @param currentGameScore skóre v aktuální hře
     */
    public JoinedPlayer(int id, String nickname, byte currentGameIndex, int currentGameScore) {
        this.id = id;
        this.nickname = nickname;
        this.currentGameIndex = currentGameIndex;
        this.currentGameScore = currentGameScore;
    }

    /**
     * Vrátí ID hráče.
     * 
     * @return ID hráče
     */
    public int getId() {
        return id;
    }
    
    /**
     * Vrátí přezdívku hráče.
     * 
     * @return přezdívka hráče
     */
    public String getNickname() {
        return nickname;
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
        int hash = 3;
        hash = 59 * hash + this.currentGameIndex;
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
        if (this.id != other.id) {
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
            return String.format("<html>%d: %s<br/>skóre ve hře: %d</html>", 
                            currentGameIndex, nickname, currentGameScore);
    }
    
    /**
     * Porovná dvě položky.
     * 
     * @param o druhá položka
     * @return výsledek porovnání
     */
    @Override
    public int compareTo(JoinedPlayer o) {
        return currentGameIndex - o.currentGameIndex;
    }
    
}
