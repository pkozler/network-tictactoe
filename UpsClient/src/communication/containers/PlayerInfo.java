package communication.containers;

/**
 * Třída PlayerInfo slouží jako přepravka pro uchování informací
 * o položce seznamu hráčů.
 * 
 * @author Petr Kozler
 */
public class PlayerInfo implements Comparable<PlayerInfo> {
    
    /**
     * klíč - ID hráče
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
     * příznak připojení
     */
    private boolean connected;
    
    /**
     * Vytvoří seznam hráčů.
     * 
     * @param id ID hráče
     * @param nick přezdívka
     * @param totalScore celkové skóre
     * @param connected příznak připojení
     */
    public PlayerInfo(int id, String nick, int totalScore, boolean connected) {
        ID = id;
        NICK = nick;
        this.totalScore = totalScore;
        this.connected = connected;
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
     * Vrátí příznak připojení.
     * 
     * @return příznak připojení
     */
    public boolean isConnected() {
        return connected;
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
        return String.format("<html>%d: %s<br />celkové skóre: %d<br />stav připojení: %s</html>",
                ID, NICK, totalScore, connected ? "online" : "offline");
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
