package communication.containers;

/**
 * Třída GameBoard slouží jako přepravka pro uchování informací
 * o stavu herního pole.
 * 
 * @author Petr Kozler
 */
public class GameBoard {
    
    /**
     * základní informace o hře
     */
    public final GameInfo GAME_INFO;
    
    /**
     * aktuální kolo hry
     */
    private int currentRound;
    
    /**
     * příznak dokončení kola
     */
    private boolean roundFinished;
    
    /**
     * pořadí aktuálního hráče
     */
    private byte currentPlaying;
    
    /**
     * pořadí posledního táhnoucího hráče
     */
    private byte lastPlaying;
    
    /**
     * souřadnice X posledního tahu
     */
    private byte lastCellX;
    
    /**
     * souřadnice Y posledního tahu
     */
    private byte lastCellY;
    
    /**
     * pořadí posledního hráče, který odešel ze hry
     */
    private byte lastLeaving;
    
    /**
     * pořadí vítěze
     */
    private byte currentWinner;
    
    /**
     * souřadnice X vítězných políček
     */
    private byte[] winnerCellsX;
    
    /**
     * souřadnice Y vítězných políček
     */
    private byte[] winnerCellsY;
    
    /**
     * herní pole
     */
    private byte[][] board;

    /**
     * Vytvoří přepravku stavu herního pole.
     * 
     * @param gameInfo základní informace o hře
     * @param currentRound aktuální kolo hry
     * @param roundFinished příznak dokončení kola
     * @param currentPlaying pořadí aktuálního hráče
     * @param lastPlaying pořadí posledního táhnoucího hráče
     * @param lastCellX souřadnice X posledního tahu
     * @param lastCellY souřadnice Y posledního tahu
     * @param lastLeaving pořadí posledního hráče, který odešel ze hry
     * @param currentWinner pořadí vítěze
     * @param winnerCellsX souřadnice X vítězných políček
     * @param winnerCellsY souřadnice Y vítězných políček
     * @param cells herní pole
     */
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

    /**
     * Vrátí aktuální kolo hry.
     * 
     * @return aktuální kolo hry
     */
    public int getCurrentRound() {
        return currentRound;
    }

    /**
     * Vrátí příznak dokončení kola.
     * 
     * @return příznak dokončení kola
     */
    public boolean isRoundFinished() {
        return roundFinished;
    }

    /**
     * Vrátí pořadí aktuálního hráče.
     * 
     * @return pořadí aktuálního hráče
     */
    public byte getCurrentPlaying() {
        return currentPlaying;
    }

    /**
     * Vrátí pořadí posledního táhnoucího hráče.
     * 
     * @return pořadí posledního táhnoucího hráče
     */
    public byte getLastPlaying() {
        return lastPlaying;
    }

    /**
     * Vrátí souřadnice X posledního tahu.
     * 
     * @return souřadnice X posledního tahu
     */
    public byte getLastCellX() {
        return lastCellX;
    }

    /**
     * Vrátí souřadnice Y posledního tahu.
     * 
     * @return souřadnice Y posledního tahu
     */
    public byte getLastCellY() {
        return lastCellY;
    }

    /**
     * Vrátí pořadí posledního hráče, který odešel ze hry.
     * 
     * @return pořadí posledního hráče, který odešel ze hry
     */
    public byte getLastLeaving() {
        return lastLeaving;
    }

    /**
     * Vrátí pořadí vítěze.
     * 
     * @return pořadí vítěze
     */
    public byte getCurrentWinner() {
        return currentWinner;
    }

    /**
     * Vrátí souřadnice X vítězných políček.
     * 
     * @return souřadnice X vítězných políček
     */
    public byte[] getWinnerCellsX() {
        return winnerCellsX;
    }

    /**
     * Vrátí souřadnice Y vítězných políček.
     * 
     * @return souřadnice Y vítězných políček
     */
    public byte[] getWinnerCellsY() {
        return winnerCellsY;
    }

    /**
     * Vrátí herní pole.
     * 
     * @return herní pole
     */
    public byte[][] getBoard() {
        return board;
    }

}
