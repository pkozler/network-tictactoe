package interaction.receiving;

import communication.TcpClient;
import communication.TcpMessage;
import communication.tokens.InvalidMessageArgsException;
import communication.tokens.MissingMessageArgsException;

/**
 * Abstraktní třída AUpdateParser představuje obecný parser notifikací
 * o změnách stavu přijatých ze serveru.
 * 
 * @author Petr Kozler
 */
public abstract class AUpdateParser extends AParser {
    
    /**
     * Vytvoří parser notifikace.
     * 
     * @param client objekt klienta
     * @param message zpráva
     */
    public AUpdateParser(TcpClient client, TcpMessage message) {
        super(client, message);
    }
    
    /**
     * Otestuje, zda seznam zpráv obsahuje další položku.
     * 
     * @return true, pokud má seznam další položku, jinak false
     */
    public abstract boolean hasAllItems();
    
    /**
     * Zpracuje další položku seznamu zpráv.
     * 
     * @param itemMessage zpráva
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    public abstract void parseNextItemMessage(TcpMessage itemMessage)
            throws InvalidMessageArgsException, MissingMessageArgsException;
    
    /**
     * Aktualizuje GUI.
     */
    public abstract void updateGui();
    
}
