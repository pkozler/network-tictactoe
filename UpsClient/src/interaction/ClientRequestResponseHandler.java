package interaction;

import communication.ConnectionManager;
import communication.Message;
import configuration.Config;

/**
 *
 * @author Petr Kozler
 */
public class ClientRequestResponseHandler {
    
    private ConnectionManager connectionManager;
    
    public ClientRequestResponseHandler(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }
    
    public void handleActivationResponse(Message message) {
        if (!message.hasArgs()) {
            return;
        }
        
        String response = message.getNextArg();
        
        if (response.equals(Config.MSG_FAIL)) {
            if (!message.hasNextArg()) {
                return;
            }
            
            String errorType = message.getNextArg();
        }
        
        if (response.equals(Config.MSG_OK)) {
            if (!message.hasNextArg()) {
                return;
            }
            
            int id = 0;
            
            try {
                id = Integer.parseInt(message.getNextArg());
            }
            catch (NumberFormatException ex) {
                return;
            }
            
            connectionManager.activate(id);
        }
    }
    
    public void handleDeactivationResponse(Message message) {
        if (!message.hasArgs()) {
            return;
        }
        
        String response = message.getNextArg();
        
        if (response.equals(Config.MSG_OK)) {
            connectionManager.deactivate();
            
            return;
        }
        
        // chyba při odstraňování hráče na serveru
    }
    
    public void handleCreateGameResponse(Message message) {
        
    }
    
    public void handleJoinGameResponse(Message message) {
        
    }
    
    public void handleLeaveGameResponse(Message message) {
        
    }
    
    public void handleStartGameResponse(Message message) {
        
    }
    
    public void handlePlayGameResponse(Message message) {
        
    }
    
}
