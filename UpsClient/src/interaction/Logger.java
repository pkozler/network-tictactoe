package interaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author Petr Kozler
 */
public class Logger {
    
    private static Logger instance;
    
    private SimpleDateFormat simpleDateFormat;
    
    private Calendar calendar;
    
    private StringBuilder createLogBuilder() {
        return new StringBuilder("[").append(simpleDateFormat.
                format(calendar.getTime())).append("] ");
    }
    
    public void printOut(String format, Object... args) {
        StringBuilder sb = createLogBuilder();
        sb.append(String.format(format, args));
        System.out.println(sb.toString());
    }
    
    public void printRecv(String msg) {
        if (msg.isEmpty()) {
            return;
        }
        
        StringBuilder sb = createLogBuilder();
        sb.append("Klient <-- Server: \"").append(msg).append("\"");
        System.out.println(sb.toString());
    }
    
    public void printSend(String msg) {
        if (msg.isEmpty()) {
            return;
        }
        
        StringBuilder sb = createLogBuilder();
        sb.append("Klient --> Server: \"").append(msg).append("\"");
        System.out.println(sb.toString());
    }
    
    public void printErr(String format, Object... args) {
        StringBuilder sb = createLogBuilder();
        sb.append(String.format(format, args));
        System.err.println(sb.toString());
    }
    
    private Logger(SimpleDateFormat simpleDateFormat, Calendar calendar) {
        this.simpleDateFormat = simpleDateFormat;
        this.calendar = calendar;
    }
    
    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
                    Calendar.getInstance());
        }
        
        return instance;
    }
    
}
