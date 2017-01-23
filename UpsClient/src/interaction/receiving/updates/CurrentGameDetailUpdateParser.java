package interaction.receiving.updates;

import communication.TcpClient;
import communication.Message;
import communication.containers.CurrentGameDetail;
import communication.containers.GameBoard;
import communication.containers.JoinedPlayer;
import communication.tokens.InvalidMessageArgsException;
import communication.tokens.MissingMessageArgsException;
import configuration.Config;
import interaction.receiving.AUpdateParser;
import java.util.ArrayList;

/**
 * Třída CurrentGameDetailUpdateParser představuje parser notifikace
 * serveru o změně stavu aktuální herní místnosti.
 * 
 * @author Petr Kozler
 */
public class CurrentGameDetailUpdateParser extends AUpdateParser {

    /**
     * herní pole
     */
    private final GameBoard CURRENT_GAME_BOARD;
    
    /**
     * seznam hráčů v aktuální hře
     */
    private final ArrayList<JoinedPlayer> JOINED_PLAYER_LIST;
    
    /**
     * Vytvoří parser stavu hry.
     * 
     * @param client objekt klienta
     * @param message zpráva
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    public CurrentGameDetailUpdateParser(TcpClient client, Message message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        super(client, message);
        
        int id = message.getNextIntArg(0);
        byte playerCounter = message.getNextByteArg((byte) 0, (byte) (Config.MAX_PLAYERS_SIZE));
        byte boardSize = message.getNextByteArg((byte) 0, (byte) (Config.MAX_BOARD_SIZE));
        int currentRound = message.getNextIntArg(0);
        boolean roundFinished = message.getNextBoolArg();
        byte currentPlaying = message.getNextByteArg((byte) 0, (byte) (Config.MAX_PLAYERS_SIZE));
        byte lastPlaying = message.getNextByteArg((byte) 0, (byte) (Config.MAX_PLAYERS_SIZE));
        byte lastCellX = message.getNextByteArg((byte) 0, (byte) (Config.MAX_BOARD_SIZE - 1));
        byte lastCellY = message.getNextByteArg((byte) 0, (byte) (Config.MAX_BOARD_SIZE - 1));
        byte currentWinner = message.getNextByteArg((byte) 0, (byte) (Config.MAX_PLAYERS_SIZE));
        byte firstWinnerCellX = message.getNextByteArg((byte) 0, (byte) (Config.MAX_BOARD_SIZE - 1));
        byte firstWinnerCellY = message.getNextByteArg((byte) 0, (byte) (Config.MAX_BOARD_SIZE - 1));
        byte lastWinnerCellX = message.getNextByteArg((byte) 0, (byte) (Config.MAX_BOARD_SIZE - 1));
        byte lastWinnerCellY = message.getNextByteArg((byte) 0, (byte) (Config.MAX_BOARD_SIZE - 1));
        byte[][] board = parseBoard(boardSize, message.getNextArg());
        
        CURRENT_GAME_BOARD = new GameBoard(id, playerCounter, boardSize, currentRound, roundFinished,
                currentPlaying, lastPlaying, lastCellX, lastCellY, currentWinner,
                firstWinnerCellX, firstWinnerCellY, lastWinnerCellX, lastWinnerCellY, board);
        JOINED_PLAYER_LIST = new ArrayList<>();
    }
    
    /**
     * Zpracuje řetězec herního pole.
     * 
     * @param gameInfo základní informace o hře
     * @param boardStr řetězec herního pole
     * @return herní pole
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    private byte[][] parseBoard(byte boardSize, String boardStr)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        byte[][] board = new byte[boardSize][boardSize];
        
        if (boardStr.length() != (int) (boardSize * boardSize)) {
            throw new InvalidMessageArgsException();
        }
        
        char[] boardArr = boardStr.toCharArray();
        
        try {
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board.length; j++) {
                    board[i][j] = (byte) Character.getNumericValue(boardArr[i * board.length + j]);
                    
                    if (board[i][j] < 0 || board[i][j] >= Config.MAX_PLAYERS_SIZE) {
                        throw new InvalidMessageArgsException();
                    }
                }
            }
        }
        catch (NumberFormatException e) {
            throw new InvalidMessageArgsException();
        }
        
        return board;
    }
    
    /**
     * Otestuje, zda seznam hráčů ve hře již obsahuje všechny položky.
     * 
     * @return true, pokud seznam obsahuje všechny položky, jinak false
     */
    @Override
    public boolean hasAllItems() {
        return JOINED_PLAYER_LIST.size() == CURRENT_GAME_BOARD.getPlayerCounter();
    }

    /**
     * Zpracuje další položku seznamu hráčů ve hře ze seznamu zpráv.
     * 
     * @param itemMessage zpráva
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    @Override
    public void parseNextItemMessage(Message itemMessage)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        int id = itemMessage.getNextIntArg(0);
        boolean playing = itemMessage.getNextBoolArg();
        String nickname = itemMessage.getNextArg();
        byte currentGameIndex = itemMessage.getNextByteArg(
                (byte) 1, Config.MAX_PLAYERS_SIZE);
        int currentGameScore = itemMessage.getNextIntArg(0);
        
        JOINED_PLAYER_LIST.add(new JoinedPlayer(id, playing, nickname, currentGameIndex, currentGameScore));
    }

    /**
     * Aktualizuje objekty pro komunikaci a vrátí výsledek zpracování zprávy.
     * 
     * @return výsledek
     */
    @Override
    public String updateClient() {
        if (!hasAllItems()) {
            return null;
        }
        
        CLIENT.setGameDetail(new CurrentGameDetail(CURRENT_GAME_BOARD, JOINED_PLAYER_LIST));
        
        return "Aktualizace stavu herní místností byla dokončena";
    }
    
}
