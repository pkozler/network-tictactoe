package communication;

/**
 * Třída slouží k uchovávání stavu hry, ke které je hráč aktuálně připojen.
 */
public class Game {

	/** ID hry přiřazené serverem */
	public final int GAME_ID;
	/** velikost hracího pole */
	public final byte MATRIX_SIZE;
	/** počet hráčů */
	public final byte PLAYERS_SIZE;
	/** počet políček v řadě, nutných k obsazení, aby hráč zvítězil */
	public final byte WIN_ROW_LEN;
	/** obsazená políčka při vítězství */
	private byte[] winRow;
	/** herní ID (pořadí v poli hráčů přiřazené při vstupu do hry) */
	private byte playerId;
	/** herní ID hráče, který je aktuálně na tahu */
	private byte currentPlayer;
	/** aktuální počet hráčů připojených do hry */
	private byte playerCounter;
	/** herní ID posledního připojeného hráče */
	private byte lastAddedPlayer;
	/** herní ID posledního odpojeného hráče */
	private byte lastRemovedPlayer;
	/** X-ová souřadnice posledního tahu */
	private byte lastMoveX;
	/** Y-ová souřadnice posledního tahu */
	private byte lastMoveY;
	/** herní ID hráče, který provedl poslední tah */
	private byte lastMovePlayer;
	/** příznak rozehrání hry */
	private boolean running;
	/** herní ID vítězného hráče */
	private byte winner;
	
	/**
	 * Vytvoří nový stav hry.
	 */
	public Game(int gameId, byte playersSize, byte matrixSize,
			byte winRowLen, byte playerCounter, boolean running) {
		GAME_ID = gameId;
		MATRIX_SIZE = matrixSize;
		PLAYERS_SIZE = playersSize;
		WIN_ROW_LEN = winRowLen;
		this.playerCounter = playerCounter;
		this.running = running;
	}

	/**
	 * Vrátí herní ID.
	 */
	public synchronized byte getPlayerId() {
		return playerId;
	}
	
	/**
	 * Nastaví herní ID.
	 */
	public synchronized void setPlayerId(byte playerId) {
		this.playerId = playerId;
	}

	/**
	 * Vrátí čítač připojených hráčů.
	 */
	public synchronized byte getPlayerCounter() {
		return playerCounter;
	}

	/**
	 * Nastaví čítač připojených hráčů.
	 */
	public synchronized void setPlayerCounter(byte playerCounter) {
		this.playerCounter = playerCounter;
	}

	/**
	 * Otestuje příznak běhu hry.
	 */
	public synchronized boolean isRunning() {
		return running;
	}

	/**
	 * Nastaví příznak běhu hry.
	 */
	public synchronized void setRunning(boolean running) {
		this.running = running;
	}

	/**
	 * Vrátí vítězná políčka.
	 */
	public synchronized byte[] getWinRow() {
		return winRow;
	}

	/**
	 * Nastaví vítězná políčka.
	 */
	public synchronized void setWinRow(byte[] winRow) {
		this.winRow = winRow;
	}

	/**
	 * Vrátí aktuálního hráče.
	 */
	public synchronized byte getCurrentPlayer() {
		return currentPlayer;
	}

	/**
	 * Nastaví aktuálního hráče.
	 */
	public synchronized void setCurrentPlayer(byte currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	/**
	 * Vrátí posledního připojeného hráče.
	 */
	public synchronized byte getLastAddedPlayer() {
		return lastAddedPlayer;
	}

	/**
	 * Nastaví posledního připojeného hráče.
	 */
	public synchronized void setLastAddedPlayer(byte lastAddedPlayer) {
		this.lastAddedPlayer = lastAddedPlayer;
	}

	/**
	 * Vrátí posledního odpojeného hráče.
	 */
	public synchronized byte getLastRemovedPlayer() {
		return lastRemovedPlayer;
	}

	/**
	 * Nastaví posledního odpojeného hráče.
	 */
	public synchronized void setLastRemovedPlayer(byte lastRemovedPlayer) {
		this.lastRemovedPlayer = lastRemovedPlayer;
	}

	/**
	 * Vrátí X-ovou souřadnici tahu.
	 */
	public synchronized byte getLastMoveX() {
		return lastMoveX;
	}

	/**
	 * Nastaví X-ovou souřadnici tahu.
	 */
	public synchronized byte getLastMoveY() {
		return lastMoveY;
	}

	/**
	 * Vrátí Y-ovou souřadnici tahu.
	 */
	public synchronized byte getLastMovePlayer() {
		return lastMovePlayer;
	}

	/**
	 * Nastaví Y-ovou souřadnici tahu.
	 */
	public synchronized void setLastMove(byte lastMoveX, byte lastMoveY, byte lastMovePlayer) {
		this.lastMoveX = lastMoveX;
		this.lastMoveY = lastMoveY;
		this.lastMovePlayer = lastMovePlayer;
	}

	/**
	 * Vrátí vítězného hráče.
	 */
	public synchronized byte getWinner() {
		return winner;
	}

	/**
	 * Nastaví vítězného hráče.
	 */
	public synchronized void setWinner(byte winner) {
		this.winner = winner;
	}

	/*
	 * Vrátí textovou reprezentaci hry pro zobrazení v seznamu.
	 */
	@Override
	public synchronized String toString() {
		return String.format("<html>Hra %d<br/>pole %d*%d - hraje se na %d<br/>připojeno hráčů %d/%d - %s</html>", 
				GAME_ID, MATRIX_SIZE, MATRIX_SIZE, WIN_ROW_LEN, playerCounter, PLAYERS_SIZE,
				running ? "OBSAZENO" : "VOLNO");
	}

	/*
	 * Vrátí heškód hry.
	 */
	@Override
	public synchronized int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + GAME_ID;
		return result;
	}

	/*
	 * Otestuje totožnost dvou her podle jejich unikátního ID.
	 */
	@Override
	public synchronized boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Game other = (Game) obj;
		if (GAME_ID != other.GAME_ID)
			return false;
		return true;
	}

}
