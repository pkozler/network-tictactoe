package interaction;

import communication.TcpClient;
import communication.Message;
import communication.ClientNotLoggedException;
import communication.ClientAlreadyLoggedException;
import communication.InvalidMessageStringLengthException;
import communication.tokens.InvalidMessageArgsException;
import communication.containers.MissingListHeaderException;
import communication.tokens.MissingMessageArgsException;
import communication.tokens.ResponseWithoutRequestException;
import communication.tokens.UnknownMessageTypeException;
import communication.tokens.WrongResponseTypeException;
import interaction.receiving.AParser;
import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import javax.swing.SwingUtilities;
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
    
    private final RequestResponseHandler REQUEST_RESPONSE_HANDLER;
    
    private final ListUpdateHandler LIST_UPDATE_HANDLER;
    
    /**
     * Vytvoří přijímač zpráv.
     * 
     * @param client objekt klienta
     * @param statusBarPanel panel stavového řádku
     * @param requestResponseHandler objekt pro zpracování odpovědí na požadavky
     * @param listUpdateHandler objekt pro zpracování notifikací o změnách
     */
    public MessageBackgroundReceiver(TcpClient client, StatusBarPanel statusBarPanel,
            RequestResponseHandler requestResponseHandler, ListUpdateHandler listUpdateHandler) {
        CLIENT = client;
        STATUS_BAR_PANEL = statusBarPanel;
        REQUEST_RESPONSE_HANDLER = requestResponseHandler;
        LIST_UPDATE_HANDLER = listUpdateHandler;
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
        Message message;
        
        try {
            // příjem zprávy
            message = CLIENT.receiveMessage();
        }
        catch (IOException | InvalidMessageStringLengthException ex) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    if (!(ex instanceof SocketException || ex instanceof EOFException)) {
                        STATUS_BAR_PANEL.printErrorStatus(
                            "Chyba příjmu zprávy: %s", ex.getClass().getSimpleName());
                    }
                    ex.printStackTrace();
                }

            });
            
            message = null;
        }
        
        try {
            // uzavření spojení při chybě
            if (message == null) {
                CLIENT.disconnect();
                
                return;
            }
        }
        catch (final IOException ex) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    STATUS_BAR_PANEL.printErrorStatus(
                        "Chyba při uzavírání socketu: %s", ex.getClass().getSimpleName());
                    ex.printStackTrace();
                }

            });
            
            return;
        }
        
        final AParser parser;
        
        try {
            // zpracování zprávy odpovídajícím parserem
            parser = parseOnBackground(message);
        }
        catch (MissingListHeaderException | UnknownMessageTypeException |
                ClientAlreadyLoggedException | InvalidMessageArgsException |
                MissingMessageArgsException | ClientNotLoggedException |
                ResponseWithoutRequestException | WrongResponseTypeException ex) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    STATUS_BAR_PANEL.printErrorStatus(
                        "Chyba parsování zprávy: %s", ex.getClass().getSimpleName());
                    ex.printStackTrace();
                }

            });
            
            return;
        }
        
        if (parser == null) {
            return;
        }
        
        // změna stavu komunikační vrstvy aplikace
        final String status = parser.updateClient();

        if (status == null) {
            return;
        }
        
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (status != null) {
                    STATUS_BAR_PANEL.printReceivingStatus(status);
                }
            }

        });
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
    private AParser parseOnBackground(Message message) throws MissingListHeaderException,
            UnknownMessageTypeException, ClientAlreadyLoggedException,
            InvalidMessageArgsException, MissingMessageArgsException, ClientNotLoggedException,
            ResponseWithoutRequestException, WrongResponseTypeException {
        // přijatá zpráva je odpověď na testování odezvy
        if (message.isPing()) {
            return null;
        }
        
        AParser parser = REQUEST_RESPONSE_HANDLER.handleResponse(message);
        
        // přijatá zpráva je odpověď na požadavek
        if (parser != null) {
            return parser;
        }
        
        parser = LIST_UPDATE_HANDLER.handleUpdate(message);
        
        // přijatá zpráva je aktualizace stavu
        if (parser != null) {
            return parser;
        }
        
        // přijatá zpráva je neznámého typu
        throw new UnknownMessageTypeException();
    }
    
}
