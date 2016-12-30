package application;

import communication.TcpClient;
import interaction.CmdArg;
import visualisation.MainWindow;

/**
 *
 * @author Petr Kozler
 */
public class Main {
    
    public static void main(String[] args) {
        TcpClient client = new TcpClient();
        CmdArg cmdArgHandler = new CmdArg(args);
        new MainWindow(client, cmdArgHandler);
    }
    
}
