package interaction.receiving.updates;

import communication.ConnectionManager;
import communication.Message;
import communication.containers.PlayerInfo;
import communication.tokens.InvalidMessageArgsException;
import communication.tokens.MissingMessageArgsException;
import interaction.receiving.AUpdateParser;
import java.util.ArrayList;
import visualisation.components.PlayerListPanel;

/**
 *
 * @author Petr Kozler
 */
public class PlayerListUpdateParser extends AUpdateParser {

    private final PlayerListPanel PLAYER_LIST_PANEL;
    private final int ITEM_COUNT;
    private final ArrayList<PlayerInfo> PLAYER_LIST;
    
    public PlayerListUpdateParser(ConnectionManager connectionManager,
            PlayerListPanel playerListPanel, Message message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        super(connectionManager, message);
        
        PLAYER_LIST_PANEL = playerListPanel;
        ITEM_COUNT = message.getNextIntArg(0);
        PLAYER_LIST = new ArrayList<>(ITEM_COUNT);
    }

    @Override
    public boolean hasNextItemMessage() {
        return PLAYER_LIST.size() < ITEM_COUNT;
    }

    @Override
    public void parseNextItemMessage(Message itemMessage)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        int id = itemMessage.getNextIntArg(1);
        String nick = itemMessage.getNextArg();
        int totalScore = itemMessage.getNextIntArg(0);
        PLAYER_LIST.add(new PlayerInfo(id, nick, totalScore));
    }

    @Override
    public String getStatusAndUpdateGUI() {
        PLAYER_LIST_PANEL.setPlayerList(PLAYER_LIST);
        
        return "Byl aktualizován seznam přihlášených klientů.";
    }
    
}
