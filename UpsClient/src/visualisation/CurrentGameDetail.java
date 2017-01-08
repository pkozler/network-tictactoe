package visualisation;

import communication.containers.GameBoard;
import communication.containers.JoinedPlayer;
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
     * Vytvoří přepravku se stavem herní místnosti.
     * 
     * @param gameBoard herní pole
     * @param joinedPlayers hráči v herní místnosti
     */
    public CurrentGameDetail(GameBoard gameBoard, ArrayList<JoinedPlayer> joinedPlayers) {
        super();
        this.GAME_BOARD = gameBoard;
        this.JOINED_PLAYERS = joinedPlayers;
    }

}
