package communication.tokens;

import java.util.Objects;

/**
 * Abstraktní třída AMessageStringToken představuje obecný token řetězce
 * zprávy přijaté od serveru.
 * Slouží jako pomocná datová třída pro definici klíčových slov
 * aplikačního protokolu.
 * 
 * @author Petr Kozler
 */
public abstract class AMessageStringToken {
    
    /**
     * klíčové slovo
     */
    public final String KEYWORD;
    
    /**
     * počet argumentů
     */
    public final int ARG_COUNT;
    
    /**
     * Vytvoří token.
     * 
     * @param keyword klíčové slovo
     * @param argCount počet argumentů
     */
    public AMessageStringToken(String keyword, int argCount) {
        if (keyword == null || keyword.isEmpty() || argCount < 0) {
            throw new IllegalArgumentException();
        }
        
        this.KEYWORD = keyword;
        this.ARG_COUNT = argCount;
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
        final AMessageStringToken other = (AMessageStringToken) obj;
        if (!Objects.equals(this.KEYWORD, other.KEYWORD)) {
            return false;
        }
        return true;
    }
    
    /**
     * Vrátí hashcode.
     * 
     * @return hashcode
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + Objects.hashCode(this.KEYWORD);
        return hash;
    }

    /**
     * Vrátí textovou reprezentaci položky.
     * 
     * @return textová reprezentace položky
     */
    @Override
    public String toString() {
        return KEYWORD;
    }

}
