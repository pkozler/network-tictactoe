package visualisation.components.game;

import interaction.MessageBackgroundSender;
import interaction.sending.requests.LeaveGameRequestBuilder;
import interaction.sending.requests.StartGameRequestBuilder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Petr Kozler
 */
public class LastEventTextPanel extends JPanel {
    
    private JLabel eventLabel;
    private final JButton START_GAME_BUTTON;
    private final JButton LEAVE_GAME_BUTTON;
    private final MessageBackgroundSender MESSAGE_SENDER;
    
    
    public LastEventTextPanel(MessageBackgroundSender messageBackgroundSender) {
        MESSAGE_SENDER = messageBackgroundSender;
        
        eventLabel = new JLabel();
        add(eventLabel, BorderLayout.NORTH);
        START_GAME_BUTTON = new JButton("Nové kolo");
        LEAVE_GAME_BUTTON = new JButton("Odejít ze hry");
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(START_GAME_BUTTON);
        buttonPanel.add(LEAVE_GAME_BUTTON);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setListeners();
    }
    
    private void startGameActionPerformed() {
        MESSAGE_SENDER.enqueueMessageBuilder(new StartGameRequestBuilder());
    }
    
    private void leaveGameActionPerformed() {
        int result = JOptionPane.showConfirmDialog(null,
                "Opravdu chcete opustit herní místnost?", "Opuštění hry", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            MESSAGE_SENDER.enqueueMessageBuilder(new LeaveGameRequestBuilder());
        }
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
    
}
