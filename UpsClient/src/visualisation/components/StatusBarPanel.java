package visualisation.components;

import javax.swing.JPanel;

/**
 *
 * @author Petr Kozler
 */
public class StatusBarPanel extends JPanel {
    
    public void printStatus(String format, Object... args) {
        String str = String.format(format, args);
        // TODO vypsat
    }
    
}
