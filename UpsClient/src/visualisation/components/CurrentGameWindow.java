package visualisation.components;

import interaction.MessageBackgroundSender;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import visualisation.CurrentGameDetail;
import visualisation.components.game.BoardPanel;
import visualisation.components.game.JoinedPlayerListPanel;

/**
 *
 * @author Petr Kozler
 */
public class CurrentGameWindow extends JFrame {

    private final BoardPanel BOARD_PANEL;
    private final JoinedPlayerListPanel JOINED_PLAYER_LIST_PANEL;
    private final JButton START_GAME_BUTTON;
    private final JButton LEAVE_GAME_BUTTON;
    private final MessageBackgroundSender MESSAGE_SENDER;
    
    public CurrentGameWindow(MessageBackgroundSender messageBackgroundSender) {
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        
        MESSAGE_SENDER = messageBackgroundSender;
        BOARD_PANEL = new BoardPanel();
        JOINED_PLAYER_LIST_PANEL = new JoinedPlayerListPanel();
        
        contentPane.add(BOARD_PANEL, BorderLayout.CENTER);
        contentPane.add(JOINED_PLAYER_LIST_PANEL, BorderLayout.NORTH);
        
        START_GAME_BUTTON = new JButton("Zahájit hru");
        LEAVE_GAME_BUTTON = new JButton("Odpojit ze hry");
        
        setListeners();
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(START_GAME_BUTTON);
        buttonPanel.add(LEAVE_GAME_BUTTON);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(contentPane);
    }
    
    public void setGameDetail(CurrentGameDetail currentGameDetail) {
        BOARD_PANEL.setGameBoard(currentGameDetail.GAME_BOARD);
        JOINED_PLAYER_LIST_PANEL.setJoinedPlayerList(currentGameDetail.JOINED_PLAYERS);
    }
    
    private void setListeners() {
        START_GAME_BUTTON.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                startGameActionPerformed();
            }
            
        });
        
        LEAVE_GAME_BUTTON.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                leaveGameActionPerformed();
            }
            
        });
    }
    
    private void startGameActionPerformed() {
        
    }
    
    private void leaveGameActionPerformed() {
        
    }
    
}
