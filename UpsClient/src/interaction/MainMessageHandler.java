package interaction;

import communication.ConnectionManager;
import communication.InvalidMessageException;
import communication.Message;
import configuration.Config;
import java.io.IOException;
import javax.swing.SwingWorker;

/**
 *
 * @author Petr Kozler
 */
public class ResponseReceiveWorker extends SwingWorker<Object, Object> {

    private ConnectionManager connectionManager;
    private ClientRequestResponseHandler requestHandler;
    private ServerUpdateResponseHandler updateHandler;
    
    public ResponseReceiveWorker(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        requestHandler = new ClientRequestResponseHandler(connectionManager);
        updateHandler = new ServerUpdateResponseHandler(connectionManager);
    }
    
    private void parseReceivedMessage() {
        try {
            Message response = connectionManager.receiveMessage();
            
            if (!response.hasType()) {
                // přijata odpověď na testování odezvy - spojení je v pořádku
                return;
            }
            
            if (!connectionManager.isActive()) {
                switch (response.getType()) {
                    case Config.MSG_ACTIVATE_CLIENT: {
                        requestHandler.handleActivationResponse(response);
                        break;
                    }
                }
                
                return;
            }
            
            switch (response.getType()) {
                case Config.MSG_DEACTIVATE_CLIENT: {
                    requestHandler.handleDeactivationResponse(response);
                    break;
                }
                case Config.MSG_CREATE_GAME: {
                    requestHandler.handleCreateGameResponse(response);
                    break;
                }
                case Config.MSG_JOIN_GAME: {
                    requestHandler.handleJoinGameResponse(response);
                    break;
                }
                case Config.MSG_LEAVE_GAME: {
                    requestHandler.handleLeaveGameResponse(response);
                    break;
                }
                case Config.MSG_START_GAME: {
                    requestHandler.handleStartGameResponse(response);
                    break;
                }
                case Config.MSG_PLAY_GAME: {
                    requestHandler.handlePlayGameResponse(response);
                    break;
                }
                case Config.MSG_CLIENT_LIST: {
                    updateHandler.handleClientListUpdate();
                    break;
                }
                case Config.MSG_GAME_LIST: {
                    updateHandler.handleGameListUpdate();
                    break;
                }
                case Config.MSG_GAME_STATUS: {
                    updateHandler.handleGameStatusUpdate();
                    break;
                }
                case Config.MSG_SERVER_SHUTDOWN: {
                    updateHandler.handleServerShutdownUpdate();
                    break;
                }
                default: {
                    // TODO prijata neplatna zprava
                    break;
                }
            }
        }
        catch (IOException ex) {
            // TODO chyba pri nacteni zpravy
        }
        catch (InvalidMessageException ex) {
            // TODO neplatna zprava - bude ignorovana
        }
    }
    
    @Override
    protected Object doInBackground() throws Exception {
        while (connectionManager.isActive()) {
            parseReceivedMessage();
        }
        
        return null;
    }
}
