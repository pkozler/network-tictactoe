package interaction.receiving.updates;

import communication.TcpClient;
import communication.TcpMessage;
import communication.containers.PlayerInfo;
import communication.tokens.InvalidMessageArgsException;
import communication.tokens.MissingMessageArgsException;
import interaction.receiving.AUpdateParser;
import java.util.ArrayList;
import visualisation.components.PlayerListPanel;

/**
 * Třída PlayerListUpdateParser představuje parser notifikace
 * serveru o změně seznamu hráčů.
 * 
 * @author Petr Kozler
 */
public class PlayerListUpdateParser extends AUpdateParser {

    /**
     * panel seznamu hráčů
     */
    private final PlayerListPanel PLAYER_LIST_PANEL;
    
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
     * @param playerListPanel panel seznamu hráčů
     * @param message zpráva
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    public PlayerListUpdateParser(TcpClient client,
            PlayerListPanel playerListPanel, TcpMessage message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        super(client, message);
        
        PLAYER_LIST_PANEL = playerListPanel;
        ITEM_COUNT = message.getNextIntArg(0);
        PLAYER_LIST = new ArrayList<>(ITEM_COUNT);
    }

    /**
     * Otestuje, zda seznam zpráv obsahuje další položku seznamu hráčů.
     * 
     * @return true, pokud má seznam další položku, jinak false
     */
    @Override
    public boolean hasNextItemMessage() {
        return PLAYER_LIST.size() < ITEM_COUNT;
    }

    /**
     * Zpracuje další položku seznamu hráčů ze seznamu zpráv.
     * 
     * @param itemMessage zpráva
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    @Override
    public void parseNextItemMessage(TcpMessage itemMessage)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        int id = itemMessage.getNextIntArg(1);
        String nick = itemMessage.getNextArg();
        int totalScore = itemMessage.getNextIntArg(0);
        PLAYER_LIST.add(new PlayerInfo(id, nick, totalScore));
    }

    /**
     * Vrátí výsledek zpracování zprávy a aktualizuje seznam hráčů v GUI.
     * 
     * @return výsledek
     */
    @Override
    public String getStatusAndUpdateGUI() {
        if (hasNextItemMessage()) {
            return String.format("Probíhá aktualizace seznamu přihlášených klientů (zbývá %d položek)",
                    ITEM_COUNT - PLAYER_LIST.size());
        }
        
        PLAYER_LIST_PANEL.setPlayerList(PLAYER_LIST);
        
        return "Aktualizace seznamu přihlášených klientů byla dokončena";
    }
    
}
