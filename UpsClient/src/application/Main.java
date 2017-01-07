package application;

import communication.TcpClient;
import interaction.CmdArg;
import visualisation.MainWindow;

/**
 * Třída Main 
 * 
 * @author Petr Kozler
 */
public class Main {
    
    /**
     * 
     * 
     * @param args 
     */
    public static void main(String[] args) {
        TcpClient client = new TcpClient();
        CmdArg cmdArgHandler = new CmdArg(args);
        new MainWindow(client, cmdArgHandler);
    }
    
}
