package visualisation.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 * Třída StatusBarPanel 
 * 
 * @author Petr Kozler
 */
public class StatusBarPanel extends JPanel {

    /**
     * 
     */
    private final JTextPane textPane;

    /**
     * 
     * 
     */
    public StatusBarPanel() {
        final int fontSize = 12;
        textPane = new JTextPane();
        textPane.setFont(new Font("Monospaced", Font.PLAIN, fontSize));
        JScrollPane scrollPane = new JScrollPane(textPane);

        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
                public void adjustmentValueChanged(AdjustmentEvent e) {
                    textPane.select(textPane.getCaretPosition() * fontSize, 0);
                }
            }
        );
    }

    /**
     * 
     * 
     * @param format
     * @param args 
     */
    public void printReceivingStatus(String format, Object... args) {
        appendToPane(Color.BLACK, format, args);
    }

    /**
     * 
     * 
     * @param format
     * @param args 
     */
    public void printSendingStatus(String format, Object... args) {
        appendToPane(Color.BLUE, format, args);
    }

    /**
     * 
     * 
     * @param format
     * @param args 
     */
    public void printErrorStatus(String format, Object... args) {
        appendToPane(Color.RED, format, args);
    }

    /**
     * 
     * 
     * @param c
     * @param format
     * @param args 
     */
    private void appendToPane(Color c, String format, Object... args) {
        String msg = String.format(format, args);

        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = textPane.getDocument().getLength();
        textPane.setCaretPosition(len);
        textPane.setCharacterAttributes(aset, false);
        textPane.replaceSelection(msg);
    }

}
