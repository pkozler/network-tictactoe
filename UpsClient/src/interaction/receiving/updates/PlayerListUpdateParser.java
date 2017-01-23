package interaction.receiving.updates;

import communication.TcpClient;
import communication.Message;
import communication.containers.PlayerInfo;
import communication.tokens.InvalidMessageArgsException;
import communication.tokens.MissingMessageArgsException;
import interaction.receiving.AUpdateParser;
import java.util.ArrayList;

/**
 * Třída PlayerListUpdateParser představuje parser notifikace
 * serveru o změně seznamu hráčů.
 * 
 * @author Petr Kozler
 */
public class PlayerListUpdateParser extends AUpdateParser {

    /**
     * počet prvků
     */
    private final int ITEM_COUNT;
    
    /**
     * seznam hráčů
     */
    private final ArrayList<PlayerInfo> PLAYER_LIST;
    
    /**
     * Vytvoří parser seznamu hráčů.
     * 
     * @param client objekt klienta
     * @param message zpráva
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    public PlayerListUpdateParser(TcpClient client, Message message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        super(client, message);
        
        ITEM_COUNT = message.getNextIntArg(0);
        PLAYER_LIST = new ArrayList<>();
    }

    /**
     * Otestuje, zda seznam hráčů již obsahuje všechny položky.
     * 
     * @return true, pokud seznam obsahuje všechny položky, jinak false
     */
    @Override
    public boolean hasAllItems() {
        return PLAYER_LIST.size() == ITEM_COUNT;
    }

    /**
     * Zpracuje další položku seznamu hráčů ze seznamu zpráv.
     * 
     * @param itemMessage zpráva
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    @Override
    public void parseNextItemMessage(Message itemMessage)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        int id = itemMessage.getNextIntArg(1);
        String nick = itemMessage.getNextArg();
        int totalScore = itemMessage.getNextIntArg(0);
        PLAYER_LIST.add(new PlayerInfo(id, nick, totalScore));
    }

    /**
     * Aktualizuje objekty pro komunikaci a vrátí výsledek zpracování zprávy.
     * 
     * @return výsledek
     */
    @Override
    public String updateClient() {
        if (!hasAllItems()) {
            return null;
        }
        
        CLIENT.setPlayerList(PLAYER_LIST);
        
        return "Aktualizace seznamu přihlášených klientů byla dokončena";
    }
    
}
