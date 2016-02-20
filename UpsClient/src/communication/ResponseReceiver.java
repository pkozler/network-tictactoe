package communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import configuration.Config;
import visualization.StatusBar;

/**
 * Třída obsahuje komunikační funkce pro příjem odpovědí serveru
 * a odesílání potvrzení o příjmu těchto odpovědí.
 */
public class ResponseReceiver {
	
	/** vstupní proud socketu */
	private DataInputStream dis;
	/** výstupní proud socketu */
	private DataOutputStream dos;
	
	/**
	 * Vytvoří nový přijímač.
	 */
	public ResponseReceiver(DataInputStream dis, DataOutputStream dos) {
		this.dis = dis;
		this.dos = dos;
	}

	/**
	 * Přijme seznam dostupných her
	 * a odešle potvrzení o příjmu.
	 */
	public Game receiveGameList(List<Game> games) throws IOException {
		Game currentGame = null;
		MainLoop:
		while (true) {
			int code = dis.readInt();
			if (code != Config.MSG_CODE) {
				StatusBar.printGameStatus("Odmítnuta neplatná data seznamu her.");
				continue;
			}
			
			int gameCount = dis.readInt();
			
			if (gameCount < 0) {
				StatusBar.printGameStatus("Odmítnut neplatný počet her.");
				continue;
			}
			
			int currentGameId = dis.readInt();
			
			if (currentGameId < 0) {
				StatusBar.printGameStatus("Odmítnuto neplatné ID aktuální hry.");
				continue;
			}

			StatusBar.printGameStatus("Přijato ID aktuální hry: " + currentGameId + ".");
			
			for (int i = 0; i < gameCount; i++) {
				int gameId = dis.readInt();
				
				if (gameId < 0) {
					StatusBar.printGameStatus("Odmítnuto neplatné ID hry.");
					continue MainLoop;
				}
				
				byte matrixSize = dis.readByte();
				
				if (matrixSize < Config.MIN_MATRIX_SIZE || matrixSize > Config.MAX_MATRIX_SIZE) {
					StatusBar.printGameStatus(String.format("Odmítnuta velikost herního pole"
							+ " mimo povolený interval <%d, %d>.\n", 
							Config.MIN_MATRIX_SIZE, Config.MAX_MATRIX_SIZE));
					continue MainLoop;
				}
				
				byte winRowLen = dis.readByte();
				
				if (winRowLen < Config.MIN_MATRIX_SIZE || winRowLen > Config.MAX_MATRIX_SIZE) {
					StatusBar.printGameStatus(String.format("Odmítnut počet obsazených políček pro vítězství"
							+ " mimo povolený interval <%d, %d>.\n", 
							Config.MIN_MATRIX_SIZE, Config.MAX_MATRIX_SIZE));
					continue MainLoop;
				}
				
				byte playersSize = dis.readByte();
				
				if (playersSize < Config.MIN_PLAYERS_SIZE || playersSize > Config.MAX_PLAYERS_SIZE) {
					StatusBar.printGameStatus(String.format("Odmítnut počet hráčů"
							+ " mimo povolený interval <%d, %d>.\n", 
							Config.MIN_PLAYERS_SIZE, Config.MAX_PLAYERS_SIZE));
					continue MainLoop;
				}
				
				byte playerCounter = dis.readByte();
				
				if (playerCounter < 0 || playerCounter > playersSize) {
					StatusBar.printGameStatus("Odmítnut neplatný aktuální počet hráčů.");
					continue MainLoop;
				}
				
				byte r = dis.readByte();
				
				if (r != (byte) 0 && r != (byte) 1) {
					StatusBar.printGameStatus("Odmítnut neplatný příznak běhu hry.");
					continue MainLoop;
				}
				
				boolean running = (r != (byte) 0);
				
				Game game = new Game(gameId, playersSize, matrixSize, winRowLen, playerCounter, running);
				
				if (currentGameId == game.GAME_ID) {
					currentGame = game;
				}
				
				games.add(game);
				
				StatusBar.printGameStatus("Přijaty informace o hře s ID " + gameId + ".");
			}
			
			StatusBar.printGameStatus("Přijat seznam vytvořených her.");
			dos.writeInt(Config.ACK_CODE);
			
			//StatusBar.printGameStatus("Odesláno potvrzení o přijetí seznamu her.");
			return currentGame;
		}
	}

	/**
	 * Přijme stav aktuální hry
	 * a odešle potvrzení o příjmu.
	 */
	public boolean receiveStatus(Game currentGame) throws IOException {
		while (true) {
			int code = dis.readInt();
			if (code != Config.MSG_CODE) {
				StatusBar.printGameStatus("Odmítnuta neplatná data stavu hry.");
				continue;
			}
			
			byte playerId = dis.readByte();
			StatusBar.printUserInstruction("Přijato pořadí ve hře: " + playerId);
			currentGame.setPlayerId(playerId);
			
			byte r = dis.readByte();
			
			if (r != (byte) 0 && r != (byte) 1) {
				StatusBar.printGameStatus("Odmítnut neplatný příznak běhu hry.");
				continue;
			}
			
			boolean running = (r != (byte) 0);
			StatusBar.printUserInstruction(running ? "Hra pokračuje." : "Hra ještě nezačala.");
			currentGame.setRunning(running);
			
			byte currentPlayer = dis.readByte();
			
			if (currentPlayer == currentGame.getPlayerId()) {
				StatusBar.printUserInstruction("Jste na tahu!");
			}
			else {
				StatusBar.printUserInstruction("Na tahu je hráč s číslem: " + currentPlayer);
			}
			
			currentGame.setCurrentPlayer(currentPlayer);

			byte lastAddedPlayer = dis.readByte();
			
			if (lastAddedPlayer < (byte) 0 || lastAddedPlayer > currentGame.PLAYERS_SIZE) {
				StatusBar.printConnectionStatus("Odmítnuto neplatné pořadí právě připojeného hráče.");
				continue;
			}
			
			if (lastAddedPlayer != (byte) 0) {
				StatusBar.printConnectionStatus("Hráč s číslem " + lastAddedPlayer + " se připojil do hry.");
				currentGame.setLastAddedPlayer(lastAddedPlayer);
			}
			
			byte lastRemovedPlayer = dis.readByte();
			
			if (lastRemovedPlayer < (byte) 0 || lastRemovedPlayer > currentGame.PLAYERS_SIZE) {
				StatusBar.printConnectionStatus("Odmítnuto neplatné pořadí právě odpojeného hráče.");
				continue;
			}
			
			if (lastRemovedPlayer != (byte) 0) {
				StatusBar.printConnectionStatus("Hráč s číslem " + lastRemovedPlayer + " se odpojil ze hry.");
				currentGame.setLastRemovedPlayer(lastRemovedPlayer);
			}
			
			byte lastMovePlayer = dis.readByte();
			
			if (lastMovePlayer < (byte) 0 || lastMovePlayer > (byte) currentGame.PLAYERS_SIZE) {
				StatusBar.printGameStatus("Odmítnuto neplatné pořadí táhnoucího hráče.");
				continue;
			}
			
			byte lastMoveX = -1;
			byte lastMoveY = -1;
			
			if (lastMovePlayer != (byte) 0) {
				lastMoveX = dis.readByte();
				
				if (lastMoveX < (byte) 0 || lastMoveX >= (byte) currentGame.MATRIX_SIZE) {
					StatusBar.printGameStatus("Odmítnut tah na neexistujících souřadnicích.");
					continue;
				}

				lastMoveY = dis.readByte();

				if (lastMoveY < (byte) 0 || lastMoveY >= (byte) currentGame.MATRIX_SIZE) {
					StatusBar.printGameStatus("Odmítnut tah na neexistujících souřadnicích.");
					continue;
				}
				
				StatusBar.printGameStatus("Hráč s číslem " + lastMovePlayer + " táhl na souřadnicích {" 
						+ lastMoveX + ", " + lastMoveY + "}.");
			}
			
			currentGame.setLastMove(lastMoveX, lastMoveY, lastMovePlayer);
			
			byte winner = dis.readByte();
			StatusBar.printGameStatus("Vítězem je hráč s číslem: " + winner);
			currentGame.setWinner(winner);
			
			if (winner != 0) {
				byte[] winRow = new byte[currentGame.WIN_ROW_LEN * 2];
				dis.read(winRow);
				//StatusBar.printGameStatus("Přijata vítězná políčka.");
				currentGame.setWinRow(winRow);
			}
			
			dos.writeInt(Config.ACK_CODE);
			//StatusBar.printGameStatus("Odesláno potvrzení o přijetí stavu hry.");

			return true;
		}

	}
	
}
