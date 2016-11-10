package communication;

/**
 *
 * @author Petr Kozler
 */
public class PlayerInfo {
    public final int ID;
    public final String NAME;
    public final int TOTAL_SCORE;
    
    public PlayerInfo(int id, String name, int totalScore) {
        ID = id;
        NAME = name;
        TOTAL_SCORE = totalScore;
    }
}
