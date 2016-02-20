package interaction;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import communication.ConnectionManager;
import communication.Game;
import visualization.GameFieldPanel;
import visualization.StatusBar;

/**
 * SwingWorker pro příjem stavu hry na pozadí a bezpečnou aktualizaci
 * hracího pole v uživatelském rozhraní.
 */
public class GameReceiveWorker extends SwingWorker<Boolean, Object> {

	/** správce spojení  */
	private ConnectionManager connectionManager;
	/** herní pole */
	private GameFieldPanel gameFieldPanel;
	/** seznam her */
	private JList<Game> gameList;
	/** časovač pro periodické navazování spojení */
	private Timer connectTimer;
	/** tlačítko pro odeslání požadavku na vytvoření hry */
	private JButton createGameButton;
	/** tlačítko pro odeslání požadavku na odpojení ze hry */
	private JButton leaveGameButton;
	
	/**
	 * Vytvoří nový SwingWorker pro příjem stavu hry.
	 */
	public GameReceiveWorker(ConnectionManager connectionManager, GameFieldPanel gameFieldPanel, 
			JList<Game> gameList, JButton createGameButton, JButton leaveGameButton, Timer connectTimer) {
		this.connectionManager = connectionManager;
		this.gameFieldPanel = gameFieldPanel;
		this.gameList = gameList;
		this.connectTimer = connectTimer;
		this.createGameButton = createGameButton;
		this.leaveGameButton = leaveGameButton;
	}
	
	/*
	 * zavolání komunikační funkce na pozadí
	 */
	@Override
	public Boolean doInBackground() {
		try {
			return connectionManager.receiveGameSync();
		} 
		catch (IOException e) {
			return false;
		}
	}
	
	/*
	 * aktualizace GUI v EDT
	 */
	@Override
	protected void done() {
		Boolean success = false;
		
		try {
			success = get();
		} 
		catch (InterruptedException | ExecutionException e) {
			StatusBar.printConnectionStatus("Chyba při aktualizaci stavu hry!");
		}
		
		if (!success) {
			StatusBar.printConnectionStatus("Přerušeno spojení se serverem.");
			connectTimer.restart();
			return;
		}
		
		// uživatel vystoupil ze hry
		if (!connectionManager.hasCurrentGame()) {
			gameList.setEnabled(true);
			createGameButton.setEnabled(true);
			leaveGameButton.setEnabled(false);
			
			(new ListReceiveWorker(connectionManager, gameFieldPanel, gameList, 
					createGameButton, leaveGameButton, connectTimer)).execute();
			
			return;
		} 
		
		// uživatel je stále ve hře
		gameList.setEnabled(false);
		createGameButton.setEnabled(false);
		leaveGameButton.setEnabled(true);
		
		// hra skončila
		if (connectionManager.getCurrentGame().getLastMovePlayer() == 0) {
			gameFieldPanel.createButtons(connectionManager);
		}
		
		// hra stále běží
		gameFieldPanel.updateButtons();
		(new GameReceiveWorker(connectionManager, gameFieldPanel, gameList, 
				createGameButton, leaveGameButton, connectTimer)).execute();
	}
	
}
