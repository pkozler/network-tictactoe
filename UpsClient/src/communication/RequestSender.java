package communication;

import java.io.DataOutputStream;
import java.io.IOException;

import configuration.Config;
import visualization.StatusBar;

/**
 * Třída obsahuje komunikační funkce pro posílání požadavků uživatele.
 */
public class RequestSender {
	
	/** výstupní proud socketu */
	private DataOutputStream dos;
	
	/**
	 * Vytvoří nový vysílač.
	 */
	public RequestSender(DataOutputStream dos) {
		this.dos = dos;
	}
	
	/**
	 * Odešle požadavek na vytvoření hry 
	 * nebo připojení do hry nebo odpojení ze hry.
	 */
	public void sendGameRequest(Game game) {
		try {
			dos.writeInt(Config.MSG_CODE);
			
			if (game == null) {
				dos.writeInt(-1);
				StatusBar.printGameStatus("Odeslán požadavek na odstranění z aktuální hry.");
			}
			else if (game.GAME_ID != 0) {
				dos.writeInt(game.GAME_ID);
				StatusBar.printGameStatus("Odesláno ID požadované hry.");
			}
			else {
				dos.writeInt(game.GAME_ID);
				
				dos.writeByte(game.PLAYERS_SIZE);
				dos.writeByte(game.MATRIX_SIZE);
				dos.writeByte(game.WIN_ROW_LEN);
				
				StatusBar.printGameStatus("Odeslán požadavek na vytvoření hry.");
			}
		}
		catch (IOException ioe) {
			StatusBar.printConnectionStatus("Přerušeno spojení se serverem.");
		}
	}
	
	/**
	 * Odešle souřadnice herního tahu.
	 */
	public void sendPlayMessage(byte x, byte y) {
		try {
			dos.writeInt(Config.MSG_CODE);
			dos.writeByte(x);
			dos.writeByte(y);
			
			if (x == -1 && y == -1) {
				StatusBar.printGameStatus("Odeslán požadavek na opuštění hry.");
			}
			else {
				StatusBar.printGameStatus("Odeslán tah hráče.");
			}
		}
		catch (IOException ioe) {
			StatusBar.printConnectionStatus("Přerušeno spojení se serverem.");
		}
	}
	
}
