package application;

import java.util.concurrent.ExecutionException;
import javax.swing.JTextField;

/**
 * Třída ConnectionWorker představuje spouštěč operace navázání spojení
 * se serverem, jejíž výsledek je poté zobrazen do příslušných
 * textových komponent GUI.
 * 
 * @author Petr Kozler
 */
public class ConnectionWorker extends ABackgroundWorker {

    /**
     * Textové pole pro zadání hostname nebo adresy serveru.
     */
    private JTextField hostTF;
    
    /**
     * Textové pole pro zadání čísla portu serveru.
     */
    private JTextField portTF;
    
    /**
     * Hostname nebo adresa serveru.
     */
    private String host;
    
    /**
     * Číslo portu serveru.
     */
    private int port;
    
    /**
     * Textové pole pro výpis odpovědi.
     */
    private JTextField receivedTF;

    /**
     * Indikátor úspěšného připojení.
     */
    private boolean isOk;
    
    /**
     * Vytvoří spouštěč navázání spojení se serverem.
     * 
     * @param client objekt klienta
     * @param connectionStatusTF textové pole pro výpis výsledku
     * @param hostTF textové pole pro hostname
     * @param portTF textové pole pro port
     */
    public ConnectionWorker(IClient client, JTextField connectionStatusTF,
            JTextField hostTF, JTextField portTF, JTextField receivedTF) {
        super(client, connectionStatusTF);
        
        this.hostTF = hostTF;
        this.portTF = portTF;
        this.receivedTF = receivedTF;
        
        host = hostTF.getText();
        port = Integer.parseInt(portTF.getText());
    }
    
    /**
     * Spustí síťovou operaci navázání spojení na pozadí.
     * 
     * @return chybová zpráva, pokud nastala chyba
     * 
     * @throws Exception výjimka, pokud se nepodařilo spustit operaci
     */
    @Override
    protected String doInBackground() throws Exception {
        try {
            if (client.isConnected()) {
                client.disconnect();
            }
            
            client.connect(host, port);
            
            return String.format("Připojen k %s:%d",
                    client.getAddress(), client.getPort());
        } catch (ClientException e) {
            return e.getErrorMessage();
        } finally {
            isOk = client.isConnected();
        }
    }

    /**
     * Zobrazí výsledek operace navázání spojení do GUI.
     */
    @Override
    protected void done() {
        try {
            connectionStatusTF.setText(get());
        } catch (InterruptedException | ExecutionException e ) {
            connectionStatusTF.setText("Chyba navazování spojení: "
                    + e.getLocalizedMessage());
        }
        
        if (isOk) {
            new ResponseWorker(client, connectionStatusTF, receivedTF).execute();
            new PingWorker(client, connectionStatusTF).execute();
        }
    }
    
}
