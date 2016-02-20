package communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import configuration.Config;
import visualization.StatusBar;

/**
 * Třída slouží ke správě spojení se serverem a k volání komunikačních funkcí.
 */
public class ConnectionManager {
	
	/** koncový bod komunikace se serverem */
	private Socket socket;
	/** adresa socketu */
	private InetAddress host;
	/** port socketu */
	private int port;
	/** vysílač požadavků uživatele */
	private RequestSender requestSender;
	/** přijímač odpovědí serveru */
	private ResponseReceiver responseReceiver;
	/** ID klienta přiřazené serverem */
	private int clientId;
	/** stav aktuální hry */
	private Game currentGame;
	/** zámek pro vzájemnou synchronizaci volání komunikačních funkcí */
	private ReentrantLock lock;
	
	/**
	 * Vytvoří správce spojení s nastavenou IP adresou a portem socketu.
	 */
	public ConnectionManager(InetSocketAddress address) {
		this.host = address.getAddress();
		this.port = address.getPort();
	}
	
	/**
	 * Naváže spojení se serverem pomocí vytvořeného socketu.
	 * V případě úspěchu vrací TRUE, jinak FALSE. 
	 * Tato metoda je volána periodicky, dokud se spojení
	 * nepodaří úspěšně navázat, a poté opětovně v případě,
	 * že bylo spojení ztraceno.
	 */
	public boolean connect() {
		try {
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
			
			socket = new Socket(host, port);
			InetAddress address = socket.getInetAddress();
			socket.setSoTimeout(Config.SOCKET_TIMEOUT_MILLIS);
			
			StatusBar.printConnectionStatus("Připojuji se na : " + address.getHostName() 
				+ " (" + address.getHostAddress() + ") na portu: " + socket.getPort());
			
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			lock = new ReentrantLock();

			responseReceiver = new ResponseReceiver(dis, dos);
			requestSender = new RequestSender(dos);
			currentGame = null;
			
			clientId = dis.readInt();
			StatusBar.printConnectionStatus("Přijato klientské ID: " + clientId);
			
			return true;
		}
		catch (IOException ioe) {
			return false;
		}
	}
	
	/**
	 * Zavolá komunikační funkci pro odeslání požadavku
	 * na vstoupení do nerozehrané hry, vytvoření nové hry,
	 * nebo vystoupení z nerozehrané hry.
	 */
	public synchronized void sendSelectSync(Game selectedGame) {
		requestSender.sendGameRequest(selectedGame);
	}
	
	/**
	 * Zavolá komunikační funkci pro odeslání požadavku
	 * představujícího tah v právě rozehrané hře,
	 * do které hráč vstoupil, nebo vystoupení
	 * z právě rozehrané hry.
	 */
	public synchronized void sendPlaySync(byte x, byte y) {
		requestSender.sendPlayMessage(x, y);
	}
	
	/**
	 * Zavolá komunikační funkci pro příjem odpovědi serveru
	 * představující aktuální seznam her dostupných her,
	 * do kterých hráč může vstoupit, pokud se právě
	 * v žádné hře nenachází.
	 */
	public synchronized List<Game> receiveListSync() throws IOException {
		List<Game> games = new ArrayList<>();
		currentGame = responseReceiver.receiveGameList(games);

		return games;
	}
	
	/**
	 * Zavolá komunikační funkci pro příjem odpovědi serveru
	 * představující stav aktuálně rozehrané hry, ve které
	 * se hráč právě nachází.
	 */
	public synchronized Boolean receiveGameSync() throws IOException {
		Boolean success = responseReceiver.receiveStatus(currentGame);
		
		return success;
	}
	
	/**
	 * Testuje, zda je hráč připojen do hry.
	 */
	public synchronized boolean hasCurrentGame() {
		return currentGame != null;
	}
	
	/**
	 * Vrací aktuální hru, do které hráč vstoupil.
	 */
	public synchronized Game getCurrentGame() {
		return currentGame;
	}
	
	public void quitGame() {
		currentGame = null;
	}

	/**
	 * Vrací textovou reprezenaci adresy a portu socketu.
	 */
	public synchronized String getAddress() {
		return host.getHostAddress() + ":" + port;
	}
	
	/**
	 * Uzavře spojení se serverem.
	 */
	protected void finalize() {
		try {
			socket.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
