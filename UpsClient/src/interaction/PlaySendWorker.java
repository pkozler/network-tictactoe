package interaction;

import javax.swing.SwingWorker;

import communication.ConnectionManager;
import visualization.GameFieldPanel;
import visualization.StatusBar;

/**
 * SwingWorker pro odeslání herního tahu na pozadí a bezpečnou aktualizaci
 * hracího pole v uživatelském rozhraní.
 */
public class PlaySendWorker extends SwingWorker<Object, Object> {

	/** správce spojení */
	private ConnectionManager connectionManager;
	/** X-ová souřadnice tahu */
	private byte x;
	/** Y-ová souřadnice tahu */
	private byte y;
	
	/**
	 * Vytvoří nový SwingWorker pro odeslání herního tahu.
	 */
	public PlaySendWorker(ConnectionManager connectionManager, GameFieldPanel gameFieldPanel,
			byte x, byte y) {
		this.connectionManager = connectionManager;
		this.x = x;
		this.y = y;
	}
	
	/*
	 * zavolání komunikační funkce na pozadí
	 */
	@Override
	public Object doInBackground() {
		connectionManager.sendPlaySync(x, y);
		return null;
	}
	
	/*
	 * aktualizace GUI v EDT
	 */
	@Override
	protected void done() {
		StatusBar.printConnectionStatus("Odeslán tah.");
	}
	
}
