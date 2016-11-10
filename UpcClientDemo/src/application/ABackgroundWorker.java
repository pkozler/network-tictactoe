package application;

import javax.swing.JTextField;
import javax.swing.SwingWorker;

/**
 * Abstraktní třída ABackgroundWorker představuje obecnou komponentu
 * pro spuštění síťové operace na pozadí a následné zobrazení výsledků
 * této operace do komponent GUI.
 * 
 * @author Petr Kozler
 */
public abstract class ABackgroundWorker extends SwingWorker<String, Void> {
    
    /**
     * Objekt klienta provádějícího síťové operace.
     */
    protected IClient client;
    
    /**
     * Textové pole pro výpis výsledků síťových operací.
     */
    protected JTextField connectionStatusTF;
    
    /**
     * Vytvoří spouštěč síťové operace.
     * 
     * @param client objekt klienta
     * @param connectionStatusTF textové pole pro výsledky
     */
    public ABackgroundWorker(IClient client, JTextField connectionStatusTF) {
        this.client = client;
        this.connectionStatusTF = connectionStatusTF;
    }
    
    @Override
    protected abstract void done();
    
}
