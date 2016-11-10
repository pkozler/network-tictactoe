package visualisation;

import communication.containers.GameBoard;
import communication.containers.JoinedPlayer;
import java.util.ArrayList;

/**
 *
 * @author Petr Kozler
 */
public class CurrentGameDetail {
    
    public final ArrayList<JoinedPlayer> JOINED_PLAYERS;
    public final GameBoard GAME_BOARD;
    
    public CurrentGameDetail(GameBoard gameBoard, ArrayList<JoinedPlayer> joinedPlayers) {
        super();
        this.GAME_BOARD = gameBoard;
        this.JOINED_PLAYERS = joinedPlayers;
    }

}
