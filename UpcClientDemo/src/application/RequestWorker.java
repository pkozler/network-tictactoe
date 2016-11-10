package application;

import java.util.concurrent.ExecutionException;
import javax.swing.JTextField;

/**
 * Třída RequestWorker představuje spouštěč operace odeslání požadavku serveru
 * na pozadí, jejíž výsledek je zobrazen do příslušné textové komponenty GUI.
 * 
 * @author Petr Kozler
 */
public class RequestWorker extends ABackgroundWorker {

    /**
     * Textové pole pro zadání zprávy.
     */
    private JTextField sentTF;
    
    /**
     * Text odeslaného požadavku.
     */
    private String sent;
    
    /**
     * Vytvoří vysílač požadavku.
     * 
     * @param client objekt klienta
     * @param connectionStatusTF textové pole pro výpis výsledku
     * @param sentTF textové pole pro požadavek
     */
    public RequestWorker(IClient client, JTextField connectionStatusTF, JTextField sentTF) {
        super(client, connectionStatusTF);
        
        this.sentTF = sentTF;
        
        sent = sentTF.getText();
    }
    
    /**
     * Spustí síťovou operaci odeslání zprávy na pozadí.
     * 
     * @return chybová zpráva, pokud nastala chyba
     * 
     * @throws Exception výjimka, pokud se nepodařilo spustit operaci
     */
    @Override
    protected String doInBackground() throws Exception {
        try {
            Message message = new Message(sent);
            client.sendMessage(message);
            
            if (!client.isConnected()) {
                return "Odpojen";
            }
            
            if (client.isLogged()) {
                return "Přihlášen jako " + client.getLogin();
            }
            
            return null;
        } catch (ClientException e) {
            return e.getErrorMessage();
        }
    }

    /**
     * Zobrazí výsledek operace odeslání zprávy do GUI.
     */
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
    }
    
}
