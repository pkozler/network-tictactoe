package visualisation.components.game;

import communication.containers.GameBoard;
import communication.containers.JoinedPlayer;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import visualisation.listmodels.JoinedPlayerListModel;

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
     * herní pole
     */
    private GameBoard gameBoard;
    
    /**
     * model seznamu přítomných hráčů
     */
    private JoinedPlayerListModel joinedPlayerListModel;
    
    /**
     * pořadí hráče v místnosti
     */
    private byte playerIndex;
    
    /**
     * Vytvoří panel pro zobrazení události v herní místnosti.
     */
    public EventTextPanel() {
        super(new GridLayout(3, 1));
        setPreferredSize(new Dimension(0, 60));
        
        ROUND_LABEL = new JLabel("(Neaktivní)");
        MOVE_LABEL = new JLabel("");
        PLAYER_LABEL = new JLabel("");
        
        add(ROUND_LABEL);
        add(MOVE_LABEL);
        add(PLAYER_LABEL);
    }
    
    /**
     * Nastaví popis místnosti, v níž se klient momentálně nachází.
     * 
     * @param gameBoard herní pole
     * @param joinedPlayerListModel model seznamu přítomných hráčů
     * @param playerIndex pořadí hráče ve hře
     */
    public void setGameDetail(GameBoard gameBoard,
            JoinedPlayerListModel joinedPlayerListModel, byte playerIndex) {
        this.gameBoard = gameBoard;
        this.joinedPlayerListModel = joinedPlayerListModel;
        this.playerIndex = playerIndex;
        
        setLabels();
    }
    
    /**
     * Vrátí text popisku posledního hráče na tahu.
     * 
     * @param player hráč
     * @return popis
     */
    private String getLastPlayerDesc(JoinedPlayer player) {
        if (player == null) {
            return "";
        }
        
        if (player.getCurrentGameIndex() == playerIndex) {
            return String.format("Obsadil jste políčko na souřadnicích [%d; %d]",
                    gameBoard.getLastCellX(), gameBoard.getLastCellY());
        }
        
        return String.format("Hráč %s obsadil políčko na souřadnicích [%d; %d]",
                        player.getNickname(), gameBoard.getLastCellX(), gameBoard.getLastCellY());
    }
    
    /**
     * Vrátí text popisku aktuálního hráče na tahu.
     * 
     * @param player hráč
     * @return popis
     */
    private String getCurrentPlayerDesc(JoinedPlayer player) {
        if (player == null) {
            return "";
        }
        
        if (player.getCurrentGameIndex() == playerIndex) {
            return "Jste na řadě";
        }
        
        return String.format("Na řadě je: %s", player.getNickname());
    }
    
    /**
     * Vrátí text popisku vítězného hráče.
     * 
     * @param player hráč
     * @return popis
     */
    private String getCurrentWinnerDesc(JoinedPlayer player) {
        if (player == null) {
            return "";
        }
        
        if (player.getCurrentGameIndex() == playerIndex) {
            return "Jste vítěz";
        }
        
        return String.format("Vítězem je: %s", player.getNickname());
    }
    
    /**
     * Zobrazí hlášení o aktuálním stavu herní místnosti.
     */
    private void setLabels() {
        // herní místnost nezvolena
        if (this.gameBoard == null) {
            ROUND_LABEL.setText("(Neaktivní)");
            MOVE_LABEL.setText("");
            PLAYER_LABEL.setText("");
            
            return;
        }
        
        // počítadlo kola - pokud je rovno 0, místnost je nově vytvořena
        String roundString = gameBoard.getCurrentRound() < 1 ?
                "Herní místnost otevřena" :
                String.format("%d. kolo", gameBoard.getCurrentRound());
        ROUND_LABEL.setText(roundString);
        
        // čeká se na vstup hráčů nebo zahájení kola
        if (gameBoard.getCurrentRound() < 1) {
            // počet hráčů - pokud není větší než 1, hra nemůže být zahájena
            String playerCounterString = (gameBoard.getPlayerCounter() <= 1 ?
                        "Čeká se na vstup dalšího hráče do místnosti" :
                        "Čeká se na zahájení hry některým z hráčů");
        
            MOVE_LABEL.setText(playerCounterString);
            PLAYER_LABEL.setText("");
            
            return;
        }
        
        JoinedPlayer lastPlaying = joinedPlayerListModel.
                getByGameIndex(gameBoard.getLastPlaying());
        // poslední táhnoucí hráč - pokud není, hra buď skončila, nebo ještě nebyla zahájena
        String moveString = lastPlaying == null ? "Hra zahájena" :
                getLastPlayerDesc(lastPlaying);
        MOVE_LABEL.setText(moveString);
        
        // kolo bylo odehráno
        if (gameBoard.isRoundFinished()) {
            JoinedPlayer currentWinner = joinedPlayerListModel.
                getByGameIndex(gameBoard.getCurrentWinner());
            // vítěz - pokud není, hra skončila remízou nebo ještě nebyla zahájena/odehrána
            String winnerString = "Konec hry - " + (currentWinner == null ? "Remíza" :
                    getCurrentWinnerDesc(currentWinner));

            PLAYER_LABEL.setText(winnerString);
            
            return;
        }
           
        JoinedPlayer currentPlaying = joinedPlayerListModel.
                getByGameIndex(gameBoard.getCurrentPlaying());
        // aktuální hráč na tahu - pokud není, hra buď skončila, nebo ještě nezačala
        String playerString = getCurrentPlayerDesc(currentPlaying);
        PLAYER_LABEL.setText(playerString);
    }
    
}
