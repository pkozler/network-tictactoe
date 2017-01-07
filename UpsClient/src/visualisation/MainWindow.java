package visualisation;

import configuration.Config;
import communication.TcpClient;
import interaction.CmdArg;
import interaction.MessageBackgroundReceiver;
import interaction.MessageBackgroundSender;
import java.awt.BorderLayout;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import visualisation.components.ConnectionBarPanel;
import visualisation.components.GameListPanel;
import visualisation.components.CurrentGamePanel;
import visualisation.components.PlayerListPanel;
import visualisation.components.StatusBarPanel;

/**
 * Třída MainWindow představuje hlavní okno GUI, které je základní součástí vizualizační
 * vrstvy aplikace.
 * Okno obsahuje komponenty pro zobrazení seznamu připojených hráčů,
 * seznamu herních místností, a dále textová pole zobrazující hlášení
 * o událostech probíhajících v komunikační vrstvě aplikace.
 * 
 * @author Petr Kozler
 */
public class MainWindow extends JFrame {
    
    /**
     * 
     */
    private final TcpClient CLIENT;
    
    /**
     * 
     */
    private final StatusBarPanel STATUS_BAR_PANEL;
    
    /**
     * 
     */
    private final PlayerListPanel PLAYER_LIST_PANEL;
    
    /**
     * 
     */
    private final GameListPanel GAME_LIST_PANEL;
    
    /**
     * 
     */
    private final ConnectionBarPanel CONNECTION_PANEL;
    
    /**
     * 
     */
    private final CurrentGamePanel CURRENT_GAME_PANEL;
    
    /**
     * 
     */
    private final MessageBackgroundReceiver MESSAGE_RECEIVER;
    
    /**
     * 
     */
    private final MessageBackgroundSender MESSAGE_SENDER;
    
    /**
     * 
     */
    private final CmdArg CMD_ARG_HANDLER;
    
    /**
     * 
     */
    private Timer connectTimer;
    
    /**
     * 
     */
    private Timer pingTimer;
    
    /**
     * 
     */
    private Thread receiveThread;
    
    /**
     * 
     */
    private Thread sendThread;
    
    /**
     * 
     * 
     * @param client
     * @param cmdArgHandler 
     */
    public MainWindow(TcpClient client, CmdArg cmdArgHandler) {
        setTitle("Piškvorky - klient");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(Config.DEFAULT_WINDOW_WIDTH, Config.DEFAULT_WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        
        CMD_ARG_HANDLER = cmdArgHandler;
        CLIENT = client;
        STATUS_BAR_PANEL = new StatusBarPanel();
        MESSAGE_SENDER = new MessageBackgroundSender(CLIENT, STATUS_BAR_PANEL);
        PLAYER_LIST_PANEL = new PlayerListPanel(MESSAGE_SENDER);
        GAME_LIST_PANEL = new GameListPanel(MESSAGE_SENDER);
        CURRENT_GAME_PANEL = new CurrentGamePanel(MESSAGE_SENDER);
        MESSAGE_RECEIVER = new MessageBackgroundReceiver(CLIENT,
                STATUS_BAR_PANEL, PLAYER_LIST_PANEL, GAME_LIST_PANEL, CURRENT_GAME_PANEL);
        
        contentPane.add(CURRENT_GAME_PANEL, BorderLayout.CENTER);
        contentPane.add(STATUS_BAR_PANEL, BorderLayout.SOUTH);
        contentPane.add(PLAYER_LIST_PANEL, BorderLayout.WEST);
        contentPane.add(GAME_LIST_PANEL, BorderLayout.EAST);
        
        TimerTask connectTimerTask = createConnectionTimers();
        CONNECTION_PANEL = new ConnectionBarPanel(CLIENT, connectTimer, connectTimerTask, STATUS_BAR_PANEL);
        contentPane.add(CONNECTION_PANEL, BorderLayout.NORTH);
        
        setContentPane(contentPane);
        setVisible(true);
        
        connectTimer.schedule(connectTimerTask, Config.SOCKET_TIMEOUT_MILLIS, Config.SOCKET_TIMEOUT_MILLIS);
    }
    
    /**
     * 
     * 
     * @return 
     */
    private TimerTask createConnectionTimers() {
        TimerTask pingTimerTask = new TimerTask() {
            
            @Override
            public void run() {
                MESSAGE_SENDER.enqueueMessageBuilder(null);
                
                if (!CLIENT.isConnected()) {
                    createConnectionTimers();
                }
            }
            
        };
        
        TimerTask connectTimerTask = new TimerTask() {
            
            @Override
            public void run() {
                try {
                    CLIENT.connect(
                            CMD_ARG_HANDLER.getHost(), CMD_ARG_HANDLER.getPort());
                }
                catch (IOException e) {
                    // čekání na spojení
                }
                
                if (CLIENT.isConnected()) {
                    startCommunicationThreads();
                    pingTimer.schedule(pingTimerTask,
                            Config.SOCKET_TIMEOUT_MILLIS, Config.SOCKET_TIMEOUT_MILLIS);
                }
            }
            
        };
        
        return connectTimerTask;
    }
    
    /**
     * 
     * 
     */
    private void startCommunicationThreads() {
        receiveThread = new Thread(MESSAGE_RECEIVER);
        sendThread = new Thread(MESSAGE_SENDER);
        
        receiveThread.start();
        sendThread.start();
    }
    
}
