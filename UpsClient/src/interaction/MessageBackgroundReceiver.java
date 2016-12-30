package interaction;

import communication.TcpClient;
import communication.TcpMessage;
import communication.ClientNotActivatedException;
import communication.ClientAlreadyActiveException;
import communication.containers.InvalidListItemException;
import communication.tokens.InvalidMessageArgsException;
import communication.containers.MissingListHeaderException;
import communication.tokens.MissingMessageArgsException;
import communication.tokens.UnknownMessageTypeException;
import configuration.Config;
import interaction.receiving.AParser;
import interaction.receiving.AUpdateParser;
import interaction.receiving.responses.ActivationResponseParser;
import interaction.receiving.responses.CreateGameResponseParser;
import interaction.receiving.responses.DeactivationResponseParser;
import interaction.receiving.responses.JoinGameResponseParser;
import interaction.receiving.responses.LeaveGameResponseParser;
import interaction.receiving.responses.PlayGameResponseParser;
import interaction.receiving.updates.CurrentGameDetailUpdateParser;
import interaction.receiving.updates.GameListUpdateParser;
import interaction.receiving.updates.PlayerListUpdateParser;
import javax.swing.SwingUtilities;
import visualisation.components.CurrentGameWindow;
import visualisation.components.GameListPanel;
import visualisation.components.PlayerListPanel;
import visualisation.components.StatusBarPanel;

/**
 *
 * @author Petr Kozler
 */
public class MessageBackgroundReceiver implements Runnable {

    private final TcpClient CLIENT;
    private final StatusBarPanel STATUS_BAR_PANEL;
    private final PlayerListPanel PLAYER_LIST_PANEL;
    private final GameListPanel GAME_LIST_PANEL;
    private final CurrentGameWindow CURRENT_GAME_WINDOW;
    
    private AParser currentParser;
    
    public MessageBackgroundReceiver(TcpClient client,
            StatusBarPanel statusBarPanel, PlayerListPanel playerListPanel,
            GameListPanel gameListPanel, CurrentGameWindow currentGameWindow) {
        CLIENT = client;
        STATUS_BAR_PANEL = statusBarPanel;
        PLAYER_LIST_PANEL = playerListPanel;
        GAME_LIST_PANEL = gameListPanel;
        CURRENT_GAME_WINDOW = currentGameWindow;
        currentParser = null;
    }
    
    @Override
    public void run() {
        handleCreatedConnection();
        
        while (CLIENT.isConnected()) {
            handleReceivedMessage();
        }
        
        handleLostConnection();
    }
    
    private void handleCreatedConnection() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                STATUS_BAR_PANEL.printStatus("Spojení navázáno.");
            }

        });
    }
    
    private void handleLostConnection() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                STATUS_BAR_PANEL.printStatus("Spojení ztraceno.");
            }

        });
    }
    
    private void handleReceivedMessage() {
        Runnable runnable;
        
        try {
            TcpMessage message = CLIENT.receiveMessage();
            executeParserOnBackground(message);
            
            runnable = new Runnable() {

                @Override
                public void run() {
                    String status = currentParser.getStatusAndUpdateGUI();
                    
                    if (status != null) {
                        STATUS_BAR_PANEL.printStatus(status);
                    }
                }

            };
        }
        catch (Exception e) {
            runnable = new Runnable() {

                @Override
                public void run() {
                    STATUS_BAR_PANEL.printStatus(
                            "Při příjmu zprávy ze serveru se vyskytla výjimka typu: %s",
                            e.getClass().getSimpleName());
                }

            };
        }
        
        SwingUtilities.invokeLater(runnable);
    }
    
    private void executeParserOnBackground(TcpMessage message) throws MissingListHeaderException,
            UnknownMessageTypeException, ClientAlreadyActiveException,
            InvalidMessageArgsException, MissingMessageArgsException, InvalidListItemException, ClientNotActivatedException {
        // přijatá zpráva je testování odezvy
        if (message.isPing()) {
            return;
        }

        // přijatá zpráva je potvrzení o přihlášení
        if (!CLIENT.isLoggedIn()) {
            currentParser = handleResponse(message, false);
            
            return;
        }
        
        // přijatá zpráva je položka právě aktualizovaného seznamu
        if (currentParser != null && currentParser instanceof AUpdateParser
                && ((AUpdateParser) currentParser).hasNextItemMessage()) {
            parseListItemMessage(((AUpdateParser) currentParser), message);
            
            return;
        }
        
        // přijatá zpráva je jiného typu
        AParser parser = handleResponse(message, true);
        
        if (parser != null) {
            currentParser = parser;
            
            return;
        }
        
        parser = handleUpdate(message);
        
        if (parser != null) {
            currentParser = parser;
            
            return;
        }
        
        throw new UnknownMessageTypeException();
    }
    
    private AParser handleResponse(TcpMessage message, boolean active)
            throws UnknownMessageTypeException, ClientAlreadyActiveException,
            MissingListHeaderException, InvalidMessageArgsException,
            MissingMessageArgsException, ClientNotActivatedException {
        if (!active) {
            if (message.isTypeOf(Config.MSG_ACTIVATE_CLIENT)) {
                return new ActivationResponseParser(CLIENT, GAME_LIST_PANEL, STATUS_BAR_PANEL, message);
            }
            
            throw new ClientNotActivatedException();
        }
        
        if (message.isTypeOf(Config.MSG_ACTIVATE_CLIENT)) {
            throw new ClientAlreadyActiveException();
        }
        else if (message.isTypeOf(Config.MSG_DEACTIVATE_CLIENT)) {
            return new DeactivationResponseParser(CLIENT, GAME_LIST_PANEL, STATUS_BAR_PANEL, message);
        }
        else if (message.isTypeOf(Config.MSG_CREATE_GAME)) {
            return new CreateGameResponseParser(CLIENT, GAME_LIST_PANEL, STATUS_BAR_PANEL, message);
        }
        else if (message.isTypeOf(Config.MSG_JOIN_GAME)) {
            return new JoinGameResponseParser(CLIENT, GAME_LIST_PANEL, STATUS_BAR_PANEL, message);
        }
        else if (message.isTypeOf(Config.MSG_LEAVE_GAME)) {
            return new LeaveGameResponseParser(CLIENT, GAME_LIST_PANEL, STATUS_BAR_PANEL, message);
        }
        else if (message.isTypeOf(Config.MSG_PLAY_GAME)) {
            return new PlayGameResponseParser(CLIENT, GAME_LIST_PANEL, STATUS_BAR_PANEL, message);
        }
        
        return null;
    }
    
    private AParser handleUpdate(TcpMessage message)
            throws MissingListHeaderException, InvalidMessageArgsException, MissingMessageArgsException {
        if (message.isTypeOf(Config.MSG_PLAYER_LIST)) {
            return new PlayerListUpdateParser(CLIENT,
                    PLAYER_LIST_PANEL, message);
        }
        else if (message.isTypeOf(Config.MSG_GAME_LIST)) {
            return new GameListUpdateParser(CLIENT,
                    GAME_LIST_PANEL, message);
        }
        else if (message.isTypeOf(Config.MSG_GAME_DETAIL)) {
            return new CurrentGameDetailUpdateParser(CLIENT,
                    PLAYER_LIST_PANEL, GAME_LIST_PANEL, CURRENT_GAME_WINDOW, message);
        }
        else if (message.isTypeOf(Config.MSG_PLAYER_LIST_ITEM)
                || message.isTypeOf(Config.MSG_GAME_LIST_ITEM)
                || message.isTypeOf(Config.MSG_GAME_PLAYER)) {
            throw new MissingListHeaderException();
        }
        
        return null;
    }
    
    private void parseListItemMessage(AUpdateParser currentListReceiver, TcpMessage message)
            throws InvalidListItemException, InvalidMessageArgsException, MissingMessageArgsException {
        if ((message.isTypeOf(Config.MSG_PLAYER_LIST_ITEM) && currentListReceiver instanceof PlayerListUpdateParser)
         || (message.isTypeOf(Config.MSG_GAME_LIST_ITEM) && currentListReceiver instanceof GameListUpdateParser)
         || (message.isTypeOf(Config.MSG_GAME_PLAYER) && currentListReceiver instanceof CurrentGameDetailUpdateParser)) {
            currentListReceiver.parseNextItemMessage(message);
        }
        else {
            throw new InvalidListItemException();
        }
    }
    
}
