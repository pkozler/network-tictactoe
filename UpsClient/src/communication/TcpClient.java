package communication;

import communication.containers.GameInfo;
import communication.containers.PlayerInfo;
import communication.tokens.InvalidMessageArgsException;
import configuration.Config;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
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
     * adresa
     */
    private String host;
    
    /**
     * port
     */
    private int port;
    
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
     * ID hráče
     */
    private int playerId;
    
    /**
     * ID aktuální herní místnosti
     */
    private int gameId;
    
    /**
     * struktura s údaji přihlášeného hráče
     */
    private PlayerInfo playerInfo;
    
    /**
     * struktura s údaji aktuální zvolené herní místnosti
     */
    private GameInfo gameInfo;
    
    /**
     * Provede pokus o navázání spojení se serverem.
     * 
     * @param address adresa
     * @param port port
     * @throws IOException 
     */
    public void connect(String address, int port) throws IOException {
        host = InetAddress.getByName(address).getHostAddress();
        this.port = port;
        
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
    public void disconnect() throws IOException {
        if (socket != null && !socket.isClosed()) {
            dis.close();
            dos.close();
            socket.close();
        }
        
        socket = null;
        logOut();
    }
    
    /**
     * Otestuje, zda je navázáno spojení se serverem.
     * 
     * @return true, pokud je spojení navázáno, jinak false
     */
    public boolean isConnected() {
        return socket != null && !socket.isClosed();
    }
    
    /**
     * Vrátí ID hráče.
     * 
     * @return ID hráče
     */
    public int getPlayerId() {
        return playerId;
    }

    /**
     * Nastaví ID hráče.
     * 
     * @param playerId ID hráče
     */
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    /**
     * Vrátí ID hry.
     * 
     * @return ID hry
     */
    public int getGameId() {
        return gameId;
    }

    /**
     * Nastaví ID hry.
     * 
     * @param gameId ID hry
     */
    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
    
    /**
     * Propojí hráče s položkou seznamu hráčů (použito po přihlášení).
     * 
     * @param playerInfo údaje hráče
     */
    public void logIn(PlayerInfo playerInfo) {
        if (playerInfo != null) {
            this.playerInfo = playerInfo;
        }
    }
    
    /**
     * Odstraní odkaz na položku seznamu hráčů (použito po odhlášení).
     */
    public void logOut() {
        playerInfo = null;
        playerId = 0;
        leaveGame();
    }
    
    /**
     * Otestuje, zda je klient přihlášen.
     * 
     * @return true, je-li klient přihlášen, jinak false
     */
    public boolean isLogged() {
        return playerInfo != null;
    }
    
    /**
     * Vrátí údaje o hráči.
     * 
     * @return údaje hráče
     */
    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    /**
     * Propojí hráče s položkou seznamu her (použito po vstupu do hry).
     * 
     * @param gameInfo údaje hry
     */
    public void joinGame(GameInfo gameInfo) {
        if (gameInfo != null) {
            this.gameInfo = gameInfo;
        }
    }
    
    /**
     * Odstraní odkaz na položku seznamu her (použito po odchodu ze hry).
     */
    public void leaveGame() {
        gameInfo = null;
        gameId = 0;
    }

    /**
     * Otestuje, zda je klient v herní místnosti.
     * 
     * @return true, je-li klient v herní místnosti, jinak false
     */
    public boolean isInGame() {
        return gameInfo != null;
    }
    
    /**
     * Vrátí údaje o hře.
     * 
     * @return údaje hry
     */
    public GameInfo getGameInfo() {
        return gameInfo;
    }
    
    /**
     * Odešle zprávu.
     * 
     * @param message zpráva
     * @throws IOException
     * @throws InvalidMessageArgsException 
     */
    public void sendMessage(TcpMessage message) throws IOException, InvalidMessageArgsException {
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
    public TcpMessage receiveMessage() throws IOException, InvalidMessageStringLengthException {
        String msgStr = readFromSocket();
        
        if (!msgStr.isEmpty()) {
            System.out.println(msgStr);
        }
        
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
        
        length = dis.readInt();
        
        if (length < 0 || length > Config.MAX_MESSAGE_LENGTH) {
            throw new InvalidMessageStringLengthException();
        }
        
        if (length == 0) {
            return "";
        }
        
        byte[] bytes = new byte[length];
        
        dis.read(bytes);
        
        return new String(bytes, StandardCharsets.US_ASCII);
    }
    
}
