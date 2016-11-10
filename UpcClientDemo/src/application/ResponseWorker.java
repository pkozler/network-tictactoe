package application;

import java.util.concurrent.ExecutionException;
import javax.swing.JTextField;

/**
 * Třída ResponseWorker představuje spouštěč operace příjmu odpovědi serveru
 * na pozadí, jejíž výsledek je zobrazen do příslušné textové komponenty GUI.
 * 
 * @author Petr Kozler
 */
public class ResponseWorker extends ABackgroundWorker {

    /**
     * Textové pole pro výpis odpovědi.
     */
    private JTextField receivedTF;

    /**
     * Text přijaté odpovědi.
     */
    private String received;
    
    /**
     * Indikátor příjmu další zprávy.
     */
    private boolean isOk;
    
    /**
     * Vytvoří přijímač odpovědi.
     * 
     * @param client objekt klienta
     * @param receivedTF textové pole pro odpověď
     */
    public ResponseWorker(IClient client, JTextField connectionStatusTF, JTextField receivedTF) {
        super(client, connectionStatusTF);
        
        this.receivedTF = receivedTF;
    }
    
    /**
     * Spustí síťovou operaci příjmu zprávy na pozadí.
     * 
     * @return chybová zpráva, pokud nastala chyba
     * 
     * @throws Exception výjimka, pokud se nepodařilo spustit operaci
     */
    @Override
    protected String doInBackground() throws Exception {
        try {
            Message message = client.receiveMessage();
            
            if (message.isPing()) {
                return null;
            }
            
            received = message.toString();
            
            if (!client.isConnected()) {
                return "Odpojen";
            }
            
            if (client.isLogged()) {
                return "Přihlášen jako " + client.getLogin();
            }
            
            return null;
        } catch (ClientException e) {
            return e.getErrorMessage();
        } finally {
            isOk = client.isConnected();
        }
    }

    /**
     * Zobrazí výsledek operace příjmu zprávy do GUI.
     */
    @Override
    protected void done() {
        try {
            if (received != null) {
                receivedTF.setText(received);
            }
            
            if (get() != null) {
                connectionStatusTF.setText(get());
            }
        } catch (InterruptedException | ExecutionException e ) {
            connectionStatusTF.setText("Chyba spuštění komunikace: "
                    + e.getLocalizedMessage());
        }
        
        if (isOk) {
            new ResponseWorker(client, connectionStatusTF, receivedTF).execute();
        }
    }
    
}
