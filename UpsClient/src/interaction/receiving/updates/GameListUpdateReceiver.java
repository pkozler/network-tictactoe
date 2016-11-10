package interaction.receiving.updates;

import communication.ConnectionManager;
import communication.Message;
import communication.containers.GameInfo;
import communication.tokens.InvalidMessageArgsException;
import communication.tokens.MissingMessageArgsException;
import configuration.Config;
import interaction.receiving.AReceiver;
import interaction.receiving.IListReceiver;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import visualisation.components.GameListPanel;
import visualisation.components.StatusBarPanel;

/**
 *
 * @author Petr Kozler
 */
public class GameListUpdateReceiver extends AReceiver<ArrayList<GameInfo>, Object> implements IListReceiver {

    private final GameListPanel GAME_LIST_PANEL;
    private final int ITEM_COUNT;
    private final ArrayList<GameInfo> GAME_LIST;
    private boolean valid = true;
    
    public GameListUpdateReceiver(ConnectionManager connectionManager, StatusBarPanel statusBarPanel,
            GameListPanel gameListPanel, Message message) {
        super(connectionManager, message);
        
        GAME_LIST_PANEL = gameListPanel;
        int itemCount = 0;
        
        try {
            itemCount = message.getNextIntArg(0);
        }
        catch (InvalidMessageArgsException | MissingMessageArgsException ex) {
            valid = false;
        }
        
        ITEM_COUNT = itemCount;
        GAME_LIST = new ArrayList<>(ITEM_COUNT);
    }

    @Override
    protected ArrayList<GameInfo> doInBackground() throws Exception {
        return GAME_LIST;
    }

    @Override
    protected void done() {
        try {
            if (!valid) {
                return;
            }
            
            ArrayList<GameInfo> gameList = get();
            GAME_LIST_PANEL.setGameList(gameList);
        }
        catch (InterruptedException | ExecutionException ex) {
            // TODO
        }
    }

    @Override
    public boolean hasNextItem() {
        return GAME_LIST.size() < ITEM_COUNT;
    }

    @Override
    public void addNextItem(Message itemMessage) {
        try{
            int id = itemMessage.getNextIntArg(1);
            String name = itemMessage.getNextArg();
            byte boardSize = itemMessage.getNextByteArg(Config.MIN_BOARD_SIZE, Config.MAX_BOARD_SIZE);
            byte playerCount = itemMessage.getNextByteArg(Config.MIN_PLAYERS_SIZE, Config.MAX_PLAYERS_SIZE);
            byte cellCount = itemMessage.getNextByteArg(Config.MIN_CELL_COUNT, Config.MAX_CELL_COUNT);
            byte playerCounter = itemMessage.getNextByteArg((byte) 1, playerCount);
            int roundCounter = itemMessage.getNextIntArg(0);
            boolean active = itemMessage.getNextBoolArg();
            GAME_LIST.add(new GameInfo(id, name, boardSize, playerCount, cellCount, playerCounter, roundCounter, active));
        }
        catch (InvalidMessageArgsException | MissingMessageArgsException ex) {
            valid = false;
        }
    }
    
}
