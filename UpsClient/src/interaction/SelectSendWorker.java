package interaction;

import javax.swing.JList;
import javax.swing.SwingWorker;

import communication.ConnectionManager;
import communication.Game;
import visualization.GameFieldPanel;
import visualization.StatusBar;

/**
 * SwingWorker pro odeslání výběru hry na pozadí a bezpečnou aktualizaci
 * seznamu v uživatelském rozhraní.
 */
public class SelectSendWorker extends SwingWorker<Object, Object> {

	/** správce spojení */
	private ConnectionManager connectionManager;
	/** aktuálně zvolená hra */
	private Game selectedGame;
	
	/**
	 * Vytvoří nový SwingWorker pro odeslání výběru hry.
	 */
	public SelectSendWorker(ConnectionManager connectionManager, 
			GameFieldPanel gameFieldPanel, JList<Game> gameList, Game selectedGame) {
		this.connectionManager = connectionManager;
		this.selectedGame = selectedGame;
	}
	
	/*
	 * zavolání komunikační funkce na pozadí
	 */
	@Override
	public Object doInBackground() {
		connectionManager.sendSelectSync(selectedGame);
		return null;
	}
	
	/*
	 * aktualizace GUI v EDT
	 */
	@Override
	protected void done() {
		StatusBar.printConnectionStatus("Odeslán požadavek na vytvoření/připojení do hry.");
	}
	
}
