package visualisation.components;

import communication.containers.GameInfo;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import visualisation.listmodels.GameListModel;

/**
 *
 * @author Petr Kozler
 */
public class GameListPanel extends JPanel {

    private final JList<GameInfo> GAME_LIST_VIEW;
    private final GameListModel GAME_LIST_MODEL;
    private final JButton CREATE_GAME_BUTTON;
    private final JButton JOIN_GAME_BUTTON;
    
    public GameListPanel() {
        super(new BorderLayout());
        
        GAME_LIST_MODEL = new GameListModel();
        GAME_LIST_VIEW = new JList<>(GAME_LIST_MODEL);
        
        CREATE_GAME_BUTTON = new JButton("Vytvořit hru");
        JOIN_GAME_BUTTON = new JButton("Připojit ke hře");
        
        setListeners();
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(CREATE_GAME_BUTTON);
        buttonPanel.add(JOIN_GAME_BUTTON);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    public JList<GameInfo> getGameList() {
        return GAME_LIST_VIEW;
    }

    public void setGameList(ArrayList<GameInfo> gameList) {
        GAME_LIST_MODEL.setListWithSorting(gameList);
    }
    
    private void setListeners() {
        CREATE_GAME_BUTTON.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                createGameActionPerformed();
            }
            
        });
        
        JOIN_GAME_BUTTON.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                joinGameActionPerformed();
            }
            
        });
    }
    
    private void createGameActionPerformed() {
        
    }
    
    private void joinGameActionPerformed() {
        
    }

}