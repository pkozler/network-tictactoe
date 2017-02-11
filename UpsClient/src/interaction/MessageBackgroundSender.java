package interaction;

import communication.TcpClient;
import communication.Message;
import interaction.sending.ARequestBuilder;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.SwingUtilities;
import visualisation.components.StatusBarPanel;

/**
 * Třída MessageBackgroundSender představuje vlákno pro odesílání
 * požadavků serveru uložených ve frontě.
 * 
 * @author Petr Kozler
 */
public class MessageBackgroundSender implements Runnable {

    /**
     * objekt klienta
     */
    private final TcpClient CLIENT;
    
    /**
     * fronta požadavků k odeslání
     */
    private final Queue<ARequestBuilder> REQUEST_QUEUE = new LinkedList<>();
    
    /**
     * panel stavového řádku
     */
    private final StatusBarPanel STATUS_BAR_PANEL;
    
    /**
     * objekt pro zpracování odpovědí na požadavky
     */
    private final RequestResponseHandler REQUEST_RESPONSE_HANDLER;
    
    /**
     * Vytvoří vysílač zpráv.
     * 
     * @param client objekt klienta
     * @param statusBarPanel panel stavového řádku
     * @param requestResponseHandler objekt pro zpracování odpovědí na požadavky
     */
    public MessageBackgroundSender(TcpClient client, StatusBarPanel statusBarPanel,
            RequestResponseHandler requestResponseHandler) {
        CLIENT = client;
        STATUS_BAR_PANEL = statusBarPanel;
        REQUEST_RESPONSE_HANDLER = requestResponseHandler;
    }
    
    /**
     * Uloží požadavek klienta.
     * 
     * @param builder požadavek
     */
    public void enqueueMessageBuilder(ARequestBuilder builder) {
        REQUEST_QUEUE.add(builder);
    }
    
    /**
     * Provádí pozorování fronty požadavků, které postupně odesílá na server.
     */
    @Override
    public void run() {
        while (CLIENT.isConnected()) {
            handleMessageToSend();
        }
        
        REQUEST_QUEUE.clear();
        REQUEST_RESPONSE_HANDLER.clearPendingRequests();
    }
    
    /**
     * Sestaví zprávu představující požadavek klienta a odešle ji na server.
     */
    private void handleMessageToSend() {
        if (REQUEST_QUEUE.isEmpty()) {
            return;
        }
        
        // vyjmutí požadavku k odeslání ze začátku fronty
        final ARequestBuilder builder = REQUEST_QUEUE.poll();

        Message message;
        final String status;
        
        if (builder == null || builder.getMessage() == null) {
            // vytvoření prázdné zprávy pro testování odezvy
            message = new Message();
            status = null;
        }
        else {
            // sestavení zprávy představující požadavek
            message = builder.getMessage();
            status = builder.getStatus();
        }
        
        // zjištění stavu připojení před pokusem o odeslání
        if (!CLIENT.isConnected()) {
            return;
        }
        
        try {
            // odeslání sestavené zprávy na server
            CLIENT.sendMessage(message);
            // vložení zprávy do fronty požadavků čekajících na zpracování
            REQUEST_RESPONSE_HANDLER.addPendingRequest(builder);
            
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    if (status != null) {
                        STATUS_BAR_PANEL.printSendingStatus(builder.getStatus());
                    }
                }

            });
        }
        catch (final Exception e) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    STATUS_BAR_PANEL.printErrorStatus(
                            "Chyba odesílání zprávy: %s", e.getClass().getSimpleName());
                    e.printStackTrace();
                }

            });
        }
    }
    
}
