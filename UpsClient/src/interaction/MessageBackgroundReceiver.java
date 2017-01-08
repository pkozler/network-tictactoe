package interaction;

import communication.TcpClient;
import communication.TcpMessage;
import communication.ClientNotLoggedException;
import communication.ClientAlreadyLoggedException;
import communication.tokens.InvalidMessageArgsException;
import communication.containers.MissingListHeaderException;
import communication.tokens.MissingMessageArgsException;
import communication.tokens.UnknownMessageTypeException;
import configuration.Protocol;
import interaction.receiving.AParser;
import interaction.receiving.responses.LoginResponseParser;
import interaction.receiving.responses.CreateGameResponseParser;
import interaction.receiving.responses.LogoutResponseParser;
import interaction.receiving.responses.JoinGameResponseParser;
import interaction.receiving.responses.LeaveGameResponseParser;
import interaction.receiving.responses.PlayGameResponseParser;
import interaction.receiving.responses.StartGameResponseParser;
import interaction.receiving.updates.CurrentGameDetailUpdateParser;
import interaction.receiving.updates.GameListUpdateParser;
import interaction.receiving.updates.PlayerListUpdateParser;
import javax.swing.SwingUtilities;
import visualisation.components.CurrentGamePanel;
import visualisation.components.GameListPanel;
import visualisation.components.PlayerListPanel;
import visualisation.components.StatusBarPanel;

/**
 * Třída MessageBackgroundReceiver představuje čtecí vlákno
 * pro příjem odpovědí a notifikací serveru.
 * 
 * @author Petr Kozler
 */
public class MessageBackgroundReceiver implements Runnable {

    /**
     * objekt klienta
     */
    private final TcpClient CLIENT;
    
    /**
     * panel stavového řádku
     */
    private final StatusBarPanel STATUS_BAR_PANEL;
    
    /**
     * panel seznamu hráčů
     */
    private final PlayerListPanel PLAYER_LIST_PANEL;
    
    /**
     * panel seznamu her
     */
    private final GameListPanel GAME_LIST_PANEL;
    
    /**
     * panel herní místnosti
     */
    private final CurrentGamePanel CURRENT_GAME_PANEL;
    
    /**
     * parser seznamu hráčů
     */
    private PlayerListUpdateParser playerListUpdateParser;
    
    /**
     * parser seznamu her
     */
    private GameListUpdateParser gameListUpdateParser;
    
    /**
     * parser seznamu hráčů
     */
    private CurrentGameDetailUpdateParser currentGameDetailUpdateParser;
    
    /**
     * poslední použitý parser
     */
    private AParser currentParser;
    
    /**
     * Vytvoří přijímač zpráv.
     * 
     * @param client objekt klienta
     * @param statusBarPanel panel stavového řádku
     * @param playerListPanel panel seznamu hráčů
     * @param gameListPanel panel seznamu her
     * @param currentGamePanel panel herní místnosti
     */
    public MessageBackgroundReceiver(TcpClient client,
            StatusBarPanel statusBarPanel, PlayerListPanel playerListPanel,
            GameListPanel gameListPanel, CurrentGamePanel currentGamePanel) {
        CLIENT = client;
        STATUS_BAR_PANEL = statusBarPanel;
        PLAYER_LIST_PANEL = playerListPanel;
        GAME_LIST_PANEL = gameListPanel;
        CURRENT_GAME_PANEL = currentGamePanel;
        playerListUpdateParser = null;
        gameListUpdateParser = null;
        currentGameDetailUpdateParser = null;
    }
    
    /**
     * Provádí čtení a zpracovávání zpráv serveru.
     */
    @Override
    public void run() {
        handleCreatedConnection();
        
        while (CLIENT.isConnected()) {
            handleReceivedMessage();
        }
        
        handleLostConnection();
    }
    
    /**
     * Zobrazí zprávu o navázání spojení.
     */
    private void handleCreatedConnection() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                STATUS_BAR_PANEL.printSendingStatus("Spojení navázáno.");
            }

        });
    }
    
    /**
     * Zobrazí zprávu o zrušení spojení.
     */
    private void handleLostConnection() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                STATUS_BAR_PANEL.printSendingStatus("Spojení ztraceno.");
            }

        });
    }
    
    /**
     * Přijme a zpracuje zprávu serveru.
     */
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
                        STATUS_BAR_PANEL.printReceivingStatus(status);
                    }
                }

            };
        }
        catch (Exception e) {
            runnable = new Runnable() {

                @Override
                public void run() {
                    STATUS_BAR_PANEL.printErrorStatus(
                            "Chyba příjmu zprávy: %s", e.getClass().getSimpleName());
                }

            };
        }
        
        SwingUtilities.invokeLater(runnable);
    }
    
    /**
     * Spustí parser zprávy na pozadí.
     * 
     * @param message zpráva
     * @throws MissingListHeaderException
     * @throws UnknownMessageTypeException
     * @throws ClientAlreadyLoggedException
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException
     * @throws ClientNotLoggedException 
     */
    private void executeParserOnBackground(TcpMessage message) throws MissingListHeaderException,
            UnknownMessageTypeException, ClientAlreadyLoggedException,
            InvalidMessageArgsException, MissingMessageArgsException, ClientNotLoggedException {
        // přijatá zpráva je odpověď na testování odezvy
        if (message.isPing()) {
            return;
        }
        
        currentParser = handleResponse(message);
        
        // přijatá zpráva není odpověď na požadavek
        if (currentParser == null) {
            currentParser = handleUpdate(message);
        }
        
        // přijatá zpráva není ani aktualizace stavu
        if (currentParser == null) {
            throw new UnknownMessageTypeException();
        }
        
        // validace přijaté zprávy podle stavu přihlášení klienta
        validateByLoginStatus(message, CLIENT.isLoggedIn());
    }
    
    /**
     * Zpracuje odpověď serveru na požadavek.
     * 
     * @param message zpráva
     * @return parser zprávy
     * @throws UnknownMessageTypeException
     * @throws ClientAlreadyLoggedException
     * @throws MissingListHeaderException
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException
     * @throws ClientNotLoggedException 
     */
    private AParser handleResponse(TcpMessage message)
            throws UnknownMessageTypeException, ClientAlreadyLoggedException,
            MissingListHeaderException, InvalidMessageArgsException,
            MissingMessageArgsException, ClientNotLoggedException {
        if (message.isTypeOf(Protocol.MSG_LOGIN_CLIENT)) {
            return new LoginResponseParser(CLIENT,
                    PLAYER_LIST_PANEL, GAME_LIST_PANEL, STATUS_BAR_PANEL, message);
        }
        
        if (message.isTypeOf(Protocol.MSG_LOGOUT_CLIENT)) {
            return new LogoutResponseParser(CLIENT,
                    PLAYER_LIST_PANEL, GAME_LIST_PANEL, STATUS_BAR_PANEL, message);
        }
        
        if (message.isTypeOf(Protocol.MSG_CREATE_GAME)) {
            return new CreateGameResponseParser(CLIENT,
                    PLAYER_LIST_PANEL, GAME_LIST_PANEL, STATUS_BAR_PANEL, message);
        }
        
        if (message.isTypeOf(Protocol.MSG_JOIN_GAME)) {
            return new JoinGameResponseParser(CLIENT,
                    PLAYER_LIST_PANEL, GAME_LIST_PANEL, STATUS_BAR_PANEL, message);
        }
        
        if (message.isTypeOf(Protocol.MSG_LEAVE_GAME)) {
            return new LeaveGameResponseParser(CLIENT,
                    PLAYER_LIST_PANEL, GAME_LIST_PANEL, STATUS_BAR_PANEL, message);
        }
        
        if (message.isTypeOf(Protocol.MSG_START_GAME)) {
            return new StartGameResponseParser(CLIENT,
                    PLAYER_LIST_PANEL, GAME_LIST_PANEL, STATUS_BAR_PANEL, message);
        }
        
        if (message.isTypeOf(Protocol.MSG_PLAY_GAME)) {
            return new PlayGameResponseParser(CLIENT,
                    PLAYER_LIST_PANEL, GAME_LIST_PANEL, STATUS_BAR_PANEL, message);
        }
        
        return null;
    }
    
    /**
     * Zpracuje notifikaci serveru o změně stavu.
     * 
     * @param message zpráva
     * @return parser zprávy
     * @throws MissingListHeaderException
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    private AParser handleUpdate(TcpMessage message)
            throws MissingListHeaderException, InvalidMessageArgsException, MissingMessageArgsException {
        if (message.isTypeOf(Protocol.MSG_PLAYER_LIST)) {
            return new PlayerListUpdateParser(CLIENT,
                    PLAYER_LIST_PANEL, message);
        }
        
        if (message.isTypeOf(Protocol.MSG_GAME_LIST)) {
            return new GameListUpdateParser(CLIENT,
                    GAME_LIST_PANEL, message);
        }
        
        if (message.isTypeOf(Protocol.MSG_GAME_DETAIL)) {
            return new CurrentGameDetailUpdateParser(CLIENT,
                    PLAYER_LIST_PANEL, GAME_LIST_PANEL, CURRENT_GAME_PANEL, message);
        }
        
        if (message.isTypeOf(Protocol.MSG_PLAYER_LIST_ITEM)) {
            if (playerListUpdateParser == null) {
                throw new MissingListHeaderException();
            }
            
            playerListUpdateParser.parseNextItemMessage(message);
            
            if (!playerListUpdateParser.hasNextItemMessage()) {
                playerListUpdateParser = null;
            }
            
            return playerListUpdateParser;
        }
        
        if (message.isTypeOf(Protocol.MSG_GAME_LIST_ITEM)) {
            if (gameListUpdateParser == null) {
                throw new MissingListHeaderException();
            }
            
            gameListUpdateParser.parseNextItemMessage(message);
            
            if (!gameListUpdateParser.hasNextItemMessage()) {
                gameListUpdateParser = null;
            }
            
            return gameListUpdateParser;
        }
        
        if (message.isTypeOf(Protocol.MSG_GAME_PLAYER)) {
            if (currentGameDetailUpdateParser == null) {
                throw new MissingListHeaderException();
            }
            
            currentGameDetailUpdateParser.parseNextItemMessage(message);
            
            if (!currentGameDetailUpdateParser.hasNextItemMessage()) {
                currentGameDetailUpdateParser = null;
            }
            
            return currentGameDetailUpdateParser;
        }
        
        return null;
    }
    
    /**
     * Ověří platnost zprávy na základě přihlášení klienta.
     * 
     * @param message zpráva
     * @param logged příznak přihlášení klienta
     * @throws ClientAlreadyLoggedException
     * @throws ClientNotLoggedException 
     */
    private void validateByLoginStatus(TcpMessage message, boolean logged)
            throws ClientAlreadyLoggedException, ClientNotLoggedException {
        if (logged) {
            if (message.isTypeOf(Protocol.MSG_LOGIN_CLIENT)) {
                throw new ClientAlreadyLoggedException();
                
            }
            
            return;
        }
        
        if (message.isTypeOf(Protocol.MSG_LOGIN_CLIENT)
                || message.isTypeOf(Protocol.MSG_PLAYER_LIST)
                || message.isTypeOf(Protocol.MSG_PLAYER_LIST_ITEM)
                || message.isTypeOf(Protocol.MSG_GAME_LIST)
                || message.isTypeOf(Protocol.MSG_GAME_LIST_ITEM)) {
            return;
        }
        
        throw new ClientNotLoggedException();
    }
    
}
