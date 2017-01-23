package visualisation.components;

import communication.TcpClient;
import interaction.MessageBackgroundSender;
import interaction.sending.requests.LeaveGameRequestBuilder;
import interaction.sending.requests.StartGameRequestBuilder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import communication.containers.CurrentGameDetail;
import java.util.Observable;
import java.util.Observer;
import javax.swing.SwingUtilities;
import visualisation.components.game.BoardPanel;
import visualisation.components.game.JoinedPlayerListPanel;
import visualisation.components.game.EventTextPanel;

/**
 * Třída CurrentGamePanel představuje panel pro zobrazení stavu aktuální
 * herní místnosti.
 * 
 * @author Petr Kozler
 */
public class CurrentGamePanel extends JPanel implements Observer {

    /**
     * panel herního pole
     */
    private final BoardPanel BOARD_PANEL;
    
    /**
     * panel hráčů v herní místnosti
     */
    private final JoinedPlayerListPanel JOINED_PLAYER_LIST_PANEL;
    
    /**
     * panel událostí hry
     */
    private final EventTextPanel LAST_EVENT_TEXT_PANEL;
    
    /**
     * tlačítko zahájení herního kola
     */
    private final JButton START_GAME_BUTTON;
    
    /**
     * tlačítko opuštění herní místnosti
     */
    private final JButton LEAVE_GAME_BUTTON;
    
    /**
     * vysílač zpráv
     */
    private final MessageBackgroundSender MESSAGE_SENDER;
    
    /**
     * Vytvoří panel herní místnosti.
     * 
     * @param messageBackgroundSender vysílač zpráv
     */
    public CurrentGamePanel(MessageBackgroundSender messageBackgroundSender) {
        super(new BorderLayout());
        setPreferredSize(new Dimension(240, 0));
        setBorder(BorderFactory.createTitledBorder("Stav hry"));
        
        MESSAGE_SENDER = messageBackgroundSender;
        START_GAME_BUTTON = new JButton("Nové kolo");
        LEAVE_GAME_BUTTON = new JButton("Odejít");
        BOARD_PANEL = new BoardPanel(messageBackgroundSender);
        JOINED_PLAYER_LIST_PANEL = new JoinedPlayerListPanel();
        LAST_EVENT_TEXT_PANEL = new EventTextPanel();
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(START_GAME_BUTTON);
        buttonPanel.add(LEAVE_GAME_BUTTON);
        
        JPanel insetPanel = new JPanel();
        insetPanel.setPreferredSize(new Dimension(10, 0));
        
        add(LAST_EVENT_TEXT_PANEL, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
        add(insetPanel, BorderLayout.WEST);
        add(JOINED_PLAYER_LIST_PANEL, BorderLayout.EAST);
        add(BOARD_PANEL, BorderLayout.CENTER);
        
        setListeners();
        setButtons(false, false, false, false);
    }
    
    /**
     * Zpracuje stisk tlačítka pro zahájení nového kola hry.
     */
    private void startGameActionPerformed() {
        MESSAGE_SENDER.enqueueMessageBuilder(new StartGameRequestBuilder());
    }
    
    /**
     * Zpracuje stisk tlačítka pro opuštění herní místnosti.
     */
    private void leaveGameActionPerformed() {
        int result = JOptionPane.showConfirmDialog(null,
                "Opravdu chcete opustit herní místnost?", "Opuštění hry", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            MESSAGE_SENDER.enqueueMessageBuilder(new LeaveGameRequestBuilder());
        }
    }
    
    /**
     * Nastaví listenery pro stisk tlačítek.
     */
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
    
    /**
     * Nastaví aktivaci tlačítek.
     * 
     * @param connected příznak připojení
     * @param loggedIn příznak přihlášení
     * @param inGame příznak přítomnosti ve hře
     * @param roundFinished příznak dohrání aktuálního kola hry
     */
    public void setButtons(boolean connected, boolean loggedIn, boolean inGame,
            boolean roundFinished) {
        LEAVE_GAME_BUTTON.setEnabled(connected && loggedIn && inGame);
        START_GAME_BUTTON.setEnabled(connected && loggedIn && inGame && roundFinished);
    }

    @Override
    public void update(Observable o, Object o1) {
        final TcpClient client = (TcpClient) o;
        CurrentGameDetail currentGameDetail = client.getGameDetail();
        final CurrentGameDetail gameDetail = currentGameDetail == null ?
                new CurrentGameDetail(null, null) : currentGameDetail;
        
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                BOARD_PANEL.setGameDetail(gameDetail);
                JOINED_PLAYER_LIST_PANEL.setGameDetail(gameDetail);
                LAST_EVENT_TEXT_PANEL.setGameDetail(gameDetail);

                setButtons(client.isConnected(), client.hasPlayerInfo(), client.hasGameInfo(),
                        gameDetail.GAME_BOARD != null && gameDetail.GAME_BOARD.isRoundFinished());
            }
            
        });
    }
    
}
