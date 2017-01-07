package communication.tokens;

import java.util.Objects;

/**
 * Abstraktní třída AMessageStringToken 
 * 
 * @author Petr Kozler
 */
public abstract class AMessageStringToken {
    
    /**
     * 
     */
    public final String KEYWORD;
    
    /**
     * 
     */
    public final int ARG_COUNT;
    
    /**
     * 
     * 
     * @param keyword
     * @param argCount 
     */
    public AMessageStringToken(String keyword, int argCount) {
        if (keyword == null || keyword.isEmpty() || argCount < 0) {
            throw new IllegalArgumentException();
        }
        
        this.KEYWORD = keyword;
        this.ARG_COUNT = argCount;
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
        final AMessageStringToken other = (AMessageStringToken) obj;
        if (!Objects.equals(this.KEYWORD, other.KEYWORD)) {
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
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + Objects.hashCode(this.KEYWORD);
        return hash;
    }

    /**
     * 
     * 
     * @return 
     */
    @Override
    public String toString() {
        return KEYWORD;
    }

}
