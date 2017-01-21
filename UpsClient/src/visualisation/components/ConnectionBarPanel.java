package visualisation.components;

import communication.TcpClient;
import configuration.Config;
import interaction.CmdArg;
import interaction.MessageBackgroundReceiver;
import interaction.MessageBackgroundSender;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
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
     * objekt pro zpracování parametrů příkazového řádku
     */
    private final CmdArg CMD_ARG_HANDLER;
    
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
    private Timer connectTimer;
    
    /**
     * časovač pro testování odezvy
     */
    private Timer pingTimer;
    
    /**
     * vlákno pro příjem zpráv na pozadí
     */
    private Thread receiveThread;
    
    /**
     * vlákno pro odesílání zpráv na pozadí
     */
    private Thread sendThread;
    
    /**
     * panel stavového řádku
     */ 
    private final StatusBarPanel STATUS_BAR_PANEL;
    
    /**
     * panel seznamu hráčů
     */
    private final PlayerListPanel PLAYER_LIST_PANEL;
    
    /**
     * panel seznamu her
     */
    private final GameListPanel GAME_LIST_PANEL;
    
    /**
     * panel stavu hry
     */
    private final CurrentGamePanel CURRENT_GAME_PANEL;
    
    /**
     * Vytvoří panel pro zobrazení stavu spojení.
     * 
     * @param client
     * @param cmdArgHandler
     * @param playerListPanel
     * @param messageBackgroundSender
     * @param currentGamePanel
     * @param statusBarPanel
     * @param gameListPanel
     * @param messageBackgroundReceiver
     */
    public ConnectionBarPanel(TcpClient client, CmdArg cmdArgHandler,
            MessageBackgroundSender messageBackgroundSender, MessageBackgroundReceiver messageBackgroundReceiver,
            PlayerListPanel playerListPanel, GameListPanel gameListPanel,
            CurrentGamePanel currentGamePanel, StatusBarPanel statusBarPanel) {
        super(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Připojení"));
        setPreferredSize(new Dimension(0, 60));
        
        CMD_ARG_HANDLER = cmdArgHandler;
        CLIENT = client;
        MESSAGE_SENDER = messageBackgroundSender;
        MESSAGE_RECEIVER = messageBackgroundReceiver;
        
        this.PLAYER_LIST_PANEL = playerListPanel;
        this.GAME_LIST_PANEL = gameListPanel;
        this.CURRENT_GAME_PANEL = currentGamePanel;
        this.STATUS_BAR_PANEL = statusBarPanel;
        
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
        setLabel(false);
        
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
        startConnectionTimer();
        setButtons(true);
    }

    /**
     * Zpracuje stisk tlačítka pro odpojení od serveru.
     */
    private void disconnectActionPerformed() {
        if (!CLIENT.isConnected()) {
            connectTimer.cancel();
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
                        ex.getLocalizedMessage());
            }
        }
    }
    
    /**
     * Vytvoří úlohu časovače pro navazování spojení se serverem.
     * 
     * @return úloha časovače pro navazování spojení
     */
    private TimerTask createConnectionTask() {
        return new TimerTask() {
            
            @Override
            public void run() {
                try {
                    CLIENT.connect(CMD_ARG_HANDLER.getHost(), CMD_ARG_HANDLER.getPort());
                }
            catch (IOException e) {
                    // čekání na spojení
                }
                
                if (CLIENT.isConnected()) {
                    STATUS_BAR_PANEL.printSendingStatus("Spojení navázáno.");
                    setLabel(true);
                    startCommunicationThreads();
                    startPingTimer();
                    // ukončení úlohy po navázání spojení
                    connectTimer.cancel();
                }
            }
            
        };
    }
    
    /**
     * Vytvoří úlohu časovače pro testování odezvy serveru.
     * 
     * @return úloha časovače pro testování odezvy
     */
    private TimerTask createPingTask() {
        return new TimerTask() {
            
            @Override
            public void run() {
                MESSAGE_SENDER.enqueueMessageBuilder(null);
                
                if (!CLIENT.isConnected()) {
                    STATUS_BAR_PANEL.printSendingStatus("Spojení ukončeno.");
                    setLabel(false);
                    setButtons(false);
                    // ukončení úlohy po zrušení spojení
                    pingTimer.cancel();
                }
            }
            
        };
    }
    
    /**
     * Spustí navazování spojení.
     */
    private void startConnectionTimer() {
        STATUS_BAR_PANEL.printSendingStatus("Spuštěno navazování spojení");
        connectTimer = new Timer();
        connectTimer.schedule(createConnectionTask(), 0, Config.PING_PERIOD_MILLIS);
    }
    
    /**
     * Spustí periodické testování odezvy serveru.
     */
    private void startPingTimer() {
        STATUS_BAR_PANEL.printSendingStatus("Spuštěna kontrola odezvy");
        pingTimer = new Timer();
        pingTimer.schedule(createPingTask(),
                            Config.PING_PERIOD_MILLIS, Config.PING_PERIOD_MILLIS);
    }
    
    /**
     * Spustí vlákna pro příjem a odesílání zpráv.
     */
    private void startCommunicationThreads() {
        receiveThread = new Thread(MESSAGE_RECEIVER);
        sendThread = new Thread(MESSAGE_SENDER);

        receiveThread.start();
        sendThread.start();
    }
    
    /**
     * Nastaví hlášení v popisku podle stavu připojení.
     * 
     * @param connected true, pokud je klient připojen, jinak false
     */
    private void setLabel(boolean connected) {
        CONNECTION_LABEL.setText(connected ? String.format("Připojeno k serveru na adrese %s:%d",
                    CMD_ARG_HANDLER.getHost(), CMD_ARG_HANDLER.getPort()) : "Odpojeno");
        PLAYER_LIST_PANEL.setButtons(connected);
        GAME_LIST_PANEL.setButtons(connected);
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

    @Override
    public void update(Observable o, Object o1) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
