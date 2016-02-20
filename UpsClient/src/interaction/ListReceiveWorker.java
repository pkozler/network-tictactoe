package interaction;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import communication.ConnectionManager;
import communication.Game;
import visualization.GameFieldPanel;
import visualization.StatusBar;

/**
 * SwingWorker pro příjem seznamu her na pozadí a bezpečnou aktualizaci
 * seznamu v uživatelském rozhraní.
 */
public class ListReceiveWorker extends SwingWorker<List<Game>, Object> {
	
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
	 * Vytvoří nový SwingWorker pro příjem seznamu her.
	 */
	public ListReceiveWorker(ConnectionManager connectionManager, GameFieldPanel gameFieldPanel, 
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
	public List<Game> doInBackground() {
		try {
			return connectionManager.receiveListSync();
		} 
		catch (IOException e) {
			return null;
		}
	}
	
	/*
	 * aktualizace GUI v EDT
	 */
	@Override
	protected void done() {
		List<Game> games = null;
		
		try {
			games = get();
		} 
		catch (InterruptedException | ExecutionException e) {
			StatusBar.printConnectionStatus("Chyba při aktualizaci seznamu her!");
			return;
		}
		
		if (games == null) {
			StatusBar.printConnectionStatus("Přerušeno spojení se serverem.");
			connectTimer.restart();
			return;
		}
		
		DefaultListModel<Game> gameListModel = (DefaultListModel<Game>) gameList.getModel();
		Game selectedGame = gameList.getSelectedValue();
		
		gameListModel.clear();
		for (Game game : games) {
			gameListModel.addElement(game);
		}
		
		gameList.setSelectedValue(selectedGame, true);
		
		// dosud nebyla vybrána hra
		if (!connectionManager.hasCurrentGame()) {
			gameList.setEnabled(true);
			createGameButton.setEnabled(true);
			leaveGameButton.setEnabled(false);
			
			(new ListReceiveWorker(connectionManager, gameFieldPanel, gameList, 
					createGameButton, leaveGameButton, connectTimer)).execute();
			
			return;
		}
		
		// uživatel vstoupil do hry
		gameList.setEnabled(false);
		createGameButton.setEnabled(false);
		leaveGameButton.setEnabled(true);
		
		// hra dosud nebyla spuštěna
		if (!connectionManager.getCurrentGame().isRunning()) {
			(new ListReceiveWorker(connectionManager, gameFieldPanel, gameList, 
					createGameButton, leaveGameButton, connectTimer)).execute();
			
			return;
		}
		
		// hra běží
		gameFieldPanel.createButtons(connectionManager);
		(new GameReceiveWorker(connectionManager, gameFieldPanel, gameList, 
				createGameButton, leaveGameButton, connectTimer)).execute();
	}
	
}