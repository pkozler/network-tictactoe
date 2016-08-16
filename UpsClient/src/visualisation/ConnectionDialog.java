package visualisation;

import communication.ConnectionManager;
import configuration.Config;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.swing.JDialog;

/**
 *
 * @author Petr Kozler
 */
public class ConnectionDialog extends JDialog {
    
    private static final int HOST_ERR = 1;
    private static final int PORT_ERR = 2;
    private static final int NICK_ERR = 3;
    
    private ConnectionDialog(Map<Integer, String> errors) {
        // TODO pridat policka pro zadani informaci
    }
    
    private static InetAddress parseHost(String hostArg) throws UnknownHostException {
        return InetAddress.getByName(hostArg);
    }

    private static int parsePort(String portArg) throws NumberFormatException {
        int port = Integer.parseInt(portArg);

        if (port >= Config.MIN_PORT && port <= Config.MAX_PORT) {
            throw new NumberFormatException();
        }

        return port;
    }

    private static String parseNick(String nickArg) throws IllegalArgumentException {
        Pattern p = Pattern.compile("[^a-zA-Z0-9]");
        boolean validNickname = !(p.matcher(nickArg).find());
        
        if (validNickname) {
            return nickArg;
        }
        
        throw new IllegalArgumentException();
    }
    
    public static ConnectionManager setupConnection(String hostArg, String portArg, String nickArg) {
        while (true) {
            Map<Integer, String> errors = new HashMap<>();
            InetAddress host = null;
            int port = 0;
            String nick = null;
            boolean filledArgs = true;
            
            try {
                if (hostArg != null) {
                    host = parseHost(hostArg);
                }
                else {
                    filledArgs = false;
                }
            }
            catch (UnknownHostException ex) {
                errors.put(HOST_ERR, "Neplatný formát IP adresy. IP adresa musí být ve tvaru \"#.#.#.#\", kde \"#\" je celé číslo v rozsahu 0 - 255.");
            }
            
            try {
                if (portArg != null) {
                    port = parsePort(portArg);
                }
                else {
                    filledArgs = false;
                }
            }
            catch (NumberFormatException ex) {
                errors.put(PORT_ERR, "Neplatný formát čísla portu. Port musí být celé číslo v rozsahu " + Config.MIN_PORT + " - " + Config.MAX_PORT + ".");
            }
            
            try {
                if (nickArg != null) {
                    nick = parseNick(nickArg);
                }
                else {
                    filledArgs = false;
                }
            }
            catch (IllegalArgumentException ex) {
                errors.put(NICK_ERR, "Neplatný formát nickname hráče. Nickname smí obsahovat pouze velká a malá písmena a číslice.");
            }
            
            if (filledArgs && errors.isEmpty()) {
                return new ConnectionManager(host, port, nick);
            }
            
            // TODO vytvorit dialog a ulozit do predanych promennych stringy z policek dialogu
        }
    }
    
}
