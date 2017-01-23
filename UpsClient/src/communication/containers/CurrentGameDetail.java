package communication.containers;

import java.util.ArrayList;

/**
 * Třída CurrentGameDetail slouží jako přepravka pro předání informací
 * o stavu herního pole spolu s hráči v dané herní místnosti.
 * 
 * @author Petr Kozler
 */
public class CurrentGameDetail {
    
    /**
     * hráči v herní místnosti
     */
    public final ArrayList<JoinedPlayer> JOINED_PLAYERS;
    
    /**
     * herní pole
     */
    public final GameBoard GAME_BOARD;
    
    /**
     * vlastní údaje
     */
    private JoinedPlayer currentInfo;
    
    /**
     * Vytvoří přepravku se stavem herní místnosti.
     * 
     * @param gameBoard herní pole
     * @param joinedPlayers hráči v herní místnosti
     */
    public CurrentGameDetail(GameBoard gameBoard, ArrayList<JoinedPlayer> joinedPlayers) {
        GAME_BOARD = gameBoard;
        JOINED_PLAYERS = joinedPlayers == null ? new ArrayList<JoinedPlayer>() : joinedPlayers;
        currentInfo = null;
    }

    /**
     * Vrátí vlastní údaje.
     * 
     * @return vlastní údaje
     */
    public JoinedPlayer getCurrentInfo() {
        return currentInfo;
    }

    /**
     * Nastaví vlastní údaje.
     * 
     * @param playerId ID hráče
     */
    public void setCurrentInfo(int playerId) {
        for (JoinedPlayer p : JOINED_PLAYERS) {
            if (p.getId() == playerId) {
                currentInfo = p;
                
                return;
            }
        }
        
        currentInfo = null;
    }
    
}
