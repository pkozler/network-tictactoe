package interaction.receiving;

import communication.TcpClient;
import communication.Message;

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
    protected final Message MESSAGE;
    
    /**
     * Vytvoří parser.
     * 
     * @param client objekt klienta
     * @param message zpráva
     */
    public AParser(TcpClient client, Message message) {
        CLIENT = client;
        MESSAGE = message;
    }
    
    /**
     * Vrátí výsledek zpracování a aktualizuje stav klienta.
     * 
     * @return výsledek zpracování
     */
    public abstract String updateClient();
    
}
