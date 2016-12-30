package visualisation.components;

import communication.containers.GameInfo;
import interaction.MessageBackgroundSender;
import interaction.sending.requests.CreateGameRequestBuilder;
import interaction.sending.requests.JoinGameRequestBuilder;
import interaction.sending.requests.LeaveGameRequestBuilder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.DefaultListSelectionModel;
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
    private final JButton LEAVE_GAME_BUTTON;
    private final MessageBackgroundSender MESSAGE_SENDER;
    
    public GameListPanel(MessageBackgroundSender messageBackgroundSender) {
        super(new BorderLayout());
        
        MESSAGE_SENDER = messageBackgroundSender;
        GAME_LIST_MODEL = new GameListModel();
        GAME_LIST_VIEW = new JList<>(GAME_LIST_MODEL);
        GAME_LIST_VIEW.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        
        CREATE_GAME_BUTTON = new JButton("Vytvořit hru");
        JOIN_GAME_BUTTON = new JButton("Vstoupit do hry");
        LEAVE_GAME_BUTTON = new JButton("Odejít ze hry");
        setButtons(false);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(CREATE_GAME_BUTTON);
        buttonPanel.add(JOIN_GAME_BUTTON);
        buttonPanel.add(LEAVE_GAME_BUTTON);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setListeners();
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
        
        LEAVE_GAME_BUTTON.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                leaveGameActionPerformed();
            }
            
        });
    }
    
    private void createGameActionPerformed() {
        String name = null;
        byte playerCount = 0;
        byte boardSize = 0;
        byte cellCount = 0;
        
        // TODO vytvořit dialog a načíst hodnoty
        
        MESSAGE_SENDER.enqueueMessageBuilder(new CreateGameRequestBuilder(
                name, playerCount, boardSize, cellCount));
    }
    
    private void joinGameActionPerformed() {
        int gameId = GAME_LIST_VIEW.getSelectedValue().ID;
        MESSAGE_SENDER.enqueueMessageBuilder(new JoinGameRequestBuilder(gameId));
    }
    
    private void leaveGameActionPerformed() {
        MESSAGE_SENDER.enqueueMessageBuilder(new LeaveGameRequestBuilder());
    }
    
    public void setButtons(boolean enabled) {
        CREATE_GAME_BUTTON.setEnabled(enabled);
        JOIN_GAME_BUTTON.setEnabled(enabled);
        LEAVE_GAME_BUTTON.setEnabled(enabled);
    }

}
