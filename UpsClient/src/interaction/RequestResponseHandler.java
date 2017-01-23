package interaction;

import communication.ClientAlreadyLoggedException;
import communication.ClientNotLoggedException;
import communication.Message;
import communication.TcpClient;
import communication.tokens.InvalidMessageArgsException;
import communication.tokens.MissingMessageArgsException;
import communication.tokens.ResponseWithoutRequestException;
import communication.tokens.WrongResponseTypeException;
import configuration.Protocol;
import interaction.receiving.AParser;
import interaction.receiving.AResponseParser;
import interaction.receiving.responses.CreateGameResponseParser;
import interaction.receiving.responses.JoinGameResponseParser;
import interaction.receiving.responses.LeaveGameResponseParser;
import interaction.receiving.responses.LoginResponseParser;
import interaction.receiving.responses.LogoutResponseParser;
import interaction.receiving.responses.PlayGameResponseParser;
import interaction.receiving.responses.StartGameResponseParser;
import interaction.sending.ARequestBuilder;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Petr Kozler
 */
public class RequestResponseHandler {
    
    /**
     * objekt klienta
     */
    public final TcpClient CLIENT;
    
    /**
     * fronta požadavků
     */
    private final Queue<ARequestBuilder> PENDING_REQUEST_QUEUE = new LinkedList<>();
    
    public RequestResponseHandler(TcpClient client) {
        CLIENT = client;
    }
    
    public AParser handleResponse(Message message) throws
            ClientAlreadyLoggedException, InvalidMessageArgsException,
            MissingMessageArgsException, ClientNotLoggedException,
            ResponseWithoutRequestException, WrongResponseTypeException {
        AResponseParser parser = selectParser(message);
        
        if (parser != null) {
            parser.assignRequest(getPendingRequest());
        }
        
        return parser;
    }
    
    private AResponseParser selectParser(Message message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        if (message.isTypeOf(Protocol.MSG_LOGIN_CLIENT)) {
            return new LoginResponseParser(CLIENT, message);
        }
        
        if (message.isTypeOf(Protocol.MSG_LOGOUT_CLIENT)) {
            return new LogoutResponseParser(CLIENT, message);
        }
        
        if (message.isTypeOf(Protocol.MSG_CREATE_GAME)) {
            return new CreateGameResponseParser(CLIENT, message);
        }
        
        if (message.isTypeOf(Protocol.MSG_JOIN_GAME)) {
            return new JoinGameResponseParser(CLIENT, message);
        }
        
        if (message.isTypeOf(Protocol.MSG_LEAVE_GAME)) {
            return new LeaveGameResponseParser(CLIENT, message);
        }
        
        if (message.isTypeOf(Protocol.MSG_START_GAME)) {
            return new StartGameResponseParser(CLIENT, message);
        }
        
        if (message.isTypeOf(Protocol.MSG_PLAY_GAME)) {
            return new PlayGameResponseParser(CLIENT, message);
        }
        
        return null;
    }
    
    public void addPendingRequest(ARequestBuilder requestBuilder) {
        if (requestBuilder != null) {
            PENDING_REQUEST_QUEUE.add(requestBuilder);
        }
    }
    
    public void clearPendingRequests() {
        PENDING_REQUEST_QUEUE.clear();
    }
    
    private ARequestBuilder getPendingRequest() {
        if (PENDING_REQUEST_QUEUE.isEmpty()) {
            return null;
        }
        
        return PENDING_REQUEST_QUEUE.poll();
    }
    
}
