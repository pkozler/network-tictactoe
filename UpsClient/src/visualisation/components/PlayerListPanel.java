package visualisation.components;

import communication.TcpClient;
import communication.containers.PlayerInfo;
import configuration.Protocol;
import interaction.MessageBackgroundSender;
import interaction.sending.requests.LoginRequestBuilder;
import interaction.sending.requests.LogoutRequestBuilder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import visualisation.listmodels.PlayerListModel;

/**
 * Třída PlayerListPanel představuje panel pro zobrazení seznamu hráčů.
 * 
 * @author Petr Kozler
 */
public class PlayerListPanel extends JPanel implements Observer {

    /**
     * seznam hráčů
     */
    private final JList<PlayerInfo> PLAYER_LIST_VIEW;
    
    /**
     * model seznamu hráčů
     */
    //private final PlayerListModel PLAYER_LIST_MODEL;
    
    /**
     * popisek stavu přihlášení hráče
     */
    private final JLabel PLAYER_LABEL;
    
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
        setPreferredSize(new Dimension(240, 0));
        setBorder(BorderFactory.createTitledBorder("Přihlášení hráči"));
        
        MESSAGE_SENDER = messageBackgroundSender;
        PLAYER_LIST_VIEW = new JList<>();
        PLAYER_LIST_VIEW.setBorder(BorderFactory.createLoweredBevelBorder());
        PLAYER_LIST_VIEW.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        PLAYER_LIST_VIEW.setCellRenderer(getRenderer());
        
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        listPanel.add(PLAYER_LIST_VIEW, BorderLayout.CENTER);
        
        PLAYER_LABEL = new JLabel("Hráč nepřihlášen");
        LOGIN_BUTTON = new JButton("Přihlásit");
        LOGOUT_BUTTON = new JButton("Odhlásit");
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(LOGIN_BUTTON);
        buttonPanel.add(LOGOUT_BUTTON);
        
        JPanel labelPanel = new JPanel(new FlowLayout());
        labelPanel.add(PLAYER_LABEL);
        
        add(labelPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
        add(listPanel, BorderLayout.CENTER);
        
        setListeners();
        setButtons(false, false);
    }
    
    private ListCellRenderer<? super PlayerInfo> getRenderer() {
        return new DefaultListCellRenderer() {
            
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                    Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                JLabel listCellRendererComponent = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,cellHasFocus);
                listCellRendererComponent.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
                return listCellRendererComponent;
            }
            
        };
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
        int number = PLAYER_LIST_VIEW.getModel() == null ? 1 :
                PLAYER_LIST_VIEW.getModel().getSize() + 1;
        String nickname = JOptionPane.showInputDialog(null, "Zadejte nickname:", "Hrac" + number);
        
        if (nickname == null) {
            return;
        }
        
        if (nickname.contains(Protocol.SEPARATOR)) {
            JOptionPane.showMessageDialog(null, "Nickname hráče nesmí obsahovat znak \""
                    + Protocol.SEPARATOR + "\".", "Neplatný vstup", JOptionPane.ERROR_MESSAGE);
            
            return;
        }
        
        MESSAGE_SENDER.enqueueMessageBuilder(new LoginRequestBuilder(nickname));
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

    /**
     * Nastaví aktivaci tlačítek.
     * 
     * @param connected příznak připojení
     * @param loggedIn příznak přihlášení
     */
    public void setButtons(boolean connected, boolean loggedIn) {
        LOGIN_BUTTON.setEnabled(connected && !loggedIn);
        LOGOUT_BUTTON.setEnabled(connected && loggedIn);
    }

    @Override
    public void update(Observable o, Object o1) {
        TcpClient client = (TcpClient) o;
        ArrayList<PlayerInfo> playerList = client.getPlayerList();
        
        if (playerList == null) {
            playerList = new ArrayList<>();
        }
        
        PlayerListModel playerListModel = new PlayerListModel();
        playerListModel.setListWithSorting(playerList);
        PLAYER_LIST_VIEW.setModel(playerListModel);
        
        if (client.isConnected() && client.hasPlayerInfo()) {
            PLAYER_LABEL.setText(String.format(
                "<html>Přihlášen jako:<br />%s (ID %d)</html>",
                client.getPlayerInfo().NICK, client.getPlayerInfo().ID));
        }
        else {
            PLAYER_LABEL.setText("Nepřihlášen");
        }
        
        setButtons(client.isConnected(), client.hasPlayerInfo());
    }

}
