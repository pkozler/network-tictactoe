package interaction.receiving.updates;

import communication.TcpClient;
import communication.TcpMessage;
import communication.containers.GameBoard;
import communication.containers.GameInfo;
import visualisation.CurrentGameDetail;
import communication.containers.JoinedPlayer;
import communication.containers.PlayerInfo;
import communication.tokens.InvalidMessageArgsException;
import communication.tokens.MissingMessageArgsException;
import configuration.Config;
import configuration.Protocol;
import interaction.receiving.AUpdateParser;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.swing.JList;
import visualisation.components.CurrentGamePanel;
import visualisation.components.GameListPanel;
import visualisation.components.PlayerListPanel;
import visualisation.listmodels.GameListModel;
import visualisation.listmodels.PlayerListModel;

/**
 * Třída CurrentGameDetailUpdateParser 
 * 
 * @author Petr Kozler
 */
public class CurrentGameDetailUpdateParser extends AUpdateParser {

    /**
     * 
     */
    private final JList<PlayerInfo> PLAYER_LIST;
    
    /**
     * 
     */
    private final JList<GameInfo> GAME_LIST;
    
    /**
     * 
     */
    private final PlayerListModel PLAYER_LIST_MODEL;
    
    /**
     * 
     */
    private final GameListModel GAME_LIST_MODEL;
    
    /**
     * 
     */
    private final CurrentGamePanel CURRENT_GAME_WINDOW;
    
    /**
     * 
     */
    private final GameBoard CURRENT_GAME_BOARD;
    
    /**
     * 
     */
    private final ArrayList<JoinedPlayer> JOINED_PLAYER_LIST;
    
    /**
     * 
     * 
     * @param client
     * @param playerListPanel
     * @param gameListPanel
     * @param currentGameWindow
     * @param message
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    public CurrentGameDetailUpdateParser(TcpClient client,
            PlayerListPanel playerListPanel, GameListPanel gameListPanel,
            CurrentGamePanel currentGameWindow, TcpMessage message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        super(client, message);
        
        PLAYER_LIST = playerListPanel.getPlayerList();
        GAME_LIST = gameListPanel.getGameList();
        PLAYER_LIST_MODEL = (PlayerListModel) PLAYER_LIST.getModel();
        GAME_LIST_MODEL = (GameListModel) GAME_LIST.getModel();
        CURRENT_GAME_WINDOW = currentGameWindow;
        
        GameInfo currentGameInfo = GAME_LIST_MODEL.getElementByKey(client.getGameId());
        
        int currentRound = message.getNextIntArg(0);
        boolean roundFinished = message.getNextBoolArg();
        byte currentPlaying = message.getNextByteArg(Config.MIN_PLAYERS_SIZE, Config.MAX_PLAYERS_SIZE);
        byte lastPlaying = message.getNextByteArg(Config.MIN_PLAYERS_SIZE, Config.MAX_PLAYERS_SIZE);
        byte lastCellX = message.getNextByteArg((byte) 0, Config.MAX_BOARD_SIZE);
        byte lastCellY = message.getNextByteArg((byte) 0, Config.MAX_BOARD_SIZE);
        byte lastLeaving = message.getNextByteArg(Config.MIN_PLAYERS_SIZE, Config.MAX_PLAYERS_SIZE);
        byte currentWinner = message.getNextByteArg(Config.MIN_PLAYERS_SIZE, Config.MAX_PLAYERS_SIZE);
        byte[] winnerCellsX = parseWinnerCells(currentGameInfo, currentWinner, message.getNextArg());
        byte[] winnerCellsY = parseWinnerCells(currentGameInfo, currentWinner, message.getNextArg());
        byte[][] board = parseBoard(currentGameInfo, message.getNextArg());
        
        CURRENT_GAME_BOARD = new GameBoard(currentGameInfo, currentRound, roundFinished,
                currentPlaying, lastPlaying, lastCellX, lastCellY, lastLeaving, currentWinner,
                winnerCellsX, winnerCellsY, board);
        JOINED_PLAYER_LIST = new ArrayList<>(CURRENT_GAME_BOARD.GAME_INFO.getPlayerCounter());
    }
    
    /**
     * 
     * 
     * @param gameInfo
     * @param currentWinner
     * @param str
     * @return
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    private byte[] parseWinnerCells(GameInfo gameInfo, byte currentWinner, String str)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        byte coords[] = new byte[gameInfo.CELL_COUNT];
        
        if (currentWinner < 1) {
            return coords;
        }
        
        String[] parts = str.split(",");
        
        if (parts.length != coords.length) {
            throw new InvalidMessageArgsException();
        }
        
        try {
            for (int i = 0; i < coords.length; i++) {
                coords[i] = Byte.parseByte(parts[i]);
            }
        }
        catch (NumberFormatException e) {
            throw new InvalidMessageArgsException();
        }
        
        return coords;
    }
    
    /**
     * 
     * 
     * @param gameInfo
     * @param str
     * @return
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    private byte[][] parseBoard(GameInfo gameInfo, String str)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        byte[][] board = new byte[gameInfo.BOARD_SIZE][gameInfo.BOARD_SIZE];
        
        if (str.length() != gameInfo.BOARD_SIZE * gameInfo.BOARD_SIZE) {
            throw new InvalidMessageArgsException();
        }
        
        try {
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board.length; j++) {
                    board[i][j] = (byte) Character.getNumericValue(
                            str.charAt(i * board.length + j));
                }
            }
        }
        catch (NumberFormatException e) {
            throw new InvalidMessageArgsException();
        }
        
        return board;
    }
    
    /**
     * 
     * 
     * @return 
     */
    @Override
    public boolean hasNextItemMessage() {
        return JOINED_PLAYER_LIST.size() < CURRENT_GAME_BOARD.GAME_INFO.getPlayerCounter();
    }

    /**
     * 
     * 
     * @param itemMessage
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    @Override
    public void parseNextItemMessage(TcpMessage itemMessage)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        int id = itemMessage.getNextIntArg(1);
        byte currentGameIndex = itemMessage.getNextByteArg((byte) 1, CURRENT_GAME_BOARD.GAME_INFO.PLAYER_COUNT);
        int currentGameScore = itemMessage.getNextIntArg(0);

        PlayerInfo joinedPlayerInfo = PLAYER_LIST_MODEL.getElementByKey(id);
        JOINED_PLAYER_LIST.add(new JoinedPlayer(joinedPlayerInfo, currentGameIndex, currentGameScore));
    }

    /**
     * 
     * 
     * @return 
     */
    @Override
    public String getStatusAndUpdateGUI() {
        if (hasNextItemMessage()) {
            return String.format("Probíhá aktualizace stavu herní místností (zbývá %d položek)",
                    CURRENT_GAME_BOARD.GAME_INFO.getPlayerCounter() - JOINED_PLAYER_LIST.size());
        }
        
        CurrentGameDetail currentGameDetail = new CurrentGameDetail(
                CURRENT_GAME_BOARD, JOINED_PLAYER_LIST);
        CURRENT_GAME_WINDOW.setGameDetail(currentGameDetail);
        
        return "Aktualizace stavu herní místností byla dokončena";
    }
    
}
