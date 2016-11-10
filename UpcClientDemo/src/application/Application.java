package application;

/**
 * Třída Application představuje hlavní třídu programu.
 * 
 * @author Petr Kozler
 */
public class Application {

    /**
     * Spustí klientskou aplikaci.
     * 
     * @param args argumenty příkazové řádky (hostname a port serveru)
     */
    public static void main(String[] args) {
        ArgParser parser = new ArgParser(args);
        IClient client = new Client();
        new MainWindow(client, parser);
    }
}
