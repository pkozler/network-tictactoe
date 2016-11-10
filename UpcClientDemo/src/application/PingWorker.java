package application;

import java.util.concurrent.ExecutionException;
import javax.swing.JTextField;

/**
 *
 * @author Petr Kozler
 */
public class PingWorker extends ABackgroundWorker {

    /**
     * Indikátor úspěšného ověření.
     */
    private boolean isOk;
    
    public PingWorker(IClient client, JTextField connectionStatusTF) {
        super(client, connectionStatusTF);
    }

    @Override
    protected String doInBackground() throws Exception {
        try {
            Message message = new Message();
            client.sendMessage(message);
            Thread.sleep(Configuration.PING_PERIOD_MILLIS);
            
            return null;
        } catch (ClientException e) {
            return e.getErrorMessage();
        } finally {
            isOk = client.isConnected();
        }
    }
    
    @Override
    protected void done() {
        try {
            if (get() != null) {
                connectionStatusTF.setText(get());
            }
        } catch (InterruptedException | ExecutionException e ) {
            connectionStatusTF.setText("Chyba spuštění komunikace: "
                    + e.getLocalizedMessage());
        }
        
        if (isOk) {
            new PingWorker(client, connectionStatusTF).execute();
        }
    }

}
