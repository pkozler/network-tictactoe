package interaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Třída Logger představuje objekt pro zápis logů, který je jako jedináček
 * přístupný pro použití z kterékoliv komponenty aplikace.
 * 
 * @author Petr Kozler
 */
public class Logger {
    
    /**
     * jediná instance loggeru
     */
    private static Logger instance;
    
    /**
     * Vytvoří objekt pro sestavení řetězce záznamu, který obsahuje
     * aktuální datum a čas.
     * 
     * @return objekt pro sestavení záznamu
     */
    private StringBuilder createLogBuilder() {
        return new StringBuilder("[").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").
                format(Calendar.getInstance().getTime())).append("] ");
    }
    
    /**
     * Vypíše na obrazovku zadanou hlášku a ukončí řádek.
     * 
     * @param format formát řetězce hlášky
     * @param args argumenty řetězce hlášky
     */
    public void printOut(String format, Object... args) {
        StringBuilder sb = createLogBuilder();
        sb.append(String.format(format, args));
        System.out.println(sb.toString());
    }
    
    /**
     * Vypíše záznam o zprávě přijaté od serveru.
     * 
     * @param msg řetězec zprávy
     */
    public void printRecv(String msg) {
        if (msg.isEmpty()) {
            return;
        }
        
        StringBuilder sb = createLogBuilder();
        sb.append("Klient <-- Server: \"").append(msg).append("\"");
        System.out.println(sb.toString());
    }
    
    /**
     * Vypíše záznam o zprávě zaslané serveru.
     * 
     * @param msg řetězec zprávy
     */
    public void printSend(String msg) {
        if (msg.isEmpty()) {
            return;
        }
        
        StringBuilder sb = createLogBuilder();
        sb.append("Klient --> Server: \"").append(msg).append("\"");
        System.out.println(sb.toString());
    }
    
    /**
     * Vypíše na obrazovku (do chybového výstupu) zadanou chybovou hlášku
     * a ukončí řádek.
     * 
     * @param format formát řetězce hlášky
     * @param args argumenty řetězce hlášky
     */
    public void printErr(String format, Object... args) {
        StringBuilder sb = createLogBuilder();
        sb.append(String.format(format, args));
        System.err.println(sb.toString());
    }
    
    /**
     * Vrátí (a při prvním volání nejprve vytvoří) jedinou instanci loggeru
     * přístupnou pro kteroukoliv komponentu aplikace.
     * 
     * @return instance loggeru
     */
    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        
        return instance;
    }
    
    /**
     * Privátní konstruktor pro zamezení vytvoření dalších instancí.
     */
    private Logger() {
        // žádný kód
    }
    
}
