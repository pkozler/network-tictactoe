package visualisation.components;

import communication.TcpClient;
import interaction.ConnectionTimer;
import interaction.MessageBackgroundReceiver;
import interaction.MessageBackgroundSender;
import interaction.PingTimer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Třída ConnectionBarPanel představuje panel pro zobrazení stavu
 * spojení se serverem.
 * 
 * @author Petr Kozler
 */
public class ConnectionBarPanel extends JPanel implements Observer {
    
    /**
     * popisek se stavem připojení
     */
    private final JLabel CONNECTION_LABEL;
    
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
     * přijímač zpráv
     */
    private final MessageBackgroundReceiver MESSAGE_RECEIVER;
    
    /**
     * vysílač zpráv
     */
    private final MessageBackgroundSender MESSAGE_SENDER;
    
    /**
     * časovač pro navazování spojení
     */
    private final ConnectionTimer CONNECT_TIMER;
    
    /**
     * časovač pro testování odezvy
     */
    private final PingTimer PING_TIMER;
    
    /**
     * panel stavového řádku
     */ 
    private final StatusBarPanel STATUS_BAR_PANEL;
    
    /**
     * Vytvoří panel pro zobrazení stavu spojení.
     * 
     * @param client
     * @param messageBackgroundSender
     * @param statusBarPanel
     * @param messageBackgroundReceiver
     */
    public ConnectionBarPanel(TcpClient client, 
            MessageBackgroundSender messageBackgroundSender,
            MessageBackgroundReceiver messageBackgroundReceiver,
            StatusBarPanel statusBarPanel) {
        super(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Připojení"));
        setPreferredSize(new Dimension(0, 60));
        
        CLIENT = client;
        MESSAGE_SENDER = messageBackgroundSender;
        MESSAGE_RECEIVER = messageBackgroundReceiver;
        STATUS_BAR_PANEL = statusBarPanel;
        
        CONNECT_BUTTON = new JButton("Připojit se");
        DISCONNECT_BUTTON = new JButton("Odpojit se");
        CONNECTION_LABEL = new JLabel("Nepřipojeno");
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(CONNECT_BUTTON);
        buttonPanel.add(DISCONNECT_BUTTON);
        add(buttonPanel, BorderLayout.EAST);
        
        JPanel labelPanel = new JPanel();
        labelPanel.add(CONNECTION_LABEL);
        add(labelPanel, BorderLayout.WEST);
        
        setListeners();
        setButtons(false);
        CONNECTION_LABEL.setText("Odpojeno");
        
        PING_TIMER = new PingTimer(CLIENT, STATUS_BAR_PANEL, MESSAGE_SENDER);
        CONNECT_TIMER = new ConnectionTimer(CLIENT, STATUS_BAR_PANEL, MESSAGE_SENDER,
                MESSAGE_RECEIVER, PING_TIMER);
        connectActionPerformed();
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
        CONNECT_TIMER.start();
        STATUS_BAR_PANEL.printSendingStatus("Spuštěno navazování spojení");
        setButtons(true);
    }

    /**
     * Zpracuje stisk tlačítka pro odpojení od serveru.
     */
    private void disconnectActionPerformed() {
        if (!CLIENT.isConnected()) {
            CONNECT_TIMER.stop();
            STATUS_BAR_PANEL.printSendingStatus("Navazování spojení zrušeno");
            setButtons(false);
            
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(null,
                "Opravdu se chcete odpojit?", "Odpojení", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                CLIENT.disconnect();
            }
            catch (IOException ex) {
                STATUS_BAR_PANEL.printSendingStatus("Chyba při odpojování: %s",
                        ex.getClass().getSimpleName());
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * Nastaví aktivaci tlačítek podle stavu připojení.
     * 
     * @param connected TRUE, pokud je klient připojen, jinak FALSE
     */
    private void setButtons(boolean connected) {
        CONNECT_BUTTON.setEnabled(!connected);
        DISCONNECT_BUTTON.setEnabled(connected);
    }

    /**
     * Nastaví hlášení v popisku podle stavu připojení.
     * 
     * @param o objekt klienta
     * @param o1 argument notifikace
     */
    @Override
    public void update(Observable o, Object o1) {
        TcpClient client = (TcpClient) o;
        
        if (!client.isConnected()) {
            setButtons(false);
            CONNECTION_LABEL.setText("Odpojeno");
            
            return;
        }
        
        CONNECTION_LABEL.setText(String.format("Připojeno k serveru na adrese %s:%d",
                    client.getHost(), client.getPort()));
    }
    
}
