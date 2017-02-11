package interaction;

import communication.TcpClient;
import configuration.Config;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import visualisation.components.StatusBarPanel;

/**
 * Třída ConnectionTimer představuje pomocnou komponentu pro vytváření a rušení
 * časovače, který slouží pro periodické spouštění úlohy vytvoření
 * spojení se serverem.
 * 
 * @author Petr Kozler
 */
public class ConnectionTimer {
    
    /**
     * objekt klienta
     */
    private final TcpClient CLIENT;
    
    /**
     * panel stavového řádku
     */
    private final StatusBarPanel STATUS_BAR_PANEL;
    
    /**
     * vysílač zpráv
     */
    private final MessageBackgroundSender MESSAGE_SENDER;
    
    /**
     * přijímač zpráv
     */
    private final MessageBackgroundReceiver MESSAGE_RECEIVER;
    
    /**
     * objekt časovače pro úlohu testování spojení
     */
    private final PingTimer PING_TIMER;
    
    /**
     * objekt časovače pro úlohu vytvoření spojení
     */
    private Timer timer;
    
    /**
     * Vytvoří komponentu s časovačem pro vytvoření spojení.
     * 
     * @param client objekt klienta
     * @param statusBarPanel panel stavového řádku
     * @param messageSender vysílač zpráv
     * @param messageReceiver přijímač zpráv
     * @param pingTimer časovač pro testování spojení
     */
    public ConnectionTimer(TcpClient client, StatusBarPanel statusBarPanel,
            MessageBackgroundSender messageSender, MessageBackgroundReceiver messageReceiver,
            PingTimer pingTimer) {
        CLIENT = client;
        STATUS_BAR_PANEL = statusBarPanel;
        MESSAGE_SENDER = messageSender;
        MESSAGE_RECEIVER = messageReceiver;
        PING_TIMER = pingTimer;
    }
    
    /**
     * Spustí periodické pokusy o navázání spojení se serverem.
     */
    public void start() {
        timer = new Timer();
        timer.schedule(createTask(), 0, Config.PING_PERIOD_MILLIS);
    }
    
    /**
     * Zastaví periodické pokusy o navázání spojení se serverem.
     */
    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }
    
    /**
     * Vytvoří úlohu časovače pro navazování spojení se serverem.
     * 
     * @return úloha časovače pro navazování spojení
     */
    private TimerTask createTask() {
        return new TimerTask() {
            
            @Override
            public void run() {
                try {
                    if (!CLIENT.isConnected()) {
                        CLIENT.connect();
                    }
                }
                catch (IOException e) {
                    // čekání na spojení
                }
                
                if (CLIENT.isConnected()) {
                    startCommunicationThreads();
                    STATUS_BAR_PANEL.printSendingStatus("Spojení navázáno.");
                    PING_TIMER.start();
                    STATUS_BAR_PANEL.printSendingStatus("Spuštěna kontrola odezvy");
                    // ukončení úlohy po navázání spojení
                    stop();
                }
            }
            
        };
    }
    
    /**
     * Spustí vlákna pro příjem a odesílání zpráv.
     */
    private void startCommunicationThreads() {
        Thread receiveThread = new Thread(MESSAGE_RECEIVER);
        Thread sendThread = new Thread(MESSAGE_SENDER);

        receiveThread.start();
        sendThread.start();
    }
    
}
