package visualisation.components.game.icons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JPanel;

/**
 * Třída IconPanel představuje panel s ikonou symbolu pro označení
 * políčka obsazeného příslušným hráčem, která se zobrazuje u položky
 * tohoto hráče v seznamu hráčů přítomných v herní místnosti.
 * 
 * @author Petr Kozler
 */
public class IconPanel extends JPanel {

    /**
     * ikona symbolu
     */
    private final Icon ICON;
    
    /**
     * rozměr ikony
     */
    private final int SIZE;
    
    /**
     * Vytvoří panel s ikonou.
     * 
     * @param icon ikona
     * @param size rozměr
     */
    public IconPanel(Icon icon, int size) {
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        ICON = icon;
        SIZE = size;
    }
    
    /**
     * Vykreslí ikonu symbolu pro označení obsazených políček.
     * 
     * @param g grafický kontext
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // vykreslení pozadí a nastavení parametrů pro kreslení symbolu
        int margin = 20;
        g2d.translate(margin, margin);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(-(margin / 2), -(margin / 2), SIZE + margin, SIZE + margin);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(-(margin / 2), -(margin / 2), SIZE + margin, SIZE + margin);
        g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        // vykreslení symbolu
        ICON.paintIcon(this, g, 0, 0);
    }
    
}
