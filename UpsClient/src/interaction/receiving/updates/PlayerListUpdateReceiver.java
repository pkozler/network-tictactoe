package interaction.receiving.updates;

import communication.ConnectionManager;
import communication.Message;
import communication.containers.PlayerInfo;
import communication.tokens.InvalidMessageArgsException;
import communication.tokens.MissingMessageArgsException;
import interaction.receiving.AReceiver;
import interaction.receiving.IListReceiver;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import visualisation.components.PlayerListPanel;
import visualisation.components.StatusBarPanel;

/**
 *
 * @author Petr Kozler
 */
public class PlayerListUpdateReceiver extends AReceiver<ArrayList<PlayerInfo>, Object> implements IListReceiver {

    private final PlayerListPanel PLAYER_LIST_PANEL;
    private final int ITEM_COUNT;
    private final ArrayList<PlayerInfo> PLAYER_LIST;
    private boolean valid = true;
    
    public PlayerListUpdateReceiver(ConnectionManager connectionManager, StatusBarPanel statusBarPanel,
            PlayerListPanel playerListPanel, Message message) {
        super(connectionManager, message);
        
        PLAYER_LIST_PANEL = playerListPanel;
        int itemCount = 0;
        
        try {
            itemCount = message.getNextIntArg(0);
        }
        catch (InvalidMessageArgsException | MissingMessageArgsException ex) {
            valid = false;
        }
        
        ITEM_COUNT = itemCount;
        PLAYER_LIST = new ArrayList<>(ITEM_COUNT);
    }

    @Override
    protected ArrayList<PlayerInfo> doInBackground() throws Exception {
        return PLAYER_LIST;
    }

    @Override
    protected void done() {
        try {
            if (!valid) {
                return;
            }
            
            ArrayList<PlayerInfo> playerList = get();
            PLAYER_LIST_PANEL.setPlayerList(playerList);
        }
        catch (InterruptedException | ExecutionException ex) {
            // TODO
        }
    }

    @Override
    public boolean hasNextItem() {
        return PLAYER_LIST.size() < ITEM_COUNT;
    }

    @Override
    public void addNextItem(Message itemMessage) {
        try {
            int id = itemMessage.getNextIntArg(1);
            String nick = itemMessage.getNextArg();
            int totalScore = itemMessage.getNextIntArg(0);
            PLAYER_LIST.add(new PlayerInfo(id, nick, totalScore));
        }
        catch (InvalidMessageArgsException | MissingMessageArgsException ex) {
            valid = false;
        }
    }
    
}
