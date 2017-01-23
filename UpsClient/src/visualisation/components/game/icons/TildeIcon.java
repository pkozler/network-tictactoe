package visualisation.components.game.icons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import javax.swing.Icon;

/**
 *
 * @author Petr Kozler
 */
public class TildeIcon implements Icon {
    
    private final int SIZE;
    
    public TildeIcon(int size) {
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
        g2d.setColor(new Color(255, 205, 155));
        g2d.fillRect(x, y, size, size);
    }
    
    /**
     * Nakreslí symbol do políčka.
     * 
     * @param g2d grafický kontext
     * @param size velikost v pixelech
     */
    public static void draw(Graphics2D g2d, int size) {
        g2d.setColor(new Color(222, 122, 22));
        Path2D.Double path = new Path2D.Double();
        path.moveTo(0, size / 2);
        path.curveTo(size / 2, 0, 
                        size / 2, size,
                        size, size / 2);
        g2d.draw(path);
    }
    
}
