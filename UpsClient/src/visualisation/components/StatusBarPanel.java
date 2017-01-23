package visualisation.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * Třída StatusBarPanel představuje panel pro zobrazení textového pole
 * pro výpis událostí klienta.
 * 
 * @author Petr Kozler
 */
public class StatusBarPanel extends JPanel {

    /**
     * textové pole
     */
    private final JTextPane textPane;
    
    /**
     * formátovaný dokument textového pole
     */
    StyledDocument doc;
    
    /**
     * atributy textu hlášení o příjmu
     */
    SimpleAttributeSet receivingStatusAttributes;
    
    /**
     * atributy textu hlášení o odesílání
     */
    SimpleAttributeSet sendingStatusAttributes;
    
    /**
     * atributy textu hlášení o chybě
     */
    SimpleAttributeSet errorStatusAttributes;

    /**
     * atributy textu hlášení o úspěšném průběhu operace
     */
    SimpleAttributeSet okStatusAttributes;
    
    /**
     * Vytvoří panel stavového řádku.
     */
    public StatusBarPanel() {
        super(new BorderLayout());
        setPreferredSize(new Dimension(0, 120));
        setBorder(BorderFactory.createTitledBorder("Výpis událostí"));
        
        final int fontSize = 12;
        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setFont(new Font("Monospaced", Font.PLAIN, fontSize));
        textPane.setBorder(BorderFactory.createLoweredBevelBorder());
        
        doc = textPane.getStyledDocument();
        receivingStatusAttributes = new SimpleAttributeSet();
        sendingStatusAttributes = new SimpleAttributeSet();
        errorStatusAttributes = new SimpleAttributeSet();
        okStatusAttributes = new SimpleAttributeSet();
        StyleConstants.setForeground(receivingStatusAttributes, Color.BLACK);
        StyleConstants.setForeground(sendingStatusAttributes, Color.BLUE);
        StyleConstants.setForeground(errorStatusAttributes, Color.RED);
        StyleConstants.setForeground(okStatusAttributes, Color.GREEN);
        
        JScrollPane scrollPane = new JScrollPane(textPane, 
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            
                @Override
                public void adjustmentValueChanged(AdjustmentEvent e) {
                    textPane.select(textPane.getCaretPosition() * fontSize, 0);
                }
                
            }
        );
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Vypíše výsledek příjmu zprávy.
     * 
     * @param format formátovací řetězec
     * @param args argumenty řetězce
     */
    public void printReceivingStatus(String format, Object... args) {
        appendToPane(receivingStatusAttributes, format, args);
    }

    /**
     * Vypíše výsledek odeslání zprávy.
     * 
     * @param format formátovací řetězec
     * @param args 
     */
    public void printSendingStatus(String format, Object... args) {
        appendToPane(sendingStatusAttributes, format, args);
    }

    /**
     * Vypíše chybu při komunikaci.
     * 
     * @param format formátovací řetězec
     * @param args argumenty řetězce
     */
    public void printErrorStatus(String format, Object... args) {
        appendToPane(errorStatusAttributes, format, args);
    }
    
    /**
     * Vypíše úspěšné provedení operace.
     * 
     * @param format formátovací řetězec
     * @param args argumenty řetězce
     */
    public void printOkStatus(String format, Object... args) {
        appendToPane(okStatusAttributes, format, args);
    }

    /**
     * Přidá text do textového pole.
     * 
     * @param attributes atributy textu
     * @param format formátovací řetězec
     * @param args argumenty řetězce
     */
    private void appendToPane(AttributeSet attributes, String format, Object... args) {
        String msg = String.format(format, args) + "\n";

        try {
            doc.insertString(doc.getLength(), msg, attributes);
        }
        catch (BadLocationException ex) {
            // chyba - neplatná pozice v dokumentu
        }
    }

}
