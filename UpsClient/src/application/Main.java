package application;

import communication.TcpClient;
import interaction.CmdArg;
import visualisation.MainWindow;

/**
 * Třída Main obsahuje metodu pro spuštění programu.
 * 
 * @author Petr Kozler
 */
public class Main {
    
    /**
     * Vstupní bod programu.
     * 
     * @param args argumenty příkazové řádky
     */
    public static void main(String[] args) {
        TcpClient client = new TcpClient();
        CmdArg cmdArgHandler = new CmdArg(args);
        new MainWindow(client, cmdArgHandler);
    }
    
}
