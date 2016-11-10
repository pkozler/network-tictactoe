package visualisation;

import configuration.Config;
import communication.ConnectionManager;
import interaction.ResponseHandler;
import interaction.MainMessageHandler;
import interaction.UpdateHandler;
import java.awt.BorderLayout;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import visualisation.components.GameListPanel;
import visualisation.components.CurrentGameWindow;
import visualisation.components.PlayerListPanel;
import visualisation.components.StatusBarPanel;

/**
 * Třída představující hlavní okno GUI, které je základní součástí vizualizační
 * vrstvy aplikace.
 * Okno obsahuje komponenty pro zobrazení seznamu připojených hráčů,
 * seznamu herních místností, a dále textová pole zobrazující hlášení
 * o událostech probíhajících v komunikační vrstvě aplikace.
 * 
 * @author Petr Kozler
 */
public class MainWindow extends JFrame {
    
    private final ConnectionManager CONNECTION_MANAGER;
    private final MainMessageHandler SERVER_MESSAGE_HANDLER;
    private final StatusBarPanel STATUS_BAR_PANEL;
    private final PlayerListPanel PLAYER_LIST_PANEL;
    private final GameListPanel GAME_LIST_PANEL;
    private final CurrentGameWindow CURRENT_GAME_WINDOW;
    
    private Timer connectTimer;
    private Timer pingTimer;
    
    public MainWindow(String host, String port, String nick) {
        setTitle("Piškvorky - klient");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(Config.DEFAULT_WINDOW_WIDTH, Config.DEFAULT_WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        
        CONNECTION_MANAGER = ConnectionSettingsDialog.setupConnection(host, port, nick);
        
        STATUS_BAR_PANEL = new StatusBarPanel();
        PLAYER_LIST_PANEL = new PlayerListPanel();
        GAME_LIST_PANEL = new GameListPanel();
        
        contentPane.add(STATUS_BAR_PANEL, BorderLayout.CENTER);
        contentPane.add(GAME_LIST_PANEL, BorderLayout.WEST);
        contentPane.add(PLAYER_LIST_PANEL, BorderLayout.EAST);
        
        CURRENT_GAME_WINDOW = new CurrentGameWindow();
        
        setContentPane(contentPane);
        setVisible(true);
        
        ResponseHandler responseHandler = new ResponseHandler(CONNECTION_MANAGER);
        UpdateHandler updateHandler = new UpdateHandler(CONNECTION_MANAGER,
                STATUS_BAR_PANEL, PLAYER_LIST_PANEL, GAME_LIST_PANEL, CURRENT_GAME_WINDOW);
        SERVER_MESSAGE_HANDLER = new MainMessageHandler(CONNECTION_MANAGER, responseHandler, updateHandler);
        
        runConnectionTasks();
    }

    private void runConnectionTasks() {
        TimerTask pingTimerTask = new TimerTask() {
            
            @Override
            public void run() {
                try {
                    CONNECTION_MANAGER.connect();
                }
                catch (IOException ex) {
                    // čekání na spuštění serveru
                }
                
                if (!CONNECTION_MANAGER.isActive()) {
                    // TODO vypsat zpravu o preruseni spojeni
                    runConnectionTasks();
                }
            }
            
        };
        
        TimerTask connectTimerTask = new TimerTask() {
            
            @Override
            public void run() {
                try {
                    CONNECTION_MANAGER.connect();
                }
                catch (IOException ex) {
                    // čekání na spuštění serveru
                }
                
                if (CONNECTION_MANAGER.isActive()) {
                    // TODO vypsat zpravu o navazani spojeni
                    pingTimer.schedule(pingTimerTask,
                            Config.SOCKET_TIMEOUT_MILLIS, Config.SOCKET_TIMEOUT_MILLIS);
                }
            }
            
        };
        
        connectTimer.schedule(connectTimerTask, Config.SOCKET_TIMEOUT_MILLIS, Config.SOCKET_TIMEOUT_MILLIS);
    }
    
}
