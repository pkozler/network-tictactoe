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
 * Třída TcpClient představuje základní součást komunikační vrstvy aplikace.
 * Zajišťuje volání vestavěných komunikačních metod pro navazování spojení
 * se serverem a operace čtení a zápisu do socketu (tj. odesílání a příjem zpráv).
 * Zároveň uchovává základní údaje o aktuálním stavu hráče (např. ID a nickname)
 * a konfiguraci připojení k serveru (IP adresu a port).
 * 
 * @author Petr Kozler
 */
public class TcpClient {

    /**
     * 
     */
    private Socket socket;
    
    /**
     * 
     */
    private DataInputStream dis;
    
    /**
     * 
     */
    private DataOutputStream dos;
    
    /**
     * 
     */
    private String nick;
    
    /**
     * 
     */
    private int playerId;
    
    /**
     * 
     */
    private int currentGameId;
    
    /**
     * 
     * 
     * @param address
     * @param port
     * @throws IOException 
     */
    public synchronized void connect(String address, int port) throws IOException {
        InetAddress host = InetAddress.getByName(address);
        
        socket = new Socket(host, port);
        socket.setSoTimeout(Config.SOCKET_TIMEOUT_MILLIS);

        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
    }
    
    /**
     * 
     * 
     * @throws IOException 
     */
    public synchronized void disconnect() throws IOException {
        if (socket != null && !socket.isClosed()) {
            dis.close();
            dos.close();
            socket.close();
        }
        
        socket = null;
    }
    
    /**
     * 
     * 
     * @return 
     */
    public synchronized boolean isConnected() {
        return socket != null && !socket.isClosed();
    }
    
    /**
     * 
     * 
     * @param id 
     */
    public synchronized void logIn(int id) {
        if (id > 0) {
            playerId = id;
        }
    }
    
    /**
     * 
     * 
     */
    public synchronized void logOut() {
        playerId = 0;
    }
    
    /**
     * 
     * 
     * @return 
     */
    public synchronized boolean isLoggedIn() {
        return playerId > 0;
    }
    
    /**
     * 
     * 
     * @param id 
     */
    public synchronized void joinGame(int id) {
        if (id > 0) {
            currentGameId = id;
        }
    }
    
    /**
     * 
     * 
     */
    public synchronized void leaveGame() {
        currentGameId = 0;
    }
    
    /**
     * 
     * 
     * @return 
     */
    public synchronized int getPlayerId() {
        return playerId;
    }
    
    /**
     * 
     * 
     * @return 
     */
    public synchronized String getPlayerNick() {
        return nick;
    }
    
    /**
     * 
     * 
     * @return 
     */
    public synchronized int getGameId() {
        return currentGameId;
    }
    
    /**
     * 
     * 
     * @param message
     * @throws IOException
     * @throws InvalidMessageArgsException 
     */
    public synchronized void sendMessage(TcpMessage message) throws IOException, InvalidMessageArgsException {
        String msgStr = message.toString();
        
        if (msgStr == null) {
            throw new InvalidMessageArgsException();
        }
        
        writeToSocket(msgStr);
    }
    
    /**
     * 
     * 
     * @return
     * @throws IOException
     * @throws InvalidMessageStringLengthException 
     */
    public synchronized TcpMessage receiveMessage() throws IOException, InvalidMessageStringLengthException {
        String msgStr = readFromSocket();
        
        return new TcpMessage(msgStr);
    }
    
    /**
     * 
     * 
     * @param message
     * @throws IOException 
     */
    private void writeToSocket(String message) throws IOException {
        dos.writeInt(message.length());
        
        if (message.length() > 0) {
            byte[] bytes = message.getBytes(StandardCharsets.US_ASCII);
            dos.write(bytes);
        }
        
        dos.flush();
    }
    
    /**
     * 
     * 
     * @return
     * @throws IOException
     * @throws InvalidMessageStringLengthException 
     */
    private String readFromSocket() throws IOException, InvalidMessageStringLengthException {
        int length;
        
        try {
            length = dis.readInt();
        }
        catch (SocketTimeoutException ex) {
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
            throw ex;
        }
        
        return new String(bytes, StandardCharsets.US_ASCII);
    }
    
}
