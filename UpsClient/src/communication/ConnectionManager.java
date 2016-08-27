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
    private DataInputStream dis;
    private DataOutputStream dos;
    private InetAddress host;
    private int port;
    private String nick;
    private boolean connected;
    
    public ConnectionManager(InetAddress host, int port, String nick) {
        this.host = host;
        this.port = port;
        this.nick = nick;
    }
    
    public void connect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

            socket = new Socket(host, port);
            socket.setSoTimeout(Config.SOCKET_TIMEOUT_MILLIS);

            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            connected = true;
        }
        catch (IOException ex) {
            connected = false;
        }
    }
    
    public boolean isConnected() {
        return connected;
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
