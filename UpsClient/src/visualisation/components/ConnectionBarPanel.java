package visualisation.components;

import communication.TcpClient;
import configuration.Config;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

/**
 * Třída ConnectionBarPanel představuje panel pro zobrazení stavu
 * spojení se serverem.
 * 
 * @author Petr Kozler
 */
public class ConnectionBarPanel extends JPanel {
    
    /**
     * tlačítko pro připojení
     */
    private final JButton CONNECT_BUTTON;
    
    /**
     * tlačítko pro odpojení
     */
    private final JButton DISCONNECT_BUTTON;
    
    /**
     * objekt klienta
     */
    private final TcpClient CLIENT;
    
    /**
     * časovač pro navazování spojení
     */
    private Timer connectTimer;
    
    /**
     * úloha časovače pro navazování spojení
     */
    private TimerTask connectTimerTask;
    
    /**
     * panel stavového řádku
     */ 
    private final StatusBarPanel statusBarPanel;
    
    /**
     * Vytvoří panel pro zobrazení stavu spojení.
     * 
     * @param client objekt klienta
     * @param connectionTimer časovač pro navazování spojení
     * @param connectTimerTask úloha časovače pro navazování spojení
     * @param statusBarPanel panel stavového řádku
     */
    public ConnectionBarPanel(TcpClient client, Timer connectionTimer,
            TimerTask connectTimerTask, StatusBarPanel statusBarPanel) {
        CLIENT = client;
        this.connectTimer = connectTimer;
        this.connectTimerTask = connectTimerTask;
        this.statusBarPanel = statusBarPanel;
        
        CONNECT_BUTTON = new JButton("Připojit se");
        DISCONNECT_BUTTON = new JButton("Odpojit se");
        
        setListeners();
    }
    
    /**
     * Nastaví listenery pro stisk tlačítek.
     */
    private void setListeners() {
        CONNECT_BUTTON.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                connectActionPerformed();
            }

        });
        
        DISCONNECT_BUTTON.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                disconnectActionPerformed();
            }

        });
    }
    
    /**
     * Zpracuje stisk tlačítka pro připojení k serveru.
     */
    private void connectActionPerformed() {
        JTextField hostTF = new JTextField("localhost");
        JSpinner portSpinner = new JSpinner(new SpinnerNumberModel(
                Config.DEFAULT_PORT, Config.MIN_PORT, Config.MAX_PORT, 1));
        
        final JComponent[] inputs = new JComponent[] {
                new JLabel("Adresa serveru:"),
                hostTF,
                new JLabel("Port:"),
                portSpinner
        };
        
        int result = JOptionPane.showConfirmDialog(null, inputs, "Připojení", JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String host = hostTF.getText();
            int port = (byte) portSpinner.getValue();
            
            connectTimer.schedule(connectTimerTask, Config.SOCKET_TIMEOUT_MILLIS, Config.SOCKET_TIMEOUT_MILLIS);
        }
    }

    /**
     * Zpracuje stisk tlačítka pro odpojení od serveru.
     */
    private void disconnectActionPerformed() {
        int result = JOptionPane.showConfirmDialog(null,
                "Opravdu se chcete odpojit?", "Odpojení", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                CLIENT.disconnect();
            }
            catch (IOException ex) {
                statusBarPanel.printSendingStatus("Chyba při odpojování: %s",
                        ex.getLocalizedMessage());
            }
        }
    }
    
}
