package application;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Třída MainWindow představuje hlavní okno GUI programu.
 * 
 * @author Petr Kozler
 */
public class MainWindow extends JFrame {

    /**
     * Objekt klienta pro provádění síťových operací.
     */
    private IClient client;
    
    /**
     * Objekt pro zpracování a uchování parametrů příkazové řádky.
     */
    private ArgParser parser;
    
    /**
     * Textové pole pro zadání požadavku k odeslání.
     */
    private JTextField sentTF;
    
    /**
     * Textové pole pro výpis přijaté odpovědi.
     */
    private JTextField receivedTF;
    
    /**
     * Textové pole pro zobrazení výsledků síťových operací.
     */
    private JTextField connectionStatusTF;
    
    /**
     * Textové pole pro zadání hostname nebo adresy serveru.
     */
    private JTextField hostTF;
    
    /**
     * Textové pole pro zadání čísla portu serveru.
     */
    private JTextField portTF;
    
    /**
     * Tlačítko pro připojení k serveru se zadaným hostname nebo adresou
     * a číslem portu.
     */
    private JButton connectButton;
    
    /**
     * Tlačítko pro odeslání zadaného požadavku serveru a přijetí jeho
     * odpovědi.
     */
    private JButton sendButton;

    /**
     * Vytvoří okno programu.
     * 
     * @param client objekt klienta
     * @param parser objekt parseru
     */
    public MainWindow(IClient client, ArgParser parser) {
        this.client = client;
        this.parser = parser;
        
        setTitle("Prototyp TCP klienta");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(Configuration.DEFAULT_WIDTH, Configuration.DEFAULT_HEIGHT);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.add(createOptionsPanel(parser.getHost(), parser.getPort()));
        panel.add(createMessagePanel());
        panel.add(createStatusPanel());
        setListeners();
        
        setContentPane(panel);
        setVisible(true);
    }
    
    /**
     * Vytvoří panel s komponentami pro nastavení připojení (hostname a port).
     * 
     * @param host hostname serveru zadaný z příkazové řádky
     * @param port port serveru zadaný z příkazové řádky
     * 
     * @return panel pro nastavení připojení
     */
    private JPanel createOptionsPanel(String host, int port) {
        JPanel panel = new JPanel(new GridLayout(3, 2));
        
        JLabel hostLabel = new JLabel("Host:");
        hostTF = new JTextField(host);
        JLabel portLabel = new JLabel("Port:");
        portTF = new JTextField(Integer.toString(port));
        connectButton = new JButton("Připojit");
        
        panel.add(hostLabel);
        panel.add(hostTF);
        panel.add(portLabel);
        panel.add(portTF);
        panel.add(connectButton);
        
        return panel;
    }

    /**
     * Vytvoří panel s komponentami pro zadání požadavku a výpis odpovědi.
     * 
     * @return panel pro zadání a výpis zpráv
     */
    private JPanel createMessagePanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2));
        
        JLabel sentLabel = new JLabel("Zpráva klienta:");
        sentTF = new JTextField();
        JLabel receivedLabel = new JLabel("Odpověď serveru:");
        receivedTF = new JTextField();
        receivedTF.setEditable(false);
        sendButton = new JButton("Odeslat");
        
        panel.add(sentLabel);
        panel.add(sentTF);
        panel.add(receivedLabel);
        panel.add(receivedTF);
        panel.add(sendButton);
        
        return panel;
    }
    
    /**
     * Vytvoří panel se stavovým řádkem (obsahuje komponenty pro výpis výsledků
     * právě provedené síťové operace včetně případných chybových hlášení).
     * 
     * @return panel se stavovým řádkem
     */
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2));
        
        JLabel connectionStatusLabel = new JLabel("Stav spojení:");
        connectionStatusTF = new JTextField("Nepřipojen");
        connectionStatusTF.setEditable(false);
        
        panel.add(connectionStatusLabel);
        panel.add(connectionStatusTF);
        
        return panel;
    }
    
    /**
     * Nastaví posluchače stisku tlačítka pro připojení k serveru
     * a pro odeslání zprávy.
     */
    private void setListeners() {
        connectButton.addActionListener((ActionEvent e) -> {
            // spuštění operace připojení k serveru na pozadí
            new ConnectionWorker(client, connectionStatusTF,
                    hostTF, portTF, receivedTF).execute();
        });
        
        sendButton.addActionListener((ActionEvent e) -> {
            // spuštění operace odeslání zprávy na pozadí
            new RequestWorker(client, connectionStatusTF, sentTF).execute();
        });
    }
    
}
