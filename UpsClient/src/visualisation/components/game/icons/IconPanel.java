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
 *
 * @author Petr Kozler
 */
public class IconPanel extends JPanel {

    private final Icon ICON;
    
    private final int SIZE;
    
    public IconPanel(Icon icon, int size) {
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        ICON = icon;
        SIZE = size;
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        
        int margin = 20;
        g2d.translate(margin, margin);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(-(margin / 2), -(margin / 2), SIZE + margin, SIZE + margin);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(-(margin / 2), -(margin / 2), SIZE + margin, SIZE + margin);
        g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        ICON.paintIcon(this, g, 0, 0);
    }
    
}
