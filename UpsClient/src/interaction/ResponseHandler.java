package interaction;

import communication.ClientAlreadyActiveException;
import communication.ClientNotActivatedException;
import communication.ConnectionManager;
import communication.Message;
import communication.containers.MissingListHeaderException;
import communication.tokens.InvalidMessageArgsException;
import communication.tokens.MissingMessageArgsException;
import communication.tokens.UnknownMessageTypeException;
import configuration.Config;
import interaction.receiving.AReceiver;
import interaction.receiving.responses.ActivationResponseReceiver;
import interaction.receiving.responses.CreateGameResponseReceiver;
import interaction.receiving.responses.DeactivationResponseReceiver;
import interaction.receiving.responses.JoinGameResponseReceiver;
import interaction.receiving.responses.LeaveGameResponseReceiver;
import interaction.receiving.responses.PlayGameResponseReceiver;
import interaction.receiving.responses.StartGameResponseReceiver;

/**
 *
 * @author Petr Kozler
 */
public class ResponseHandler {
    
    private final ConnectionManager CONNECTION_MANAGER;
    
    public ResponseHandler(ConnectionManager connectionManager) {
        CONNECTION_MANAGER = connectionManager;
    }
    
    public AReceiver handleActivation(Message message) throws ClientNotActivatedException {
        if (message.isTypeOf(Config.MSG_ACTIVATE_CLIENT)) {
            return new ActivationResponseReceiver(CONNECTION_MANAGER, message);
        }
        else {
            throw new ClientNotActivatedException();
        }
    }
    
    public AReceiver handleResponse(Message message)
            throws UnknownMessageTypeException, ClientAlreadyActiveException,
            MissingListHeaderException, InvalidMessageArgsException, MissingMessageArgsException {
        if (message.isTypeOf(Config.MSG_ACTIVATE_CLIENT)) {
            throw new ClientAlreadyActiveException();
        }
        else if (message.isTypeOf(Config.MSG_DEACTIVATE_CLIENT)) {
            return new DeactivationResponseReceiver(CONNECTION_MANAGER, message);
        }
        else if (message.isTypeOf(Config.MSG_CREATE_GAME)) {
            return new CreateGameResponseReceiver(CONNECTION_MANAGER, message);
        }
        else if (message.isTypeOf(Config.MSG_JOIN_GAME)) {
            return new JoinGameResponseReceiver(CONNECTION_MANAGER, message);
        }
        else if (message.isTypeOf(Config.MSG_LEAVE_GAME)) {
            return new LeaveGameResponseReceiver(CONNECTION_MANAGER, message);
        }
        else if (message.isTypeOf(Config.MSG_START_GAME)) {
            return new StartGameResponseReceiver(CONNECTION_MANAGER, message);
        }
        else if (message.isTypeOf(Config.MSG_PLAY_GAME)) {
            return new PlayGameResponseReceiver(CONNECTION_MANAGER, message);
        }
        
        return null;
    }
    
}
