package visualisation.components.game;

import communication.containers.GameBoard;
import communication.containers.JoinedPlayer;
import interaction.MessageBackgroundSender;
import interaction.sending.requests.LeaveGameRequestBuilder;
import interaction.sending.requests.StartGameRequestBuilder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Třída EventTextPanel představuje panel pro zobrazení
 poslední události v aktuální herní místnosti.
 * 
 * @author Petr Kozler
 */
public class EventTextPanel extends JPanel {
    
    /**
     * hlášení počtu odehraných kol
     */
    private final JLabel ROUND_LABEL;
    
    /**
     * hlášení posledního tahu
     */
    private final JLabel MOVE_LABEL;
    
    /**
     * hlášení dalšího tahu nebo konce hry
     */
    private final JLabel PLAYER_LABEL;
    
    /**
     * hráči v herní místnosti
     */
    public ArrayList<JoinedPlayer> joinedPlayers;
    
    /**
     * herní pole
     */
    public GameBoard gameBoard;
    
    /**
     * Vytvoří panel pro zobrazení události v herní místnosti.
     * 
     * @param messageBackgroundSender vysílač zpráv
     */
    public EventTextPanel() {
        super(new GridLayout(3, 1));
        setPreferredSize(new Dimension(0, 60));
        
        ROUND_LABEL = new JLabel("(neaktivní)");
        MOVE_LABEL = new JLabel("");
        PLAYER_LABEL = new JLabel("");
        
        add(ROUND_LABEL);
        add(MOVE_LABEL);
        add(PLAYER_LABEL);
    }
    
    /**
     * Nastaví herní pole místnosti, v níž se klient momentálně nachází.
     * 
     * @param joinedPlayers seznam hráčů
     * @param gameBoard herní pole
     */
    public void setCurrentGame(ArrayList<JoinedPlayer> joinedPlayers, GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        this.joinedPlayers = joinedPlayers;
        
        if (this.gameBoard == null) {
            ROUND_LABEL.setText("neaktivní");
            MOVE_LABEL.setText("");
            PLAYER_LABEL.setText("");
            
            return;
        }
        
        setLabels();
    }
    
    /**
     * Zobrazí hlášení o aktuálním stavu herní místnosti.
     */
    private void setLabels() {
        String roundString = String.format("%d. kolo (%s)", gameBoard.getCurrentRound(),
                gameBoard.isRoundFinished() ? "ukončeno" : "hraje se");
        
        JoinedPlayer lastPlaying = getJoinedPlayerFromList(gameBoard.getLastPlaying());
        String lastPlayerNicknameSubstring = lastPlaying != null ?
                String.format("Hráč %s táhl na souřadnicích [%d; %d].",
                        lastPlaying.PLAYER_INFO.NICK, gameBoard.getLastCellX(), gameBoard.getLastCellY()) :
                "Čeká se na dalšího hráče hráče.";
        String moveString = gameBoard.getLastPlaying() == 0 ? "Hra zahájena." : lastPlayerNicknameSubstring;
        
        String winnerNicknameSubstring = gameBoard.getCurrentWinner() != 0 ?
                String.format("Vítězem je hráč %s.", gameBoard.getCurrentWinner()) : "Remíza";
        String roundFinishedSubstring = String.format("Konec hry. Výsledek: ", winnerNicknameSubstring);
        String playerString = gameBoard.isRoundFinished() ? roundFinishedSubstring :
                String.format("Na řadě je hráč %s.", gameBoard.getCurrentPlaying());
        
        ROUND_LABEL.setText(roundString);
        MOVE_LABEL.setText(moveString);
        PLAYER_LABEL.setText(playerString);
    }
    
    /**
     * Získá hráče ze seznamu podle pořadí v aktuální herní místnosti.
     * 
     * @param index pořadí
     * @return hráč
     */
    private JoinedPlayer getJoinedPlayerFromList(byte index) {
        for (JoinedPlayer j : joinedPlayers) {
            if (j.getCurrentGameIndex() == index) {
                return j;
            }
        }
        
        return null;
    }
    
}
