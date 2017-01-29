package visualisation.components.game;

import communication.containers.GameBoard;
import communication.containers.JoinedPlayer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import visualisation.components.game.icons.CircleIcon;
import visualisation.components.game.icons.CrossIcon;
import visualisation.components.game.icons.IconPanel;
import visualisation.components.game.icons.TildeIcon;
import visualisation.components.game.icons.YpsilonIcon;
import visualisation.listmodels.JoinedPlayerListModel;

/**
 * Třída JoinedPlayerListPanel představuje panel pro zobrazení
 * seznamu hráčů v aktuální herní místnosti.
 * 
 * @author Petr Kozler
 */
public class JoinedPlayerListPanel extends JPanel {

    private final int FONT_SIZE = 16;
    
    private final Icon[] PLAYER_ICONS = new Icon[] {
        new CrossIcon(FONT_SIZE),
        new CircleIcon(FONT_SIZE),
        new YpsilonIcon(FONT_SIZE),
        new TildeIcon(FONT_SIZE)
    };
    
    /**
     * seznam zpráv v herní místnosti
     */
    private final JList<JoinedPlayer> JOINED_PLAYER_LIST_VIEW;
    
    /**
     * herní pole
     */
    public GameBoard gameBoard;
    
    /**
     * pořadí aktuálního hráče na tahu
     */
    private byte currentPlayingIndex;
    
    /**
     * Vytvoří panel pro zobrazení seznamu hráčů v herní místnosti.
     */
    public JoinedPlayerListPanel() {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setPreferredSize(new Dimension(300, 0));
        
        JOINED_PLAYER_LIST_VIEW = new JList<>();
        
        // TEST
        /*ArrayList<JoinedPlayer> list = new ArrayList<>();
        list.add(new JoinedPlayer("Franta", (byte)1, (byte)0));
        list.add(new JoinedPlayer("Pepa", (byte)2, (byte)0));
        list.add(new JoinedPlayer("Venca", (byte)3, (byte)0));
        list.add(new JoinedPlayer("Jarda", (byte)4, (byte)0));
        JoinedPlayerListModel joinedPlayerListModel = new JoinedPlayerListModel();
        joinedPlayerListModel.setListWithSorting(list);
        JOINED_PLAYER_LIST_VIEW.setModel(joinedPlayerListModel);*/
        
        JOINED_PLAYER_LIST_VIEW.setCellRenderer(getRenderer());
        JOINED_PLAYER_LIST_VIEW.setBackground(getBackground());
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Hráči ve hře:"));
        listPanel.add(JOINED_PLAYER_LIST_VIEW, BorderLayout.CENTER);
        add(listPanel, BorderLayout.CENTER);
    }
    
    private ListCellRenderer<? super JoinedPlayer> getRenderer() {
        return new DefaultListCellRenderer() {
            
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                    Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                byte playerItemIndex = ((JoinedPlayer) value).getCurrentGameIndex();
                Icon playerIcon = PLAYER_ICONS[playerItemIndex - (byte) 1];
                
                JLabel listCellRendererComponent = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                listCellRendererComponent.setFont(
                        new Font(Font.MONOSPACED, Font.BOLD, FONT_SIZE));
                listCellRendererComponent.setBorder(BorderFactory.createEmptyBorder(
                        5, 5, 5, 5));
                
                JPanel panel = new IconPanel(playerIcon, FONT_SIZE);
                panel.add(listCellRendererComponent);
                
                if (playerItemIndex == currentPlayingIndex) {
                    panel.setBorder(BorderFactory.createRaisedSoftBevelBorder());
                }
                
                return panel;
            }
            
        };
    }
    
    /**
     * Nastaví herní pole místnosti, v níž se klient momentálně nachází.
     * 
     * @param gameBoard herní pole
     * @param joinedPlayerListModel model seznamu přítomných hráčů
     */
    public void setGameDetail(GameBoard gameBoard,
            JoinedPlayerListModel joinedPlayerListModel) {
        this.gameBoard = gameBoard;
        this.currentPlayingIndex = gameBoard != null ?
                gameBoard.getCurrentPlaying() : (byte) 0;
        
        JOINED_PLAYER_LIST_VIEW.setModel(joinedPlayerListModel);
    }
    
}
