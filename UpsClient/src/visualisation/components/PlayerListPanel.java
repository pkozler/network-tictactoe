package visualisation.components;

import communication.containers.PlayerInfo;
import java.awt.BorderLayout;
import java.util.ArrayList;
import javax.swing.JList;
import javax.swing.JPanel;
import visualisation.listmodels.PlayerListModel;

/**
 *
 * @author Petr Kozler
 */
public class PlayerListPanel extends JPanel {

    private final JList<PlayerInfo> PLAYER_LIST_VIEW;
    private final PlayerListModel PLAYER_LIST_MODEL;
    
    public PlayerListPanel() {
        super(new BorderLayout());
        
        PLAYER_LIST_MODEL = new PlayerListModel();
        PLAYER_LIST_VIEW = new JList<>(new PlayerListModel());
    }
    
    public JList<PlayerInfo> getPlayerList() {
        return PLAYER_LIST_VIEW;
    }

    public void setPlayerList(ArrayList<PlayerInfo> playerList) {
        PLAYER_LIST_MODEL.setListWithSorting(playerList);
    }

}
