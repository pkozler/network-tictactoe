package communication.containers;

/**
 *
 * @author Petr Kozler
 */
public class GameBoard {
    
    public final GameInfo GAME_INFO;
    private int currentRound;
    private boolean roundFinished;
    private byte currentPlaying;
    private byte lastPlaying;
    private byte lastCellX;
    private byte lastCellY;
    private byte lastLeaving;
    private byte currentWinner;
    private byte[] winnerCellsX;
    private byte[] winnerCellsY;
    private byte[][] board;

    public GameBoard(GameInfo gameInfo, int currentRound, boolean roundFinished,
            byte currentPlaying, byte lastPlaying, byte lastCellX, byte lastCellY,
            byte lastLeaving, byte currentWinner, byte[] winnerCellsX, byte[] winnerCellsY,
            byte[][] cells) {
        GAME_INFO = gameInfo;
        this.currentRound = currentRound;
        this.roundFinished = roundFinished;
        this.currentPlaying = currentPlaying;
        this.lastPlaying = lastPlaying;
        this.lastCellX = lastCellX;
        this.lastCellY = lastCellY;
        this.lastLeaving = lastLeaving;
        this.currentWinner = currentWinner;
        this.winnerCellsX = winnerCellsX;
        this.winnerCellsY = winnerCellsY;
        this.board = cells;
    }

    public GameInfo getGAME_INFO() {
        return GAME_INFO;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public boolean isRoundFinished() {
        return roundFinished;
    }

    public byte getCurrentPlaying() {
        return currentPlaying;
    }

    public byte getLastPlaying() {
        return lastPlaying;
    }

    public byte getLastCellX() {
        return lastCellX;
    }

    public byte getLastCellY() {
        return lastCellY;
    }

    public byte getLastLeaving() {
        return lastLeaving;
    }

    public byte getCurrentWinner() {
        return currentWinner;
    }

    public byte[] getWinnerCellsX() {
        return winnerCellsX;
    }

    public byte[] getWinnerCellsY() {
        return winnerCellsY;
    }

    public byte[][] getBoard() {
        return board;
    }

}
