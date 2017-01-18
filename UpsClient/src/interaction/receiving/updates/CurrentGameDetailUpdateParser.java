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
import interaction.receiving.AUpdateParser;
import java.util.ArrayList;
import javax.swing.JList;
import visualisation.components.CurrentGamePanel;
import visualisation.components.GameListPanel;
import visualisation.components.PlayerListPanel;
import visualisation.listmodels.GameListModel;
import visualisation.listmodels.PlayerListModel;

/**
 * Třída CurrentGameDetailUpdateParser představuje parser notifikace
 * serveru o změně stavu aktuální herní místnosti.
 * 
 * @author Petr Kozler
 */
public class CurrentGameDetailUpdateParser extends AUpdateParser {

    /**
     * seznam hráčů
     */
    private final JList<PlayerInfo> PLAYER_LIST;
    
    /**
     * seznam her
     */
    private final JList<GameInfo> GAME_LIST;
    
    /**
     * model seznamu hráčů
     */
    private final PlayerListModel PLAYER_LIST_MODEL;
    
    /**
     * model seznamu her
     */
    private final GameListModel GAME_LIST_MODEL;
    
    /**
     * panel pro zobrazení herní místnosti
     */
    private final CurrentGamePanel CURRENT_GAME_PANEL;
    
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
     * @param playerListPanel panel seznamu hráčů
     * @param gameListPanel panel seznamu her
     * @param currentGamePanel panel stavu herní místnosti
     * @param message zpráva
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    public CurrentGameDetailUpdateParser(TcpClient client,
            PlayerListPanel playerListPanel, GameListPanel gameListPanel,
            CurrentGamePanel currentGamePanel, TcpMessage message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        super(client, message);
        
        PLAYER_LIST = playerListPanel.getPlayerList();
        GAME_LIST = gameListPanel.getGameList();
        PLAYER_LIST_MODEL = (PlayerListModel) PLAYER_LIST.getModel();
        GAME_LIST_MODEL = (GameListModel) GAME_LIST.getModel();
        CURRENT_GAME_PANEL = currentGamePanel;
        
        GameInfo currentGameInfo = client.getGameInfo();
        
        int currentRound = message.getNextIntArg(0);
        boolean roundFinished = message.getNextBoolArg();
        byte currentPlaying = message.getNextByteArg((byte) 0, (byte) (currentGameInfo.PLAYER_COUNT - 1));
        byte lastPlaying = message.getNextByteArg((byte) 0, (byte) (currentGameInfo.PLAYER_COUNT - 1));
        byte lastCellX = message.getNextByteArg((byte) 0, Config.MAX_BOARD_SIZE);
        byte lastCellY = message.getNextByteArg((byte) 0, Config.MAX_BOARD_SIZE);
        byte currentWinner = message.getNextByteArg((byte) 0, (byte) (currentGameInfo.PLAYER_COUNT - 1));
        byte firstWinnerCellX = message.getNextByteArg((byte) 0, (byte) (currentGameInfo.PLAYER_COUNT - 1));
        byte firstWinnerCellY = message.getNextByteArg((byte) 0, (byte) (currentGameInfo.PLAYER_COUNT - 1));
        byte lastWinnerCellX = message.getNextByteArg((byte) 0, (byte) (currentGameInfo.PLAYER_COUNT - 1));
        byte lastWinnerCellY = message.getNextByteArg((byte) 0, (byte) (currentGameInfo.PLAYER_COUNT - 1));
        byte[][] board = parseBoard(currentGameInfo, message.getNextArg());
        
        CURRENT_GAME_BOARD = new GameBoard(currentGameInfo, currentRound, roundFinished,
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
    private byte[][] parseBoard(GameInfo gameInfo, String boardStr)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        byte[][] board = new byte[gameInfo.BOARD_SIZE][gameInfo.BOARD_SIZE];
        
        if (boardStr.length() != gameInfo.BOARD_SIZE * gameInfo.BOARD_SIZE) {
            throw new InvalidMessageArgsException();
        }
        
        char[] boardArr = boardStr.toCharArray();
        
        try {
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board.length; j++) {
                    board[i][j] = (byte) Character.getNumericValue(boardArr[i * board.length + j]);
                    
                    if (board[i][j] < 0 || board[i][j] >= gameInfo.PLAYER_COUNT) {
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
        return JOINED_PLAYER_LIST.size() == CURRENT_GAME_BOARD.GAME_INFO.getPlayerCounter();
    }

    /**
     * Zpracuje další položku seznamu hráčů ve hře ze seznamu zpráv.
     * 
     * @param itemMessage zpráva
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
     * Aktualizuje objekty pro komunikaci a vrátí výsledek zpracování zprávy.
     * 
     * @return výsledek
     */
    @Override
    public String updateClient() {
        if (!hasAllItems()) {
            return null;
                /*String.format("Probíhá aktualizace stavu herní místností (zbývá %d položek)",
                    CURRENT_GAME_BOARD.GAME_INFO.getPlayerCounter() - JOINED_PLAYER_LIST.size());*/
        }
        
        return "Aktualizace stavu herní místností byla dokončena";
    }
    
    /**
     * Aktualizuje stav GUI.
     */
    @Override
    public void updateGui() {
        CurrentGameDetail currentGameDetail = new CurrentGameDetail(
                CURRENT_GAME_BOARD, JOINED_PLAYER_LIST);
        CURRENT_GAME_PANEL.setGameDetail(currentGameDetail);
    }
    
}
