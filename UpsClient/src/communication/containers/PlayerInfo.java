package communication.containers;

/**
 * Třída PlayerInfo slouží jako přepravka pro uchování informací
 * o položce seznamu hráčů.
 * 
 * @author Petr Kozler
 */
public class PlayerInfo implements Comparable<PlayerInfo> {
    
    /**
     * ID hráče
     */
    public final int ID;
    
    /**
     * přezdívka
     */
    public final String NICK;
    
    /**
     * celkové skóre
     */
    private int totalScore;
    
    /**
     * Vytvoří seznam hráčů.
     * 
     * @param id ID hráče
     * @param nick přezdívka
     * @param totalScore celkové skóre
     */
    public PlayerInfo(int id, String nick, int totalScore) {
        ID = id;
        NICK = nick;
        this.totalScore = totalScore;
    }
    
    /**
     * Vrátí celkové skóre.
     * 
     * @return celkové skóre
     */
    public int getTotalScore() {
        return totalScore;
    }
    
    /**
     * Vrátí hashcode.
     * 
     * @return hashcode
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + this.ID;
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
        final PlayerInfo other = (PlayerInfo) obj;
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
        return String.format("<html>%d: %s<br />skóre: %d</html>", ID, NICK, totalScore);
    }

    /**
     * Porovná dvě položky.
     * 
     * @param o druhá položka
     * @return výsledek porovnání
     */
    @Override
    public int compareTo(PlayerInfo o) {
        return ID - o.ID;
    }
    
}
