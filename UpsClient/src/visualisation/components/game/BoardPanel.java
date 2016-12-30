package visualisation.components.game;

import communication.containers.GameBoard;
import interaction.MessageBackgroundSender;
import interaction.sending.requests.PlayGameRequestBuilder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

/**
 *
 * @author Petr Kozler
 */
public class BoardPanel extends JPanel {

    private final MessageBackgroundSender MESSAGE_SENDER;
    
    private GameBoard gameBoard;
    
    public BoardPanel(MessageBackgroundSender messageBackgroundSender) {
        MESSAGE_SENDER = messageBackgroundSender;
    }
    
    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }
    
    private void setListeners() {
        addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e) {
                playGameActionPerformed(e);
            }
            
        });
    }
    
    private void playGameActionPerformed(MouseEvent e) {
        byte x = 0;
        byte y = 0;
        
        // TODO určit indexy políčka podle souřadnic kliku myši do plátna
        
        MESSAGE_SENDER.enqueueMessageBuilder(new PlayGameRequestBuilder(x, y));
    }

}
