package interaction.receiving.updates;

import communication.TcpClient;
import communication.TcpMessage;
import communication.containers.Cell;
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
import visualisation.components.CurrentGameWindow;
import visualisation.components.GameListPanel;
import visualisation.components.PlayerListPanel;
import visualisation.listmodels.GameListModel;
import visualisation.listmodels.PlayerListModel;

/**
 *
 * @author Petr Kozler
 */
public class CurrentGameDetailUpdateParser extends AUpdateParser {

    private final JList<PlayerInfo> PLAYER_LIST;
    private final JList<GameInfo> GAME_LIST;
    private final PlayerListModel PLAYER_LIST_MODEL;
    private final GameListModel GAME_LIST_MODEL;
    private final CurrentGameWindow CURRENT_GAME_WINDOW;
    private final GameBoard CURRENT_GAME_BOARD;
    private final ArrayList<JoinedPlayer> JOINED_PLAYER_LIST;
    
    public CurrentGameDetailUpdateParser(TcpClient client,
            PlayerListPanel playerListPanel, GameListPanel gameListPanel,
            CurrentGameWindow currentGameWindow, TcpMessage message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        super(client, message);
        
        PLAYER_LIST = playerListPanel.getPlayerList();
        GAME_LIST = gameListPanel.getGameList();
        PLAYER_LIST_MODEL = (PlayerListModel) PLAYER_LIST.getModel();
        GAME_LIST_MODEL = (GameListModel) GAME_LIST.getModel();
        CURRENT_GAME_WINDOW = currentGameWindow;
        
        GameInfo currentGameInfo = GAME_LIST_MODEL.getElementByKey(client.getGameId());
        
        byte winnerIndex;
        byte currentIndex;
        Cell[][] cells;
        
        winnerIndex = message.getNextByteArg(Config.MIN_PLAYERS_SIZE, Config.MAX_PLAYERS_SIZE);
        currentIndex = message.getNextByteArg(Config.MIN_PLAYERS_SIZE, Config.MAX_PLAYERS_SIZE);
        cells = getCellsFromString(message.getNextArg(), currentGameInfo.BOARD_SIZE);

        CURRENT_GAME_BOARD = new GameBoard(currentGameInfo, winnerIndex, currentIndex, cells);
        JOINED_PLAYER_LIST = new ArrayList<>(CURRENT_GAME_BOARD.GAME_INFO.getPlayerCounter());
    }
    
    @Override
    public boolean hasNextItemMessage() {
        return JOINED_PLAYER_LIST.size() < CURRENT_GAME_BOARD.GAME_INFO.getPlayerCounter();
    }

    @Override
    public void parseNextItemMessage(TcpMessage itemMessage)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        int id = itemMessage.getNextIntArg(1);
        byte currentGameIndex = itemMessage.getNextByteArg((byte) 1, CURRENT_GAME_BOARD.GAME_INFO.PLAYER_COUNT);
        int currentGameScore = itemMessage.getNextIntArg(0);

        PlayerInfo joinedPlayerInfo = PLAYER_LIST_MODEL.getElementByKey(id);
        JOINED_PLAYER_LIST.add(new JoinedPlayer(joinedPlayerInfo, currentGameIndex, currentGameScore));
    }

    @Override
    public String getStatusAndUpdateGUI() {
        CurrentGameDetail currentGameDetail = new CurrentGameDetail(
                CURRENT_GAME_BOARD, JOINED_PLAYER_LIST);
        CURRENT_GAME_WINDOW.setGameDetail(currentGameDetail);
        
        return "Byl aktualizován stav herní místnosti.";
    }
    
    private Cell getCellFromSubstring(String cellSubstr) throws InvalidMessageArgsException {
        String cellSeedSubstr = cellSubstr.substring(0, Config.BOARD_CELL_SEED_SIZE);
        String cellWinFlagSubstr = cellSubstr.substring(Config.BOARD_CELL_SEED_SIZE);

        byte playerIndex;

        try {
            playerIndex = Byte.parseByte(cellSeedSubstr);
        }
        catch (NumberFormatException ex) {
            throw new InvalidMessageArgsException();
        }

        boolean winning;

        switch (cellWinFlagSubstr) {
            case Config.WINNING_CELL_SYMBOL: {
                winning = true;
                break;
            }
            case Config.NORMAL_CELL_SYMBOL: {
                winning = false;
                break;
            }
            default: {
                throw new InvalidMessageArgsException();
            }
        }

        return new Cell(playerIndex, winning);
    }
    
    private Cell[][] getCellsFromString(String boardString, int boardSize) throws InvalidMessageArgsException {
        String[] cellSubstrings = boardString.split(
                "(?=\\b[" + Config.NORMAL_CELL_SYMBOL + Config.WINNING_CELL_SYMBOL + "])");
        
        if (boardSize * boardSize != cellSubstrings.length) {
            throw new InvalidMessageArgsException();
        }
        
        Cell[][] cells = new Cell[boardSize][boardSize];
        
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                cells[i][j] = getCellFromSubstring(cellSubstrings[i * boardSize + j]);
            }
        }
        
        return cells;
    }

}
