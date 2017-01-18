package visualisation.components;

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
import visualisation.CurrentGameDetail;
import visualisation.components.game.BoardPanel;
import visualisation.components.game.JoinedPlayerListPanel;
import visualisation.components.game.EventTextPanel;

/**
 * Třída CurrentGamePanel představuje panel pro zobrazení stavu aktuální
 * herní místnosti.
 * 
 * @author Petr Kozler
 */
public class CurrentGamePanel extends JPanel {

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
        setButtons(false, false);
    }
    
    /**
     * Zpracuje stisk tlačítka pro zahájení nového kola hry.
     */
    private void startGameActionPerformed() {
        if (!MESSAGE_SENDER.CLIENT.isConnected() ||
                !MESSAGE_SENDER.CLIENT.isLogged() ||
                !MESSAGE_SENDER.CLIENT.isInGame()) {
            return;
        }
        
        MESSAGE_SENDER.enqueueMessageBuilder(new StartGameRequestBuilder());
    }
    
    /**
     * Zpracuje stisk tlačítka pro opuštění herní místnosti.
     */
    private void leaveGameActionPerformed() {
        if (!MESSAGE_SENDER.CLIENT.isConnected() ||
                !MESSAGE_SENDER.CLIENT.isLogged() ||
                !MESSAGE_SENDER.CLIENT.isInGame()) {
            return;
        }
        
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
     * @param inGame příznak aktivace (přítomnost hráče v herní místnosti)
     * @param roundFinished příznak aktivace (ukončení aktuálního kola hry)
     */
    public void setButtons(boolean inGame, boolean roundFinished) {
        if (!inGame) {
            START_GAME_BUTTON.setEnabled(false);
            LEAVE_GAME_BUTTON.setEnabled(false);
            
            return;
        }
        
        LEAVE_GAME_BUTTON.setEnabled(true);
        
        if (!roundFinished) {
            START_GAME_BUTTON.setEnabled(false);
            
            return;
        }
        
        START_GAME_BUTTON.setEnabled(true);
    }

    /**
     * Nastaví stav herní místnosti.
     * 
     * @param currentGameDetail stav herní místnosti
     */
    public void setGameDetail(CurrentGameDetail currentGameDetail) {
        if (currentGameDetail.GAME_BOARD == null) {
            setButtons(false, false);
        }
        
        BOARD_PANEL.setGameBoard(currentGameDetail.GAME_BOARD);
        JOINED_PLAYER_LIST_PANEL.setJoinedPlayerList(currentGameDetail.JOINED_PLAYERS);
        LAST_EVENT_TEXT_PANEL.setCurrentGame(currentGameDetail.JOINED_PLAYERS, currentGameDetail.GAME_BOARD);
        
        setButtons(true, currentGameDetail.GAME_BOARD.isRoundFinished());
    }
    
}
