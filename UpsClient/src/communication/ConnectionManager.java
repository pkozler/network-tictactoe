package communication;

import configuration.Config;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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
    private int id;
    private String nick;
    
    public ConnectionManager(InetAddress host, int port, String nick) {
        this.host = host;
        this.port = port;
        this.nick = nick;
    }
    
    public void connect() throws IOException {
        socket = new Socket(host, port);
        socket.setSoTimeout(Config.SOCKET_TIMEOUT_MILLIS);

        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
    }
    
    public void disconnect() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        
        socket = null;
    }
    
    public boolean isConnected() {
        return socket != null && !socket.isClosed();
    }
    
    public void activate(int id) {
        if (id > 0) {
            this.id = id;
        }
    }
    
    public void deactivate() {
        this.id = 0;
    }
    
    public boolean isActive() {
        return id > 0;
    }
    
    public void sendMessage(Message message) throws IOException {
        if (message.hasArgs()) {
            String msgStr = message.getType();

            if (message.getType() != null) {
                msgStr += Config.DELIMITER + String.join(Config.DELIMITER, message.getArgs());
            }
            
            writeToSocket(msgStr);
            return;
        }
        
        if (!message.hasType() && !message.hasArgs()) {
            writeToSocket("");
        }
    }
    
    public Message receiveMessage() throws IOException, InvalidMessageException {
        String msgStr = readFromSocket();
        
        if (msgStr.isEmpty()) {
            return new Message(null);
        }
        
        int firstDelimIndex = msgStr.indexOf(Config.DELIMITER);
        
        if (firstDelimIndex < 0) {
            return new Message(msgStr);
        }
        
        String type = msgStr.substring(0, firstDelimIndex);
        String[] args = msgStr.substring(firstDelimIndex + 1).split(Config.DELIMITER);
        Message message = new Message(type, args);
        
        return message;
    }
    
    private void writeToSocket(String message) throws IOException {
        dos.writeInt(message.length());
        
        if (message.length() > 0) {
            byte[] bytes = message.getBytes(StandardCharsets.US_ASCII);
            dos.write(bytes);
        }
        
        dos.flush();
    }
    
    private String readFromSocket() throws IOException, InvalidMessageException {
        int length = dis.readInt();
        
        if (length < 0) {
            throw new InvalidMessageException();
        }
        
        if (length == 0) {
            return "";
        }
        
        byte[] bytes = new byte[length];
        dis.read(bytes);
        
        return new String(bytes, StandardCharsets.US_ASCII);
    }
    
}
