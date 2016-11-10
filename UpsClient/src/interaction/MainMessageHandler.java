package interaction;

import communication.ConnectionManager;
import communication.InvalidMessageStringLengthException;
import communication.Message;
import communication.ClientNotActivatedException;
import communication.ClientAlreadyActiveException;
import communication.containers.InvalidListItemException;
import communication.tokens.InvalidMessageArgsException;
import communication.containers.MissingListHeaderException;
import communication.tokens.MissingMessageArgsException;
import communication.tokens.UnknownMessageTypeException;
import interaction.receiving.AReceiver;
import interaction.receiving.IListReceiver;
import java.io.IOException;

/**
 *
 * @author Petr Kozler
 */
public class MainMessageHandler implements Runnable {

    private final ConnectionManager CONNECTION_MANAGER;
    private final ResponseHandler RESPONSE_HANDLER;
    private final UpdateHandler UPDATE_HANDLER;
    
    private AReceiver currentReceiver = null;
    
    public MainMessageHandler(ConnectionManager connectionManager,
            ResponseHandler responseHandler, UpdateHandler updateHandler) {
        CONNECTION_MANAGER = connectionManager;
        RESPONSE_HANDLER = responseHandler;
        UPDATE_HANDLER = updateHandler;
    }
    
    private AReceiver handleResonseOrUpdate(Message message) throws MissingListHeaderException,
            UnknownMessageTypeException, ClientAlreadyActiveException,
            InvalidMessageArgsException, MissingMessageArgsException {
        AReceiver receiver = RESPONSE_HANDLER.handleResponse(message);
        
        if (receiver != null) {
            return receiver;
        }
        
        return UPDATE_HANDLER.handleUpdate(message);
    }
    
    private void handleReceivedMessage() {
        try {
            Message message = CONNECTION_MANAGER.receiveMessage();
            
            if (message.isPing()) {
                return;
            }
            
            if (!CONNECTION_MANAGER.isActive()) {
                currentReceiver = RESPONSE_HANDLER.handleActivation(message);
            }
            else if (UPDATE_HANDLER.isListUpdateInProgress(currentReceiver)) {
                UPDATE_HANDLER.forwardListItemMessage(currentReceiver, message);
            }
            else {
                AReceiver receiver = handleResonseOrUpdate(message);
                
                if (receiver == null) {
                    throw new UnknownMessageTypeException();
                }
                
                currentReceiver = receiver;
            }
            
            tryExecuteReceiver();
        }
        catch (IOException ex) {
            // TODO chyba pri nacteni zpravy
        }
        catch (InvalidMessageStringLengthException ex) {
            // TODO neplatna delka zpravy - zprava bude ignorovana
        }
        catch (InvalidMessageArgsException ex) {
            // TODO neplatne argumenty zpravy
        }
        catch (MissingMessageArgsException ex) {
            // TODO chybejici argumenty zpravy
        }
        catch (ClientNotActivatedException ex) {
            // TODO hrac neni aktivni
        }
        catch (ClientAlreadyActiveException ex) {
            // TODO hrac je jiz aktivni
        }
        catch (MissingListHeaderException ex) {
            // TODO prijata polozka seznamu bez predchazejici hlavicky
        }
        catch (InvalidListItemException ex) {
            // TODO prijata neplatna polozka aktualne nacitaneho seznamu
        }
        catch (UnknownMessageTypeException ex) {
            // TODO prijat neznamy typ zpravy
        }
    }
    
    private void tryExecuteReceiver() {
        if (currentReceiver == null) {
            return;
        }
        
        if (!(currentReceiver instanceof IListReceiver)) {
            currentReceiver.execute();
            
            return;
        }
        
        IListReceiver currentListReceiver = (IListReceiver) currentReceiver;
        
        if (!currentListReceiver.hasNextItem()) {
            currentReceiver.execute();
        }
    }
    
    @Override
    public void run() {
        while (CONNECTION_MANAGER.isActive()) {
            handleReceivedMessage();
        }
    }
    
}
