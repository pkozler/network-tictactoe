package application;

import communication.ConnectionManager;
import interaction.CmdArgHandler;
import visualisation.MainWindow;

/**
 *
 * @author Petr Kozler
 */
public class Main {
    
    public static void main(String[] args) {
        ConnectionManager connectionManager = new ConnectionManager();
        CmdArgHandler cmdArgHandler = new CmdArgHandler(args);
        new MainWindow(connectionManager, cmdArgHandler);
    }
    
}
