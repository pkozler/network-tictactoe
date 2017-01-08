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
     * objekt socketu klienta
     */
    private Socket socket;
    
    /**
     * objekt pro příjem zpráv
     */
    private DataInputStream dis;
    
    /**
     * objekt pro odesílání zpráv
     */
    private DataOutputStream dos;
    
    /**
     * přezdívka hráče
     */
    private String nick;
    
    /**
     * ID hráče
     */
    private int playerId;
    
    /**
     * ID aktuální herní místnosti
     */
    private int currentGameId;
    
    /**
     * Provede pokus o navázání spojení se serverem.
     * 
     * @param address adresa
     * @param port port
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
     * Zruší spojení se serverem.
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
     * Otestuje, zda je navázáno spojení se serverem.
     * 
     * @return true, pokud je spojení navázáno, jinak false
     */
    public synchronized boolean isConnected() {
        return socket != null && !socket.isClosed();
    }
    
    /**
     * Nastaví přidělené ID po přihlášení k serveru.
     * 
     * @param id ID klienta
     */
    public synchronized void logIn(int id) {
        if (id > 0) {
            playerId = id;
        }
    }
    
    /**
     * Vynuluje přidělené ID po odhlášení ze serveru.
     */
    public synchronized void logOut() {
        playerId = 0;
    }
    
    /**
     * Otestuje, zda je klient přihlášen.
     * 
     * @return true, pokud je klient přihlášen, jinak false
     */
    public synchronized boolean isLoggedIn() {
        return playerId > 0;
    }
    
    /**
     * Nastaví ID hry po vstupu do herní místnosti.
     * 
     * @param id ID hry
     */
    public synchronized void joinGame(int id) {
        if (id > 0) {
            currentGameId = id;
        }
    }
    
    /**
     * Vynuluje ID hry po opuštění herní místnosti.
     */
    public synchronized void leaveGame() {
        currentGameId = 0;
    }
    
    /**
     * Vrátí přidělené ID klienta.
     * 
     * @return ID klienta
     */
    public synchronized int getPlayerId() {
        return playerId;
    }
    
    /**
     * Vrátí zvolený nickname klienta.
     * 
     * @return nickname klienta
     */
    public synchronized String getPlayerNick() {
        return nick;
    }
    
    /**
     * Vrátí ID aktuální hry.
     * 
     * @return ID hry
     */
    public synchronized int getGameId() {
        return currentGameId;
    }
    
    /**
     * Odešle zprávu.
     * 
     * @param message zpráva
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
     * Přijme zprávu.
     * 
     * @return zpráva
     * @throws IOException
     * @throws InvalidMessageStringLengthException 
     */
    public synchronized TcpMessage receiveMessage() throws IOException, InvalidMessageStringLengthException {
        String msgStr = readFromSocket();
        
        return new TcpMessage(msgStr);
    }
    
    /**
     * Zapíše zprávu do socketu.
     * 
     * @param message zpráva
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
     * Načte zprávu ze socketu.
     * 
     * @return zpráva
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
