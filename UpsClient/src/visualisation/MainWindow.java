package visualisation;

import configuration.Config;
import communication.ConnectionManager;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Petr Kozler
 */
public class MainWindow extends JFrame {
    
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
    }
    
    
}
