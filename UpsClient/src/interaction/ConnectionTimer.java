package interaction;

import communication.TcpClient;
import configuration.Config;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import visualisation.components.StatusBarPanel;

/**
 *
 * @author Petr Kozler
 */
public class ConnectionTimer {
    
    private final TcpClient CLIENT;
    
    private final StatusBarPanel STATUS_BAR_PANEL;
    
    private final MessageBackgroundSender MESSAGE_SENDER;
    
    private final MessageBackgroundReceiver MESSAGE_RECEIVER;
    
    private final PingTimer PING_TIMER;
    
    private Timer timer;
    
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
     * Spustí navazování spojení.
     */
    public void start() {
        timer = new Timer();
        timer.schedule(createTask(), 0, Config.PING_PERIOD_MILLIS);
    }
    
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
