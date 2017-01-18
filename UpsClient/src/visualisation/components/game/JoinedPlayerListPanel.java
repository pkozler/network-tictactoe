package visualisation.components.game;

import communication.containers.JoinedPlayer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.BorderFactory;
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
     * Vytvoří panel pro zobrazení seznamu hráčů v herní místnosti.
     */
    public JoinedPlayerListPanel() {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setPreferredSize(new Dimension(240, 0));
        
        JOINED_PLAYER_LIST_VIEW = new JList<>();
        JOINED_PLAYER_LIST_VIEW.setBackground(getBackground());
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Hráči ve hře:"));
        listPanel.add(JOINED_PLAYER_LIST_VIEW, BorderLayout.CENTER);
        add(listPanel, BorderLayout.CENTER);
    }
    
    /**
     * Nastaví seznam hráčů v herní místnosti.
     * 
     * @param joinedPlayerList seznam hráčů v herní místnosti
     */
    public void setJoinedPlayerList(ArrayList<JoinedPlayer> joinedPlayerList) {
        JoinedPlayerListModel joinedPlayerListModel = new JoinedPlayerListModel();
        joinedPlayerListModel.setListWithSorting(joinedPlayerList);
        JOINED_PLAYER_LIST_VIEW.setModel(joinedPlayerListModel);
    }
    
}
