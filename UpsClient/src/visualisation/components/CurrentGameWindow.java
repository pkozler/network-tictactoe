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
    
    public CurrentGameWindow(MessageBackgroundSender messageBackgroundSender) {
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        
        BOARD_PANEL = new BoardPanel(messageBackgroundSender);
        JOINED_PLAYER_LIST_PANEL = new JoinedPlayerListPanel();
        
        contentPane.add(BOARD_PANEL, BorderLayout.CENTER);
        contentPane.add(JOINED_PLAYER_LIST_PANEL, BorderLayout.NORTH);
        
        setContentPane(contentPane);
    }
    
    public void setGameDetail(CurrentGameDetail currentGameDetail) {
        BOARD_PANEL.setGameBoard(currentGameDetail.GAME_BOARD);
        JOINED_PLAYER_LIST_PANEL.setJoinedPlayerList(currentGameDetail.JOINED_PLAYERS);
    }
    
}
