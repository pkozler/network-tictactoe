package visualisation.components.game;

import communication.containers.JoinedPlayer;
import java.util.ArrayList;
import javax.swing.JList;
import javax.swing.JPanel;
import visualisation.listmodels.JoinedPlayerListModel;

/**
 * Třída JoinedPlayerListPanel představuje panel pro zobrazení
 * seznamu hráčů v aktuální herní místnosti.
 * 
 * @author Petr Kozler
 */
public class JoinedPlayerListPanel extends JPanel {

    /**
     * seznam zpráv v herní místnosti
     */
    private final JList<JoinedPlayer> JOINED_PLAYER_LIST_VIEW;
    
    /**
     * model seznamu zpráv v herní místnosti
     */
    private final JoinedPlayerListModel JOINED_PLAYER_LIST_MODEL;
    
    /**
     * Vytvoří panel pro zobrazení seznamu hráčů v herní místnosti.
     */
    public JoinedPlayerListPanel() {
        JOINED_PLAYER_LIST_MODEL = new JoinedPlayerListModel();
        JOINED_PLAYER_LIST_VIEW = new JList<>(JOINED_PLAYER_LIST_MODEL);
    }
    
    /**
     * Nastaví seznam hráčů v herní místnosti.
     * 
     * @param joinedPlayerList seznam hráčů v herní místnosti
     */
    public void setJoinedPlayerList(ArrayList<JoinedPlayer> joinedPlayerList) {
        JOINED_PLAYER_LIST_MODEL.setListWithSorting(joinedPlayerList);
    }
    
}
