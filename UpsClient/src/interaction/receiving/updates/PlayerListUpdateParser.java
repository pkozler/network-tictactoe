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
 * Třída PlayerListUpdateParser 
 * 
 * @author Petr Kozler
 */
public class PlayerListUpdateParser extends AUpdateParser {

    /**
     * 
     */
    private final PlayerListPanel PLAYER_LIST_PANEL;
    
    /**
     * 
     */
    private final int ITEM_COUNT;
    
    /**
     * 
     */
    private final ArrayList<PlayerInfo> PLAYER_LIST;
    
    /**
     * 
     * 
     * @param client
     * @param playerListPanel
     * @param message
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
     * 
     * 
     * @return 
     */
    @Override
    public boolean hasNextItemMessage() {
        return PLAYER_LIST.size() < ITEM_COUNT;
    }

    /**
     * 
     * 
     * @param itemMessage
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
     * 
     * 
     * @return 
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
