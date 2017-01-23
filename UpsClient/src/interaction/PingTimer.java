package interaction;

import communication.TcpClient;
import configuration.Config;
import java.util.Timer;
import java.util.TimerTask;
import visualisation.components.StatusBarPanel;

/**
 *
 * @author Petr Kozler
 */
public class PingTimer {
    
    private final TcpClient CLIENT;
    
    private final StatusBarPanel STATUS_BAR_PANEL;
    
    private final MessageBackgroundSender MESSAGE_SENDER;
    
    private Timer timer;
    
    public PingTimer(TcpClient client, StatusBarPanel statusBarPanel,
            MessageBackgroundSender messageSender) {
        CLIENT = client;
        STATUS_BAR_PANEL = statusBarPanel;
        MESSAGE_SENDER = messageSender;
    }
    
    /**
     * Spustí periodické testování odezvy serveru.
     */
    public void start() {
        timer = new Timer();
        timer.schedule(createTask(),
                            Config.PING_PERIOD_MILLIS, Config.PING_PERIOD_MILLIS);
    }
    
    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }
    
    /**
     * Vytvoří úlohu časovače pro testování odezvy serveru.
     * 
     * @return úloha časovače pro testování odezvy
     */
    private TimerTask createTask() {
        return new TimerTask() {
            
            @Override
            public void run() {
                MESSAGE_SENDER.enqueueMessageBuilder(null);
                
                if (!CLIENT.isConnected()) {
                    STATUS_BAR_PANEL.printSendingStatus("Spojení ukončeno.");
                    // ukončení úlohy po zrušení spojení
                    stop();
                }
            }
            
        };
    }
    
}
