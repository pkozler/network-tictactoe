package interaction;

import communication.TcpClient;
import configuration.Config;
import java.util.Timer;
import java.util.TimerTask;
import visualisation.components.StatusBarPanel;

/**
 * Třída PingTimer představuje pomocnou komponentu pro vytváření a rušení
 * časovače, který slouží pro periodické spouštění úlohy testování
 * spojení se serverem.
 * 
 * @author Petr Kozler
 */
public class PingTimer {
    
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
     * objekt časovače pro úlohu testování spojení
     */
    private Timer timer;
    
    /**
     * Vytvoří komponentu s časovačem pro testování spojení.
     * 
     * @param client objekt klienta
     * @param statusBarPanel panel stavového řádku
     * @param messageSender vysílač zpráv
     */
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
    
    /**
     * Zastaví periodické testování odezvy serveru.
     */
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
