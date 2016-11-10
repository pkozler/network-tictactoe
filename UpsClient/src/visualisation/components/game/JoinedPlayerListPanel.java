package visualisation.components.game;

import communication.containers.JoinedPlayer;
import java.util.ArrayList;
import javax.swing.JList;
import javax.swing.JPanel;
import visualisation.listmodels.JoinedPlayerListModel;

/**
 *
 * @author Petr Kozler
 */
public class JoinedPlayerListPanel extends JPanel {

    private final JList<JoinedPlayer> JOINED_PLAYER_LIST_VIEW;
    private final JoinedPlayerListModel JOINED_PLAYER_LIST_MODEL;
    
    public JoinedPlayerListPanel() {
        JOINED_PLAYER_LIST_MODEL = new JoinedPlayerListModel();
        JOINED_PLAYER_LIST_VIEW = new JList<>(JOINED_PLAYER_LIST_MODEL);
    }
    
    public void setJoinedPlayerList(ArrayList<JoinedPlayer> joinedPlayerList) {
        JOINED_PLAYER_LIST_MODEL.setListWithSorting(joinedPlayerList);
    }
    
}
