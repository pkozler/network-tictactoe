package visualisation.components.game.icons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;

/**
 *
 * @author Petr Kozler
 */
public class CircleIcon implements Icon {
    
    private final int SIZE;
    
    public CircleIcon(int size) {
        SIZE = size;
    }
    
    @Override
    public void paintIcon(Component cmpnt, Graphics grphcs, int i, int i1) {
        Graphics2D g2d = (Graphics2D) grphcs;
        draw(g2d, SIZE);
    }

    @Override
    public int getIconWidth() {
        return SIZE;
    }

    @Override
    public int getIconHeight() {
        return SIZE;
    }
    
    /**
     * Zvýrazní políčko.
     * 
     * @param g2d grafický kontext
     * @param x souřadnice X
     * @param y souřadnice Y
     * @param size velikost v pixelech
     */
    public static void highlight(Graphics2D g2d, int x, int y, int size) {
        g2d.setColor(new Color(192, 192, 255));
        g2d.fillRect(x, y, size, size);
    }
    
    /**
     * Nakreslí symbol do políčka.
     * 
     * @param g2d grafický kontext
     * @param size velikost v pixelech
     */
    public static void draw(Graphics2D g2d, int size) {
        g2d.setColor(new Color(0, 0, 192));
        g2d.drawOval(0, 0, size, size);
    }
    
}
