package communication;

import configuration.Config;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 *
 * @author Petr Kozler
 */
public class ConnectionManager {

    private Socket socket;
    DataInputStream dis;
    DataOutputStream dos;
    private InetAddress host;
    private int port;
    private String nick;
    
    public ConnectionManager(InetAddress host, int port, String nick) {
        this.host = host;
        this.port = port;
        this.nick = nick;
    }
    
    public boolean connect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

            socket = new Socket(host, port);
            socket.setSoTimeout(Config.SOCKET_TIMEOUT_MILLIS);

            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            return true;
        }
        catch (IOException ex) {
            return false;
        }
    }
    
    protected void finalize() {
        try {
            socket.close();
        } 
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
