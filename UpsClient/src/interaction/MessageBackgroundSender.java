package interaction;

import communication.TcpClient;
import communication.TcpMessage;
import interaction.sending.ARequestBuilder;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.SwingUtilities;
import visualisation.components.StatusBarPanel;

/**
 * Třída MessageBackgroundSender 
 * 
 * @author Petr Kozler
 */
public class MessageBackgroundSender implements Runnable {

    /**
     * 
     */
    private final Queue<ARequestBuilder> requestQueue = new LinkedList<>();
    
    /**
     * 
     */
    private final TcpClient CLIENT;
    
    /**
     * 
     */
    private final StatusBarPanel STATUS_BAR_PANEL;
    
    /**
     * 
     * 
     * @param client
     * @param statusBarPanel 
     */
    public MessageBackgroundSender(TcpClient client, StatusBarPanel statusBarPanel) {
        CLIENT = client;
        STATUS_BAR_PANEL = statusBarPanel;
    }
    
    /**
     * 
     * 
     * @param builder 
     */
    public void enqueueMessageBuilder(ARequestBuilder builder) {
        requestQueue.add(builder);
    }
    
    /**
     * 
     * 
     */
    @Override
    public void run() {
        while (CLIENT.isConnected()) {
            handleMessageToSend();
        }
        
        requestQueue.clear();
    }
    
    /**
     * 
     * 
     */
    private void handleMessageToSend() {
        if (requestQueue.isEmpty()) {
            return;
        }

        ARequestBuilder builder = requestQueue.poll();

        TcpMessage message;
        String status;
        Runnable runnable;
        
        if (builder == null || builder.getMessage() == null) {
            message = new TcpMessage();
            status = null;
        }
        else {
            message = builder.getMessage();
            status = builder.getStatus();
        }
        
        try {
            CLIENT.sendMessage(message);

            runnable = new Runnable() {

                @Override
                public void run() {
                    if (status != null) {
                        STATUS_BAR_PANEL.printSendingStatus(builder.getStatus());
                    }
                }

            };
        }
        catch (Exception e) {
            runnable = new Runnable() {

                @Override
                public void run() {
                    STATUS_BAR_PANEL.printErrorStatus(
                            "Chyba odesílání zprávy: %s", e.getClass().getSimpleName());
                }

            };
        }
        
        SwingUtilities.invokeLater(runnable);
    }
    
}
