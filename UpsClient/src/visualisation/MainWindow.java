package visualisation;

import configuration.Config;
import communication.ConnectionManager;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Petr Kozler
 */
public class MainWindow extends JFrame {
    
    private Timer connectTimer;
    private Timer pingTimer;
    private ConnectionManager connectionManager;
    
    public MainWindow(String host, String port, String nick) {
        setTitle("Piškvorky - klient");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(Config.DEFAULT_WINDOW_WIDTH, Config.DEFAULT_WINDOW_HEIGHT);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));

        // TODO pridat panely
        
        setContentPane(contentPane);
        setVisible(true);

        connectionManager = ConnectionDialog.setupConnection(host, port, nick);
        runConnectionTasks();
    }

    private void runConnectionTasks() {
        TimerTask pingTimerTask = new TimerTask() {
            
            @Override
            public void run() {
                connectionManager.connect();
                
                if (!connectionManager.isConnected()) {
                    // TODO vypsat zpravu o preruseni spojeni
                    runConnectionTasks();
                }
            }
            
        };
        
        TimerTask connectTimerTask = new TimerTask() {
            
            @Override
            public void run() {
                connectionManager.connect();
                
                if (connectionManager.isConnected()) {
                    // TODO vypsat zpravu o navazani spojeni
                    pingTimer.schedule(pingTimerTask,
                            Config.TIMER_DELAY_MILLIS, Config.TIMER_DELAY_MILLIS);
                }
            }
            
        };
        
        connectTimer.schedule(connectTimerTask, Config.TIMER_DELAY_MILLIS, Config.TIMER_DELAY_MILLIS);
    }
    
}
