package visualisation.components.game;

import communication.containers.GameBoard;
import interaction.MessageBackgroundSender;
import interaction.sending.requests.PlayGameRequestBuilder;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

/**
 * Třída BoardPanel představuje panel pro zobrazení herního pole
 * aktuální herní místnosti.
 * 
 * @author Petr Kozler
 */
public class BoardPanel extends JPanel {

    /**
     * vysílač zpráv
     */
    private final MessageBackgroundSender MESSAGE_SENDER;
    
    /**
     * herní pole
     */
    private GameBoard gameBoard;
    
    /**
     * Vytvoří panel herního pole.
     * 
     * @param messageBackgroundSender vysílač zpráv
     */
    public BoardPanel(MessageBackgroundSender messageBackgroundSender) {
        MESSAGE_SENDER = messageBackgroundSender;
        setListeners();
    }
    
    /**
     * Nastaví herní pole.
     * 
     * @param gameBoard herní pole
     */
    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        repaint();
    }
    
    /**
     * Nastaví listenery pro tlačítka.
     */
    private void setListeners() {
        addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e) {
                playGameActionPerformed(e);
            }
            
        });
    }
    
    /**
     * Zpracuje stisk políčka v herním poli.
     * 
     * @param e událost stisku myši
     */
    private void playGameActionPerformed(MouseEvent e) {
        byte x = 0;
        byte y = 0;
        
        int boardSizeInCells = gameBoard.GAME_INFO.BOARD_SIZE;
        int canvasSize = getWidth() < getHeight() ? getWidth() : getHeight();
        int cellSize = canvasSize / boardSizeInCells;
        
        for (int i = 0; i < canvasSize; i += cellSize) {
            if (e.getX() < i + cellSize) {
                x = (byte) i;
                
                return;
            }
        }
        
        for (int i = 0; i < canvasSize; i += cellSize) {
            if (e.getY() < i + cellSize) {
                y = (byte) i;
                
                return;
            }
        }
        
        MESSAGE_SENDER.enqueueMessageBuilder(new PlayGameRequestBuilder(x, y));
    }

    /**
     * Překreslí plátno pro zobrazení herního pole.
     * 
     * @param g grafický kontext
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        
        if (gameBoard == null) {
            return;
        }
        
        int boardSizeInCells = gameBoard.GAME_INFO.BOARD_SIZE;
        int canvasSize = getWidth() < getHeight() ? getWidth() : getHeight();
        int cellSize = canvasSize / boardSizeInCells;
        
        for (int i = 0; i < boardSizeInCells; i += cellSize) {
            g2d.drawLine(0, i, canvasSize, i);
        }
        
        for (int i = 0; i < boardSizeInCells; i += cellSize) {
            g2d.drawLine(i, 0, i, canvasSize);
        }
        
        // TODO nakreslit obsazení políček podle informací z objektu GameBoard
        
        // TODO nakreslit čáru označující vítězná políčka (pokud jsou)
    }
    
}
