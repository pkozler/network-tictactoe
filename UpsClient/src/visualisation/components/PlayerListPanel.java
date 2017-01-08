package visualisation.components;

import communication.containers.PlayerInfo;
import interaction.MessageBackgroundSender;
import interaction.sending.requests.LoginRequestBuilder;
import interaction.sending.requests.LogoutRequestBuilder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import visualisation.listmodels.PlayerListModel;

/**
 * Třída PlayerListPanel představuje panel pro zobrazení seznamu hráčů.
 * 
 * @author Petr Kozler
 */
public class PlayerListPanel extends JPanel {

    /**
     * seznam hráčů
     */
    private final JList<PlayerInfo> PLAYER_LIST_VIEW;
    
    /**
     * model seznamu hráčů
     */
    private final PlayerListModel PLAYER_LIST_MODEL;
    
    /**
     * tlačítko přihlášení
     */
    private final JButton LOGIN_BUTTON;
    
    /**
     * tlačítko odhlášení
     */
    private final JButton LOGOUT_BUTTON;
    
    /**
     * vysílač zpráv
     */
    private final MessageBackgroundSender MESSAGE_SENDER;
    
    /**
     * Vytvoří panel seznamu hráčů.
     * 
     * @param messageBackgroundSender vysílač zpráv
     */
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
    
    /**
     * Vrátí seznam hráčů.
     * 
     * @return seznam hráčů
     */
    public JList<PlayerInfo> getPlayerList() {
        return PLAYER_LIST_VIEW;
    }

    /**
     * Nastaví seznam hráčů.
     * 
     * @param playerList seznam hráčů
     */
    public void setPlayerList(ArrayList<PlayerInfo> playerList) {
        PLAYER_LIST_MODEL.setListWithSorting(playerList);
    }

    /**
     * Nastaví listenery pro stisk tlačítek.
     */
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
    
    /**
     * Zpracuje stisk tlačítka pro přihlášení klienta.
     */
    private void loginActionPerformed() {
        String nickname = JOptionPane.showInputDialog(null, "Zadejte přezdívku:", "Přihlášení");
        
        if (nickname != null) {
            MESSAGE_SENDER.enqueueMessageBuilder(new LoginRequestBuilder(nickname));
        }
    }

    /**
     * Zpracuje stisk tlačítka pro odhlášení klienta.
     */
    private void logoutActionPerformed() {
        int result = JOptionPane.showConfirmDialog(null,
                "Opravdu se chcete odhlásit ze serveru?", "Odhlášení", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            MESSAGE_SENDER.enqueueMessageBuilder(new LogoutRequestBuilder());
        }
    }

}
