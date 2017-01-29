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
     * herní pole
     */
    public final GameBoard GAME_BOARD;
    
    /**
     * hráči v herní místnosti
     */
    public final ArrayList<JoinedPlayer> JOINED_PLAYERS;
    
    /**
     * Vytvoří prázdnou přepravku.
     */
    public CurrentGameDetail() {
        this(null, null);
    }
    
    /**
     * Vytvoří přepravku se stavem herní místnosti.
     * 
     * @param gameBoard herní pole
     * @param joinedPlayers hráči v herní místnosti
     */
    public CurrentGameDetail(GameBoard gameBoard, ArrayList<JoinedPlayer> joinedPlayers) {
        GAME_BOARD = gameBoard;
        JOINED_PLAYERS = joinedPlayers != null ? joinedPlayers
                : new ArrayList<JoinedPlayer>();
    }

}
