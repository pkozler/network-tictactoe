package interaction;

import communication.ConnectionManager;
import communication.Message;
import communication.containers.InvalidListItemException;
import communication.containers.MissingListHeaderException;
import communication.tokens.InvalidMessageArgsException;
import communication.tokens.MissingMessageArgsException;
import configuration.Config;
import interaction.receiving.AReceiver;
import interaction.receiving.IListReceiver;
import interaction.receiving.updates.CurrentGameDetailUpdateReceiver;
import interaction.receiving.updates.GameListUpdateReceiver;
import interaction.receiving.updates.PlayerListUpdateReceiver;
import visualisation.components.CurrentGameWindow;
import visualisation.components.GameListPanel;
import visualisation.components.PlayerListPanel;
import visualisation.components.StatusBarPanel;

/**
 *
 * @author Petr Kozler
 */
public class UpdateHandler {
    
    private final ConnectionManager CONNECTION_MANAGER;
    private final StatusBarPanel STATUS_BAR_PANEL;
    private final PlayerListPanel PLAYER_LIST_PANEL;
    private final GameListPanel GAME_LIST_PANEL;
    private final CurrentGameWindow CURRENT_GAME_WINDOW;
    
    public UpdateHandler(ConnectionManager connectionManager, StatusBarPanel statusBarPanel,
            PlayerListPanel playerListPanel, GameListPanel gameListPanel, CurrentGameWindow currentGameWindow) {
        CONNECTION_MANAGER = connectionManager;
        STATUS_BAR_PANEL = statusBarPanel;
        PLAYER_LIST_PANEL = playerListPanel;
        GAME_LIST_PANEL = gameListPanel;
        CURRENT_GAME_WINDOW = currentGameWindow;
    }
    
    public AReceiver handleUpdate(Message message) throws MissingListHeaderException {
        if (message.isTypeOf(Config.MSG_PLAYER_LIST)) {
            return new PlayerListUpdateReceiver(CONNECTION_MANAGER,
                    STATUS_BAR_PANEL, PLAYER_LIST_PANEL, message);
        }
        else if (message.isTypeOf(Config.MSG_GAME_LIST)) {
            return new GameListUpdateReceiver(CONNECTION_MANAGER,
                    STATUS_BAR_PANEL, GAME_LIST_PANEL, message);
        }
        else if (message.isTypeOf(Config.MSG_GAME_DETAIL)) {
            return new CurrentGameDetailUpdateReceiver(CONNECTION_MANAGER,
                    STATUS_BAR_PANEL, PLAYER_LIST_PANEL, GAME_LIST_PANEL, CURRENT_GAME_WINDOW, message);
        }
        else if (message.isTypeOf(Config.MSG_PLAYER_LIST_ITEM)
                || message.isTypeOf(Config.MSG_GAME_LIST_ITEM)
                || message.isTypeOf(Config.MSG_GAME_PLAYER)) {
            throw new MissingListHeaderException();
        }
        
        return null;
    }
    
    public boolean isListUpdateInProgress(AReceiver currentReceiver) {
        if (currentReceiver != null && currentReceiver instanceof IListReceiver) {
            return ((IListReceiver) currentReceiver).hasNextItem();
        }
        
        return false;
    }
    
    public void forwardListItemMessage(AReceiver currentReceiver, Message message)
            throws InvalidListItemException, InvalidMessageArgsException, MissingMessageArgsException {
        IListReceiver currentListReceiver = (IListReceiver) currentReceiver;
        
        if ((message.isTypeOf(Config.MSG_PLAYER_LIST_ITEM) && currentListReceiver instanceof PlayerListUpdateReceiver)
         || (message.isTypeOf(Config.MSG_GAME_LIST_ITEM) && currentListReceiver instanceof GameListUpdateReceiver)
         || (message.isTypeOf(Config.MSG_GAME_PLAYER) && currentListReceiver instanceof CurrentGameDetailUpdateReceiver)) {
            currentListReceiver.addNextItem(message);
        }
        else {
            throw new InvalidListItemException();
        }
    }
    
}
