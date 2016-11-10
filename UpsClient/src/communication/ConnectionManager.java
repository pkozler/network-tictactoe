package communication;

import communication.tokens.InvalidMessageArgsException;
import configuration.Config;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

/**
 * Třída představující základní součást komunikační vrstvy aplikace.
 * Zajišťuje volání vestavěných komunikačních metod pro navazování spojení
 * se serverem a operace čtení a zápisu do socketu (tj. odesílání a příjem zpráv).
 * Zároveň uchovává základní údaje o aktuálním stavu hráče (např. ID a nickname)
 * a konfiguraci připojení k serveru (IP adresu a port).
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
    private int playerId;
    private int currentGameId;
    private int timeoutCounter;
    
    public synchronized void connect(InetAddress host, int port) throws IOException {
        this.host = host;
        this.port = port;
        
        socket = new Socket(host, port);
        socket.setSoTimeout(Config.SOCKET_TIMEOUT_MILLIS);
        resetTimeoutCounter();

        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
    }
    
    public synchronized void disconnect() throws IOException {
        if (socket != null && !socket.isClosed()) {
            dis.close();
            dos.close();
            socket.close();
        }
        
        socket = null;
    }
    
    public synchronized boolean isConnected() {
        return socket != null && !socket.isClosed();
    }
    
    public synchronized void logIn(int id) {
        if (id > 0) {
            playerId = id;
        }
    }
    
    public synchronized void logOut() {
        playerId = 0;
    }
    
    public synchronized boolean isLoggedIn() {
        return playerId > 0;
    }
    
    public synchronized void incTimeoutCounter() throws IOException {
        timeoutCounter++;
        
        if (timeoutCounter > Config.MAX_TIMEOUTS) {
            disconnect();
        }
    }
    
    public synchronized void resetTimeoutCounter() {
        timeoutCounter = 0;
    }
    
    public synchronized void joinGame(int id) {
        if (id > 0) {
            currentGameId = id;
        }
    }
    
    public synchronized void leaveGame() {
        currentGameId = 0;
    }
    
    public synchronized int getPlayerId() {
        return playerId;
    }
    
    public synchronized String getPlayerNick() {
        return nick;
    }
    
    public synchronized int getGameId() {
        return currentGameId;
    }
    
    public synchronized void sendMessage(Message message) throws IOException, InvalidMessageArgsException {
        String msgStr = message.toString();
        
        if (msgStr == null) {
            throw new InvalidMessageArgsException();
        }
        
        writeToSocket(msgStr);
    }
    
    public synchronized Message receiveMessage() throws IOException, InvalidMessageStringLengthException {
        String msgStr = readFromSocket();
        
        return new Message(msgStr);
    }
    
    private void writeToSocket(String message) throws IOException {
        dos.writeInt(message.length());
        
        if (message.length() > 0) {
            byte[] bytes = message.getBytes(StandardCharsets.US_ASCII);
            dos.write(bytes);
        }
        
        dos.flush();
    }
    
    private String readFromSocket() throws IOException, InvalidMessageStringLengthException {
        int length;
        
        try {
            length = dis.readInt();
        }
        catch (SocketTimeoutException ex) {
            incTimeoutCounter();
            
            throw ex;
        }
        
        if (length < 0) {
            throw new InvalidMessageStringLengthException();
        }
        
        if (length == 0) {
            return "";
        }
        
        byte[] bytes = new byte[length];
        
        try {
            dis.read(bytes);
        }
        catch (SocketTimeoutException ex) {
            incTimeoutCounter();
            
            throw ex;
        }
        
        resetTimeoutCounter();
        
        return new String(bytes, StandardCharsets.US_ASCII);
    }
    
}
