package interaction.receiving;

import communication.TcpClient;
import communication.TcpMessage;

/**
 * Abstraktní třída AParser představuje obecný parser zpráv
 * přijatých ze serveru.
 * 
 * @author Petr Kozler
 */
public abstract class AParser {
    
    /**
     * objekt klienta
     */
    protected final TcpClient CLIENT;
    
    /**
     * zpráva
     */
    protected final TcpMessage MESSAGE;
    
    /**
     * Vytvoří parser.
     * 
     * @param client objekt klienta
     * @param message zpráva
     */
    public AParser(TcpClient client, TcpMessage message) {
        CLIENT = client;
        MESSAGE = message;
    }
    
    /**
     * Vrátí výsledek zpracování a aktualizuje stav GUI.
     * 
     * @return výsledek zpracování
     */
    public abstract String getStatusAndUpdateGUI();
    
}
