package interaction;

import communication.TcpClient;
import communication.TcpMessage;
import communication.ClientNotLoggedException;
import communication.ClientAlreadyLoggedException;
import communication.InvalidMessageStringLengthException;
import communication.tokens.InvalidMessageArgsException;
import communication.containers.MissingListHeaderException;
import communication.tokens.MissingMessageArgsException;
import communication.tokens.UnknownMessageTypeException;
import configuration.Protocol;
import interaction.receiving.AParser;
import interaction.receiving.AUpdateParser;
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
import java.io.IOException;
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
    public final TcpClient CLIENT;
    
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
    }
    
    /**
     * Provádí čtení a zpracovávání zpráv serveru.
     */
    @Override
    public void run() {
        while (CLIENT.isConnected()) {
            handleReceivedMessage();
        }
    }
    
    /**
     * Přijme a zpracuje zprávu serveru.
     */
    private void handleReceivedMessage() {
        try {
            TcpMessage message = CLIENT.receiveMessage();
            
            // přijatá zpráva je odpověď na testování odezvy
            if (message.isPing()) {
                return;
            }
        
            executeParserOnBackground(message);
            
            if (currentParser == null) {
                return;
            }
            
            final AParser parser = currentParser;
            final String status = parser.updateClient();
            final boolean updateCompleted = parser instanceof AUpdateParser
                        && ((AUpdateParser) parser).hasAllItems();
            
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    if (updateCompleted) {
                        ((AUpdateParser) parser).updateGui();
                    }
                    
                    if (status != null) {
                        STATUS_BAR_PANEL.printReceivingStatus(status);
                    }
                }

            });
            
            if (currentParser instanceof AUpdateParser
                    && ((AUpdateParser) currentParser).hasAllItems()) {
                currentParser = null;
            }
        }
        catch (final IOException | InvalidMessageStringLengthException e) {
            try {
                CLIENT.disconnect();
            }
            catch (IOException ex) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        STATUS_BAR_PANEL.printErrorStatus(
                            "Chyba při rušení socketu: %s", e.getClass().getSimpleName());
                    }

                });
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    STATUS_BAR_PANEL.printErrorStatus(
                            "Chyba příjmu zprávy: %s", e.getClass().getSimpleName());
                }

            });
        }
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
        boolean validMessageType = handleResponse(message);
        
        // přijatá zpráva není odpověď na požadavek
        if (!validMessageType) {
            validMessageType = handleUpdate(message);
        }
        
        // přijatá zpráva není ani aktualizace stavu
        if (!validMessageType) {
            throw new UnknownMessageTypeException();
        }
    }
    
    /**
     * Zpracuje odpověď serveru na požadavek.
     * 
     * @param message zpráva
     * @return parser zprávy
     * @throws ClientAlreadyLoggedException
     * @throws MissingListHeaderException
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException
     * @throws ClientNotLoggedException 
     */
    private boolean handleResponse(TcpMessage message) throws
            ClientAlreadyLoggedException, InvalidMessageArgsException,
            MissingMessageArgsException, ClientNotLoggedException {
        if (message.isTypeOf(Protocol.MSG_LOGIN_CLIENT)) {
            currentParser = new LoginResponseParser(CLIENT,
                    PLAYER_LIST_PANEL, GAME_LIST_PANEL, STATUS_BAR_PANEL, message);
            
            return true;
        }
        
        if (message.isTypeOf(Protocol.MSG_LOGOUT_CLIENT)) {
            currentParser = new LogoutResponseParser(CLIENT,
                    PLAYER_LIST_PANEL, GAME_LIST_PANEL, STATUS_BAR_PANEL, message);
            
            return true;
        }
        
        if (message.isTypeOf(Protocol.MSG_CREATE_GAME)) {
            currentParser = new CreateGameResponseParser(CLIENT,
                    PLAYER_LIST_PANEL, GAME_LIST_PANEL, STATUS_BAR_PANEL, message);
            
            return true;
        }
        
        if (message.isTypeOf(Protocol.MSG_JOIN_GAME)) {
            currentParser = new JoinGameResponseParser(CLIENT,
                    PLAYER_LIST_PANEL, GAME_LIST_PANEL, STATUS_BAR_PANEL, message);
            
            return true;
        }
        
        if (message.isTypeOf(Protocol.MSG_LEAVE_GAME)) {
            currentParser = new LeaveGameResponseParser(CLIENT,
                    PLAYER_LIST_PANEL, GAME_LIST_PANEL, STATUS_BAR_PANEL, message);
            
            return true;
        }
        
        if (message.isTypeOf(Protocol.MSG_START_GAME)) {
            currentParser = new StartGameResponseParser(CLIENT,
                    PLAYER_LIST_PANEL, GAME_LIST_PANEL, STATUS_BAR_PANEL, message);
            
            return true;
        }
        
        if (message.isTypeOf(Protocol.MSG_PLAY_GAME)) {
            currentParser = new PlayGameResponseParser(CLIENT,
                    PLAYER_LIST_PANEL, GAME_LIST_PANEL, STATUS_BAR_PANEL, message);
            
            return true;
        }
        
        return false;
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
    private boolean handleUpdate(TcpMessage message)
            throws MissingListHeaderException, InvalidMessageArgsException, MissingMessageArgsException {
        if (message.isTypeOf(Protocol.MSG_PLAYER_LIST)) {
            currentParser = new PlayerListUpdateParser(CLIENT,
                    PLAYER_LIST_PANEL, message);
            
            return true;
        }
        
        if (message.isTypeOf(Protocol.MSG_GAME_LIST)) {
            currentParser = new GameListUpdateParser(CLIENT,
                    GAME_LIST_PANEL, message);
            
            return true;
        }
        
        if (message.isTypeOf(Protocol.MSG_GAME_DETAIL)) {
            currentParser = new CurrentGameDetailUpdateParser(CLIENT,
                    PLAYER_LIST_PANEL, GAME_LIST_PANEL, CURRENT_GAME_PANEL, message);
            
            return true;
        }
        
        if (message.isTypeOf(Protocol.MSG_PLAYER_LIST_ITEM)) {
            if (currentParser == null || !(currentParser instanceof PlayerListUpdateParser)) {
                throw new MissingListHeaderException();
            }
            
            PlayerListUpdateParser playerListUpdateParser = (PlayerListUpdateParser) currentParser;
            
            if (!playerListUpdateParser.hasAllItems()) {
                playerListUpdateParser.parseNextItemMessage(message);
            }
            
            return true;
        }
        
        if (message.isTypeOf(Protocol.MSG_GAME_LIST_ITEM)) {
            if (currentParser == null || !(currentParser instanceof GameListUpdateParser)) {
                throw new MissingListHeaderException();
            }
            
            GameListUpdateParser gameListUpdateParser = (GameListUpdateParser) currentParser;
            
            if (!gameListUpdateParser.hasAllItems()) {
                gameListUpdateParser.parseNextItemMessage(message);
            }
            
            return true;
        }
        
        if (message.isTypeOf(Protocol.MSG_GAME_PLAYER)) {
            if (currentParser == null || !(currentParser instanceof CurrentGameDetailUpdateParser)) {
                throw new MissingListHeaderException();
            }
            
            CurrentGameDetailUpdateParser currentGameDetailUpdateParser = (CurrentGameDetailUpdateParser) currentParser;
            
            if (!currentGameDetailUpdateParser.hasAllItems()) {
                currentGameDetailUpdateParser.parseNextItemMessage(message);
            }
            
            return true;
        }
        
        return false;
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
