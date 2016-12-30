package visualisation.components;

import communication.containers.PlayerInfo;
import interaction.MessageBackgroundSender;
import interaction.sending.requests.ActivationRequestBuilder;
import interaction.sending.requests.DeactivationRequestBuilder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
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
    private final JButton LOGIN_BUTTON;
    private final JButton LOGOUT_BUTTON;
    private final MessageBackgroundSender MESSAGE_SENDER;
    
    public PlayerListPanel(MessageBackgroundSender messageBackgroundSender) {
        super(new BorderLayout());
        
        MESSAGE_SENDER = messageBackgroundSender;
        PLAYER_LIST_MODEL = new PlayerListModel();
        PLAYER_LIST_VIEW = new JList<>(new PlayerListModel());
        
        LOGIN_BUTTON = new JButton("Přihlásit se");
        LOGOUT_BUTTON = new JButton("Odhlásit");
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(LOGIN_BUTTON);
        buttonPanel.add(LOGOUT_BUTTON);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setListeners();
    }
    
    public JList<PlayerInfo> getPlayerList() {
        return PLAYER_LIST_VIEW;
    }

    public void setPlayerList(ArrayList<PlayerInfo> playerList) {
        PLAYER_LIST_MODEL.setListWithSorting(playerList);
    }

    private void setListeners() {
        LOGIN_BUTTON.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                loginActionPerformed();
            }

        });
        
        LOGOUT_BUTTON.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                logoutActionPerformed();
            }

        });
    }
    
    private void loginActionPerformed() {
        String nickname = null;
        
        // TODO vytvořit dialog a načíst hodnotu
        
        MESSAGE_SENDER.enqueueMessageBuilder(new ActivationRequestBuilder(nickname));
    }

    private void logoutActionPerformed() {
        MESSAGE_SENDER.enqueueMessageBuilder(new DeactivationRequestBuilder());
    }

}
