package visualisation.components;

import interaction.MessageBackgroundSender;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import visualisation.CurrentGameDetail;
import visualisation.components.game.BoardPanel;
import visualisation.components.game.JoinedPlayerListPanel;

/**
 * Třída CurrentGamePanel 
 * 
 * @author Petr Kozler
 */
public class CurrentGamePanel extends JPanel {

    /**
     * 
     */
    private final BoardPanel BOARD_PANEL;
    
    /**
     * 
     */
    private final JoinedPlayerListPanel JOINED_PLAYER_LIST_PANEL;
    
    /**
     * 
     * 
     * @param messageBackgroundSender 
     */
    public CurrentGamePanel(MessageBackgroundSender messageBackgroundSender) {
        BOARD_PANEL = new BoardPanel(messageBackgroundSender);
        JOINED_PLAYER_LIST_PANEL = new JoinedPlayerListPanel();
        
        add(BOARD_PANEL, BorderLayout.CENTER);
        add(JOINED_PLAYER_LIST_PANEL, BorderLayout.NORTH);
    }
    
    /**
     * 
     * 
     * @param currentGameDetail 
     */
    public void setGameDetail(CurrentGameDetail currentGameDetail) {
        BOARD_PANEL.setGameBoard(currentGameDetail.GAME_BOARD);
        JOINED_PLAYER_LIST_PANEL.setJoinedPlayerList(currentGameDetail.JOINED_PLAYERS);
    }
    
}
