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
 * Třída ConnectionBarPanel 
 * 
 * @author Petr Kozler
 */
public class ConnectionBarPanel extends JPanel {
    
    /**
     * 
     */
    private final JButton CONNECT_BUTTON;
    
    /**
     * 
     */
    private final JButton DISCONNECT_BUTTON;
    
    /**
     * 
     */
    private final TcpClient CLIENT;
    
    /**
     * 
     */
    private Timer connectTimer;
    
    /**
     * 
     */
    private TimerTask connectTimerTask;
    
    /**
     * 
     */ 
    private final StatusBarPanel statusBarPanel;
    
    /**
     * 
     * 
     * @param client
     * @param connectionTimer
     * @param connectTimerTask
     * @param statusBarPanel 
     */
    public ConnectionBarPanel(TcpClient client, Timer connectionTimer,
            TimerTask connectTimerTask, StatusBarPanel statusBarPanel) {
        CLIENT = client;
        this.connectTimer = connectTimer;
        this.connectTimerTask = connectTimerTask;
        this.statusBarPanel = statusBarPanel;
        
        CONNECT_BUTTON = new JButton("Připojit se");
        DISCONNECT_BUTTON = new JButton("Odpojit se");
    }
    
    /**
     * 
     * 
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
     * 
     * 
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
     * 
     * 
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
