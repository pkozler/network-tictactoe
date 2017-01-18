package visualisation;

import configuration.Config;
import communication.TcpClient;
import interaction.CmdArg;
import interaction.MessageBackgroundReceiver;
import interaction.MessageBackgroundSender;
import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import visualisation.components.ConnectionBarPanel;
import visualisation.components.GameListPanel;
import visualisation.components.CurrentGamePanel;
import visualisation.components.PlayerListPanel;
import visualisation.components.StatusBarPanel;

/**
 * Třída MainWindow představuje hlavní okno GUI, které je základní součástí
 * vizualizační vrstvy aplikace.
 * Okno obsahuje komponenty pro zobrazení seznamu připojených hráčů,
 * seznamu herních místností, a dále textová pole zobrazující hlášení
 * o událostech probíhajících v komunikační vrstvě aplikace.
 * 
 * @author Petr Kozler
 */
public class MainWindow extends JFrame {
    
    /**
     * objekt klienta
     */
    private final TcpClient CLIENT;
    
    /**
     * panel stavového řádku
     */
    private final StatusBarPanel STATUS_PANEL;
    
    /**
     * panel seznamu hráčů
     */
    private final PlayerListPanel PLAYER_LIST_PANEL;
    
    /**
     * panel seznamu her
     */
    private final GameListPanel GAME_LIST_PANEL;
    
    /**
     * panel stavu připojení
     */
    private final ConnectionBarPanel CONNECTION_PANEL;
    
    /**
     * panel aktuální herní místnosti
     */
    private final CurrentGamePanel CURRENT_GAME_PANEL;
    
    /**
     * Vytvoří hlavní okno.
     * 
     * @param client objekt klienta
     * @param cmdArgHandler objekt pro zpracování argumentů příkazové řádky
     */
    public MainWindow(TcpClient client, CmdArg cmdArgHandler) {
        setTitle("Piškvorky - klient");
        setSize(Config.DEFAULT_WINDOW_WIDTH, Config.DEFAULT_WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        
        CLIENT = client;
        STATUS_PANEL = new StatusBarPanel();
        MessageBackgroundSender messageBackgroundSender = new MessageBackgroundSender(CLIENT, STATUS_PANEL);
        CURRENT_GAME_PANEL = new CurrentGamePanel(messageBackgroundSender);
        GAME_LIST_PANEL = new GameListPanel(messageBackgroundSender);
        PLAYER_LIST_PANEL = new PlayerListPanel(messageBackgroundSender);
        MessageBackgroundReceiver messageBackgroundReceiver = new MessageBackgroundReceiver(CLIENT,
                STATUS_PANEL, PLAYER_LIST_PANEL, GAME_LIST_PANEL, CURRENT_GAME_PANEL);
        
        contentPane.add(CURRENT_GAME_PANEL, BorderLayout.CENTER);
        contentPane.add(STATUS_PANEL, BorderLayout.SOUTH);
        contentPane.add(PLAYER_LIST_PANEL, BorderLayout.EAST);
        contentPane.add(GAME_LIST_PANEL, BorderLayout.WEST);
        
        CONNECTION_PANEL = new ConnectionBarPanel(CLIENT, cmdArgHandler,
                messageBackgroundSender, messageBackgroundReceiver,
                PLAYER_LIST_PANEL, GAME_LIST_PANEL, CURRENT_GAME_PANEL, STATUS_PANEL);
        contentPane.add(CONNECTION_PANEL, BorderLayout.NORTH);
        
        setContentPane(contentPane);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            
            public void windowClosing(WindowEvent winEvt) {
                if (!CLIENT.isConnected()) {
                    System.exit(0);
                }
                
                int result = JOptionPane.showConfirmDialog(null,
                    "Opravdu se chcete odpojit?", "Odpojení", JOptionPane.YES_NO_OPTION);
                
                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
            
        });
        
        setVisible(true);
    }
    
}
