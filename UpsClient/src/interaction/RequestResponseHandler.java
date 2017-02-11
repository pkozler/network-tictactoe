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
import java.util.concurrent.locks.ReentrantLock;

/**
 * Třída RequestResponseHandler představuje pomocnou komponentu
 * pro zpracování odpovědí serveru na požadavky klienta.
 * 
 * @author Petr Kozler
 */
public class RequestResponseHandler {
    
    /**
     * objekt klienta
     */
    public final TcpClient CLIENT;
    
    /**
     * fronta odeslaných požadavků čekajících na zpracování serverem
     */
    private final Queue<ARequestBuilder> PENDING_REQUEST_QUEUE = new LinkedList<>();
    
    /**
     * zámek přístupu k frontě odeslaných požadavků
     */
    private final ReentrantLock lock = new ReentrantLock();
    
    /**
     * Vytvoří objekt pro zpracování odpovědí na požadavky.
     * 
     * @param client objekt klienta
     */
    public RequestResponseHandler(TcpClient client) {
        CLIENT = client;
    }
    
    /**
     * Vytvoří parser pro zpracování přijaté odpovědi serveru, ve kterém
     * spáruje tuto odpověď s příslušným odeslaným požadavkem klienta z fronty
     * požadavků čekajících na zpracování, a následně vrátí tento parser.
     * 
     * @param message zpráva
     * @return parser
     * @throws ClientAlreadyLoggedException
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException
     * @throws ClientNotLoggedException
     * @throws ResponseWithoutRequestException
     * @throws WrongResponseTypeException 
     */
    public AParser handleResponse(Message message) throws
            ClientAlreadyLoggedException, InvalidMessageArgsException,
            MissingMessageArgsException, ClientNotLoggedException,
            ResponseWithoutRequestException, WrongResponseTypeException {
        // výběr parseru
        AResponseParser parser = selectParser(message);
        
        if (parser != null) {
            // přiřazení požadavku k odpovědi
            parser.assignRequest(getPendingRequest());
        }
        
        return parser;
    }
    
    /**
     * Přidá odeslaný požadavek na konec fronty požadavků čekajících
     * na zpracování.
     * 
     * @param requestBuilder odeslaný požadavek
     */
    public void addPendingRequest(ARequestBuilder requestBuilder) {
        lock.lock();
        try {
            if (requestBuilder != null) {
                PENDING_REQUEST_QUEUE.add(requestBuilder);
            }
        }
        finally {
            lock.unlock();
        }
    }
    
    /**
     * Vyčistí frontu odeslaných požadavků při ukončení spojení.
     */
    public void clearPendingRequests() {
        lock.lock();
        try {
            PENDING_REQUEST_QUEUE.clear();
        }
        finally {
            lock.unlock();
        }
    }
    
    /**
     * Vybere příslušný parser podle typu přijaté odpovědi serveru
     * a vytvoří instanci tohoto parseru pro její zpracování.
     * 
     * @param message zpráva
     * @return parser
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
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
    
    /**
     * Vyjme odeslaný požadavek ze začátku fronty požadavků čekajících
     * na zpracování.
     * 
     * @return odeslaný požadavek
     */
    private ARequestBuilder getPendingRequest() {
        lock.lock();
        try {
            return PENDING_REQUEST_QUEUE.isEmpty() ?
                    null : PENDING_REQUEST_QUEUE.poll();
        }
        finally {
            lock.unlock();
        }
    }
    
}
