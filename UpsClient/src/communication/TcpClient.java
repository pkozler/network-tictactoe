package communication;

import communication.containers.CurrentGameDetail;
import communication.containers.GameInfo;
import communication.containers.PlayerInfo;
import communication.tokens.InvalidMessageArgsException;
import configuration.Config;
import interaction.Logger;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Observable;

/**
 * Třída TcpClient představuje základní součást komunikační vrstvy aplikace.
 * Zajišťuje volání vestavěných komunikačních metod pro navazování spojení
 * se serverem a operace čtení a zápisu do socketu (tj. odesílání a příjem zpráv).
 * Zároveň uchovává základní údaje o aktuálním stavu hráče (např. ID a nickname)
 * a konfiguraci připojení k serveru (IP adresu a port).
 * 
 * @author Petr Kozler
 */
public class TcpClient extends Observable {

    private final Logger LOGGER = Logger.getInstance();
    
    /**
     * adresa
     */
    private InetAddress host;
    
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
     * ID minulé herní místnosti
     */
    private int lastGameId;
    
    private ArrayList<PlayerInfo> playerList;
    
    private ArrayList<GameInfo> gameList;
    
    private CurrentGameDetail gameDetail;

    /**
     * Vytvoří objekt TCP klienta.
     * 
     * @param host adresa
     * @param port port
     */
    public TcpClient(InetAddress host, int port) {
        this.host = host;
        this.port = port;
        
        clear(false, false, false, false);
    }
    
    /**
     * Vrátí adresu.
     * 
     * @return adresa
     */
    public InetAddress getHost() {
        return host;
    }

    /**
     * Vrátí port.
     * 
     * @return port
     */
    public int getPort() {
        return port;
    }

    /**
     * Provede pokus o navázání spojení se serverem.
     * 
     * @throws IOException 
     */
    public void connect() throws IOException {
        socket = new Socket(host, port);
        socket.setSoTimeout(Config.SOCKET_TIMEOUT_MILLIS);

        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
        
        refresh();
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
        
        clear(false, false, false, true);
        refresh();
    }
    
    /**
     * Otestuje, zda je navázáno spojení se serverem.
     * 
     * @return true, pokud je spojení navázáno, jinak false
     */
    public boolean isConnected() {
        return socket != null && !socket.isClosed();
    }
    
    public void logIn(int playerId) {
        this.playerId = playerId < 0 ? 0 : playerId;
        
        refresh();
    }
    
    public void logOut() {
        clear(true, false, false, false);
        refresh();
    }

    public int getCurrentPlayerId() {
        return playerId;
    }

    public void joinGame(int gameId) {
        this.gameId = gameId < 0 ? 0 : gameId;
        lastGameId = this.gameId;
        
        refresh();
    }
    
    public void leaveGame() {
        clear(true, true, false, false);
        refresh();
    }

    public int getCurrentGameId() {
        return gameId;
    }

    public void setPlayerList(ArrayList<PlayerInfo> playerList) {
        this.playerList = playerList != null ? playerList
                : new ArrayList<PlayerInfo>();
        
        refresh();
    }

    public ArrayList<PlayerInfo> getPlayerList() {
        return playerList;
    }

    public void setGameList(ArrayList<GameInfo> gameList) {
        this.gameList = gameList != null ? gameList
                : new ArrayList<GameInfo>();

        refresh();
    }

    public ArrayList<GameInfo> getGameList() {
        return gameList;
    }

    public void setGameDetail(CurrentGameDetail gameDetail) {
        if (gameDetail != null) {
            this.gameDetail = gameDetail;
            
            gameId = gameId > 0 ? gameId : lastGameId;
        }
        else {
            this.gameDetail = new CurrentGameDetail();
        }
        
        refresh();
    }
    
    public CurrentGameDetail getGameDetail() {
        return gameDetail;
    }

    /**
     * Odešle zprávu.
     * 
     * @param message zpráva
     * @throws IOException
     * @throws InvalidMessageArgsException 
     */
    public void sendMessage(Message message) throws IOException, InvalidMessageArgsException {
        String msgStr = message.toString();
        
        if (msgStr == null) {
            throw new InvalidMessageArgsException();
        }
        
        writeToSocket(msgStr);
        LOGGER.printSend(msgStr);
    }
    
    /**
     * Přijme zprávu.
     * 
     * @return zpráva
     * @throws IOException
     * @throws InvalidMessageStringLengthException 
     */
    public Message receiveMessage() throws IOException, InvalidMessageStringLengthException {
        String msgStr = readFromSocket();
        LOGGER.printRecv(msgStr);
        
        return new Message(msgStr);
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
    
    private void clear(boolean keepLists, boolean keepPlayer,
            boolean keepGame, boolean keepLastGameId) {
        if (!keepLastGameId) {
            lastGameId = 0;
        }
        
        if (keepGame) {
            return;
        }
        
        gameId = 0;
        gameDetail = new CurrentGameDetail();

        if (keepPlayer) {
            return;
        }
        
        playerId = 0;
        
        if (keepLists) {
            return;
        }
        
        gameList = new ArrayList<>();
        playerList = new ArrayList<>();
    }
    
    private void refresh() {
        setChanged();
        notifyObservers();
    }
    
}
