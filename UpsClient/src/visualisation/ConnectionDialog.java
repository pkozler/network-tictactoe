package visualisation;

import communication.ConnectionManager;
import configuration.Config;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Petr Kozler
 */
public class ConnectionDialog extends JDialog {
    
    private JTextField hostInputTF;
    private JTextField portInputTF;
    private JTextField nickInputTF;
    
    private ConnectionDialog(String hostArg, String hostError, String portArg,
            String portError, String nickArg, String nickError) {
        JLabel hostLabel = new JLabel("IP adresa nebo název:");
        hostInputTF = new JTextField(hostArg);
        JTextField hostErrorTF = new JTextField(hostError);
        JLabel portLabel = new JLabel("Číslo portu:");
        portInputTF = new JTextField(portArg);
        JTextField portErrorTF = new JTextField(portError);
        JLabel nickLabel = new JLabel("Nickname:");
        nickInputTF = new JTextField(nickArg);
        JTextField nickErrorTF = new JTextField(nickError);
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Storno");
        
        okButton.addActionListener((ActionEvent e) -> {
            dispose();
        });
        
        cancelButton.addActionListener((ActionEvent e) -> {
            hostInputTF.setText(null);
            portInputTF.setText(null);
            nickInputTF.setText(null);
            dispose();
        });
        
        JPanel panel = new JPanel();
        panel.add(hostLabel);
        panel.add(hostInputTF);
        panel.add(hostErrorTF);
        panel.add(portLabel);
        panel.add(portInputTF);
        panel.add(portErrorTF);
        panel.add(nickLabel);
        panel.add(nickInputTF);
        panel.add(nickErrorTF);
        panel.add(okButton);
        panel.add(cancelButton);
        setContentPane(panel);
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
            InetAddress host = null;
            int port = 0;
            String nick = null;
            String hostError = null;
            String portError = null;
            String nickError = null;
            boolean validArgs = true;
            
            try {
                if (hostArg != null) {
                    host = parseHost(hostArg);
                }
                else {
                    validArgs = false;
                }
            }
            catch (UnknownHostException ex) {
                hostError = "Neplatný formát IP adresy. IP adresa musí být ve tvaru \"#.#.#.#\", kde \"#\" je celé číslo v rozsahu 0 - 255.";
                validArgs = false;
            }
            
            try {
                if (portArg != null) {
                    port = parsePort(portArg);
                }
                else {
                    validArgs = false;
                }
            }
            catch (NumberFormatException ex) {
                portError = "Neplatný formát čísla portu. Port musí být celé číslo v rozsahu "
                        + Config.MIN_PORT + " - " + Config.MAX_PORT + ".";
                validArgs = false;
            }
            
            try {
                if (nickArg != null) {
                    nick = parseNick(nickArg);
                }
                else {
                    validArgs = false;
                }
            }
            catch (IllegalArgumentException ex) {
                nickError = "Neplatný formát nickname hráče. Nickname smí obsahovat pouze velká a malá písmena a číslice.";
                validArgs = false;
            }
            
            if (validArgs) {
                return new ConnectionManager(host, port, nick);
            }
            
            // TODO vytvorit dialog a ulozit do predanych promennych stringy z policek dialogu
            ConnectionDialog dialog = new ConnectionDialog(hostArg, hostError, portArg, portError, nickArg, nickError);
            hostArg = dialog.hostInputTF.toString();
            portArg = dialog.portInputTF.toString();
            nickArg = dialog.nickInputTF.toString();
        }
    }
    
}
