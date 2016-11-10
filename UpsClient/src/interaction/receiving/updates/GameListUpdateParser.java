package interaction.receiving.updates;

import communication.ConnectionManager;
import communication.Message;
import communication.containers.GameInfo;
import communication.tokens.InvalidMessageArgsException;
import communication.tokens.MissingMessageArgsException;
import configuration.Config;
import interaction.receiving.AUpdateParser;
import java.util.ArrayList;
import visualisation.components.GameListPanel;

/**
 *
 * @author Petr Kozler
 */
public class GameListUpdateParser extends AUpdateParser {

    private final GameListPanel GAME_LIST_PANEL;
    private final int ITEM_COUNT;
    private final ArrayList<GameInfo> GAME_LIST;
    
    public GameListUpdateParser(ConnectionManager connectionManager,
            GameListPanel gameListPanel, Message message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        super(connectionManager, message);
        
        GAME_LIST_PANEL = gameListPanel;
        ITEM_COUNT = message.getNextIntArg(0);
        GAME_LIST = new ArrayList<>(ITEM_COUNT);
    }

    @Override
    public boolean hasNextItemMessage() {
        return GAME_LIST.size() < ITEM_COUNT;
    }

    @Override
    public void parseNextItemMessage(Message itemMessage)
            throws InvalidMessageArgsException, MissingMessageArgsException {
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

    @Override
    public String getStatusAndUpdateGUI() {
        GAME_LIST_PANEL.setGameList(GAME_LIST);
        
        return "Byl aktualizován seznam herních místností.";
    }
    
}
