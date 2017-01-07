package visualisation;

import communication.containers.GameBoard;
import communication.containers.JoinedPlayer;
import java.util.ArrayList;

/**
 * Třída CurrentGameDetail 
 * 
 * @author Petr Kozler
 */
public class CurrentGameDetail {
    
    /**
     * 
     */
    public final ArrayList<JoinedPlayer> JOINED_PLAYERS;
    
    /**
     * 
     */
    public final GameBoard GAME_BOARD;
    
    /**
     * 
     * 
     * @param gameBoard
     * @param joinedPlayers 
     */
    public CurrentGameDetail(GameBoard gameBoard, ArrayList<JoinedPlayer> joinedPlayers) {
        super();
        this.GAME_BOARD = gameBoard;
        this.JOINED_PLAYERS = joinedPlayers;
    }

}
