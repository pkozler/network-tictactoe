package visualisation.components;

import interaction.MessageBackgroundSender;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import visualisation.CurrentGameDetail;
import visualisation.components.game.BoardPanel;
import visualisation.components.game.JoinedPlayerListPanel;

/**
 * Třída CurrentGamePanel představuje panel pro zobrazení stavu aktuální
 * herní místnosti.
 * 
 * @author Petr Kozler
 */
public class CurrentGamePanel extends JPanel {

    /**
     * panel herního pole
     */
    private final BoardPanel BOARD_PANEL;
    
    /**
     * panel hráčů v herní místnosti
     */
    private final JoinedPlayerListPanel JOINED_PLAYER_LIST_PANEL;
    
    /**
     * Vytvoří panel herní místnosti.
     * 
     * @param messageBackgroundSender vysílač zpráv
     */
    public CurrentGamePanel(MessageBackgroundSender messageBackgroundSender) {
        BOARD_PANEL = new BoardPanel(messageBackgroundSender);
        JOINED_PLAYER_LIST_PANEL = new JoinedPlayerListPanel();
        
        add(BOARD_PANEL, BorderLayout.CENTER);
        add(JOINED_PLAYER_LIST_PANEL, BorderLayout.NORTH);
    }
    
    /**
     * Nastaví stav herní místnosti.
     * 
     * @param currentGameDetail stav herní místnosti
     */
    public void setGameDetail(CurrentGameDetail currentGameDetail) {
        BOARD_PANEL.setGameBoard(currentGameDetail.GAME_BOARD);
        JOINED_PLAYER_LIST_PANEL.setJoinedPlayerList(currentGameDetail.JOINED_PLAYERS);
    }
    
}
