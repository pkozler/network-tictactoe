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
     * pořadí vítěze
     */
    private byte currentWinner;
    
    /**
     * souřadnice X prvního vítězného políčka
     */
    private byte firstWinnerCellX;
    
    /**
     * souřadnice Y prvního vítězného políčka
     */
    private byte firstWinnerCellY;
    
    /**
     * souřadnice X posledního vítězného políčka
     */
    private byte lastWinnerCellX;
    
    /**
     * souřadnice Y posledního vítězného políčka
     */
    private byte lastWinnerCellY;
    
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
     * @param currentWinner pořadí vítěze
     * @param firstWinnerCellX souřadnice X prvního vítězného políčka
     * @param firstWinnerCellY souřadnice Y prvního vítězného políčka
     * @param lastWinnerCellX souřadnice X posledního vítězného políčka
     * @param lastWinnerCellY souřadnice Y posledního vítězného políčka
     * @param cells herní pole
     */
    public GameBoard(GameInfo gameInfo, int currentRound, boolean roundFinished,
            byte currentPlaying, byte lastPlaying, byte lastCellX, byte lastCellY,
            byte currentWinner, byte firstWinnerCellX, byte firstWinnerCellY,
            byte lastWinnerCellX, byte lastWinnerCellY, byte[][] cells) {
        GAME_INFO = gameInfo;
        this.currentRound = currentRound;
        this.roundFinished = roundFinished;
        this.currentPlaying = currentPlaying;
        this.lastPlaying = lastPlaying;
        this.lastCellX = lastCellX;
        this.lastCellY = lastCellY;
        this.currentWinner = currentWinner;
        this.firstWinnerCellX = firstWinnerCellX;
        this.firstWinnerCellY = firstWinnerCellY;
        this.lastWinnerCellX = lastWinnerCellX;
        this.lastWinnerCellY = lastWinnerCellY;
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
     * Vrátí pořadí vítěze.
     * 
     * @return pořadí vítěze
     */
    public byte getCurrentWinner() {
        return currentWinner;
    }

    /**
     * Vrátí souřadnici X prvního vítězného políčka.
     * 
     * @return souřadnice X prvního vítězného políčka
     */
    public byte getFirstWinnerCellX() {
        return firstWinnerCellX;
    }

    /**
     * Vrátí souřadnici Y prvního vítězného políčka.
     * 
     * @return souřadnice Y prvního vítězného políčka
     */
    public byte getFirstWinnerCellY() {
        return firstWinnerCellY;
    }

    /**
     * Vrátí souřadnici X posledního vítězného políčka.
     * 
     * @return souřadnice X posledního vítězného políčka
     */
    public byte getLastWinnerCellX() {
        return lastWinnerCellX;
    }

    /**
     * Vrátí souřadnici Y posledního vítězného políčka.
     * 
     * @return souřadnice Y posledního vítězného políčka
     */
    public byte getLastWinnerCellY() {
        return lastWinnerCellY;
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
