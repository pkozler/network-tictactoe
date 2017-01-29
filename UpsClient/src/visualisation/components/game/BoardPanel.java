package visualisation.components.game;

import communication.containers.GameBoard;
import interaction.MessageBackgroundSender;
import interaction.sending.requests.PlayGameRequestBuilder;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import javax.swing.JPanel;
import visualisation.components.game.icons.CircleIcon;
import visualisation.components.game.icons.CrossIcon;
import visualisation.components.game.icons.TildeIcon;
import visualisation.components.game.icons.YpsilonIcon;

/**
 * Třída BoardPanel představuje panel pro zobrazení herního pole
 * aktuální herní místnosti.
 * 
 * @author Petr Kozler
 */
public class BoardPanel extends JPanel {

    /**
     * vysílač zpráv
     */
    private final MessageBackgroundSender MESSAGE_SENDER;
    
    /**
     * index X aktuálního označeného políčka v herním poli
     */
    private Byte selectedX = null;
    
    /**
     * index Y aktuálního označeného políčka v herním poli
     */
    private Byte selectedY = null;
    
    /**
     * herní pole
     */
    private GameBoard gameBoard;
    
    /**
     * pořadí hráče v místnosti
     */
    private byte playerIndex;
    
    /**
     * Vnitřní Třída BoardPanelInfo slouží jako přepravka pro uchování aktuálních
     * informací o rozměrech potřebných k vykreslení herního pole do plátna (panelu).
     */
    private class BoardPanelInfo {
        
        /**
         * rozměr herního pole v počtu políček
         */
        public final int BOARD_SIZE_IN_CELLS;
        
        /**
         * rozměr herního pole v pixelech
         */
        public final int BOARD_SIZE_IN_PIXELS;
        
        /**
         * rozměr jednoho políčka v pixelech
         */
        public final int CELL_SIZE_IN_PIXELS;
        
        /**
         * Vytvoří novou přepravku s informacemi o rozměrech.
         * 
         * @param boardSizeInCells rozměr herního pole v počtu políček
         * @param boardSizeInPixels rozměr herního pole v pixelech
         * @param cellSizeInPixels rozměr jednoho políčka v pixelech
         */
        public BoardPanelInfo(int boardSizeInCells, int boardSizeInPixels, int cellSizeInPixels) {
            BOARD_SIZE_IN_CELLS = boardSizeInCells;
            BOARD_SIZE_IN_PIXELS = boardSizeInPixels;
            CELL_SIZE_IN_PIXELS = cellSizeInPixels;
        }
    }
    
    /**
     * Vytvoří panel herního pole.
     * 
     * @param messageBackgroundSender vysílač zpráv
     */
    public BoardPanel(MessageBackgroundSender messageBackgroundSender) {
        MESSAGE_SENDER = messageBackgroundSender;
        setListeners();
        
        // TESTOVÁNÍ GRAFIKY
        /*Random r = new Random(0);
        
        byte boardSize = 10;
        byte[][] board = new byte[boardSize][boardSize];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                board[i][j] = (byte) r.nextInt(5);
            }
        }
        
        gameBoard = new GameBoard((byte) 0, (byte) boardSize, 0, true, (byte) 0, (byte) 0,
                (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) (boardSize - 1), (byte) (boardSize - 1), 
                board);*/
    }
    
    /**
     * Nastaví herní pole místnosti, v níž se klient momentálně nachází.
     * 
     * @param gameBoard herní pole
     * @param playerIndex pořadí hráče ve hře
     */
    public void setGameDetail(GameBoard gameBoard, byte playerIndex) {
        this.gameBoard = gameBoard;
        this.playerIndex = playerIndex;
        
        repaint();
    }
    
    /**
     * Nastaví listenery pro tlačítka.
     */
    private void setListeners() {
        MouseAdapter adapter = new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e) {
                playGameMouseClicked(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                highlightCellMouseMoved(e);
            }

        };
        
        addMouseListener(adapter);
        addMouseMotionListener(adapter);
    }
    
    /**
     * Zpracuje stisk políčka v herním poli.
     * 
     * @param e událost stisku myši
     */
    private void playGameMouseClicked(MouseEvent e) {
        if (gameBoard == null) {
            return;
        }
        
        BoardPanelInfo boardPanelInfo = createBoardPanelInfo();
        
        Byte x = selectCellIndex(e.getX(),
                boardPanelInfo.BOARD_SIZE_IN_CELLS, boardPanelInfo.CELL_SIZE_IN_PIXELS);
        
        if (x == null) {
            return;
        }
        
        Byte y = selectCellIndex(e.getY(),
                boardPanelInfo.BOARD_SIZE_IN_CELLS, boardPanelInfo.CELL_SIZE_IN_PIXELS);
        
        if (y == null) {
            return;
        }
        
        MESSAGE_SENDER.enqueueMessageBuilder(new PlayGameRequestBuilder(x, y));
    }

    /**
     * Zvýrazní políčko v herním poli označené kurzorem myši.
     * 
     * @param e událost stisku myši
     */
    private void highlightCellMouseMoved(MouseEvent e) {
        /*if (gameBoard == null) {
            return;
        }*/
        
        BoardPanelInfo boardPanelInfo = createBoardPanelInfo();
        
        selectedX = selectCellIndex(e.getX(),
                boardPanelInfo.BOARD_SIZE_IN_CELLS, boardPanelInfo.CELL_SIZE_IN_PIXELS);
        selectedY = selectCellIndex(e.getY(),
                boardPanelInfo.BOARD_SIZE_IN_CELLS, boardPanelInfo.CELL_SIZE_IN_PIXELS);
        
        repaint();
    }
    
    /**
     * Určí rozměry potřebné k vykreslení herního pole do plána a uloží je do přepravky.
     * 
     * @return přepravka s informacemi o rozměrech
     */
    private BoardPanelInfo createBoardPanelInfo() {
        int width = getWidth();
        int height = getHeight();
        int boardSizeInCells = gameBoard == null ? 3 : gameBoard.getBoardSize();
        int boardSizeInPixels = (width < height ? width : height) - boardSizeInCells;
        int cellSizeInPixels = boardSizeInPixels / boardSizeInCells;
        
        return new BoardPanelInfo(boardSizeInCells, boardSizeInPixels, cellSizeInPixels);
    }
    
    /**
     * Určí index políčka v herním poli, který odpovídá dané souřadnici kurzoru myši.
     * 
     * @param mouseCoordinate souřadnice kurzoru myši
     * @param boardSizeInCells rozměr herního pole v pixelech
     * @param cellSizeInPixels rozměr jednoho políčka v pixelech
     * @return index políčka nebo NULL, pokud není označeno žádné políčko
     */
    private Byte selectCellIndex(int mouseCoordinate, int boardSizeInCells, int cellSizeInPixels) {
        for (int i = 1; i <= boardSizeInCells; i++) {
            if (mouseCoordinate < i * cellSizeInPixels) {
                return (byte) (i - 1);
            }
        }
        
        return null;
    }
    
    /**
     * Překreslí plátno pro zobrazení herního pole.
     * 
     * @param g grafický kontext
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        
        if (getWidth() < 50 || getHeight() < 50) {
            return;
        }
        
        BoardPanelInfo boardPanelInfo = createBoardPanelInfo();
        // vyčistí plátno při překreslení
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // nastaví grafický kontext pro vykreslení symbolů na políčkách
        g2d.translate(5, 5);
        g2d.scale(0.95, 0.95);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        drawBoardGrid(g2d, boardPanelInfo.BOARD_SIZE_IN_CELLS, boardPanelInfo.CELL_SIZE_IN_PIXELS);
        drawSelectedCellHighlight(g2d, boardPanelInfo.CELL_SIZE_IN_PIXELS);
        
        if (gameBoard == null) {
            return;
        }
        
        g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        drawCellSeeds(g2d, gameBoard.getBoard(), boardPanelInfo.CELL_SIZE_IN_PIXELS);
        drawWinnerCellsLine(g2d, gameBoard.getFirstWinnerCellX(),
                gameBoard.getFirstWinnerCellY(), gameBoard.getLastWinnerCellX(),
                gameBoard.getLastWinnerCellY(), boardPanelInfo.CELL_SIZE_IN_PIXELS);
    }
    
    /**
     * Vykreslí mřížku herního pole.
     * 
     * @param g2d grafický kontext
     * @param boardSizeInCells rozměr herního pole v počtu políček
     * @param cellSizeInPixels rozměr jednoho políčka v pixelech
     */
    private void drawBoardGrid(Graphics2D g2d, int boardSizeInCells,
            int cellSizeInPixels) {
        g2d.setColor(Color.WHITE);
        int size = boardSizeInCells * cellSizeInPixels;
        
        g2d.fillRect(0, 0, size, size);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, size, size);
        
        for (int i = 1; i < boardSizeInCells; i++) {
            g2d.drawLine(0, i * cellSizeInPixels,
                    size, i * cellSizeInPixels);
        }
        
        for (int i = 1; i < boardSizeInCells; i++) {
            g2d.drawLine(i * cellSizeInPixels, 0,
                    i * cellSizeInPixels, size);
        }
    }
    
    /**
     * Zvýrazní aktuálně zvolené políčko.
     * 
     * @param g2d grafický kontext
     * @param cellSizeInPixels rozměr jednoho políčka v pixelech
     */
    private void drawSelectedCellHighlight(Graphics2D g2d, int cellSizeInPixels) {
        if (selectedX == null || selectedY == null) {
            return;
        }
        
        int margin = 2;
        int x = selectedX * cellSizeInPixels + margin;
        int y = selectedY * cellSizeInPixels + margin;
        int size = cellSizeInPixels - 2 * margin;
        
        switch (playerIndex) {
            case 1: {
                CrossIcon.highlight(g2d, x, y, size);
                break;
            }
            case 2: {
                CircleIcon.highlight(g2d, x, y, size);
                break;
            }
            case 3: {
                YpsilonIcon.highlight(g2d, x, y, size);
                break;
            }
            case 4: {
                TildeIcon.highlight(g2d, x, y, size);
                break;
            }
            default: {
                // hráč není ve hře
                g2d.setColor(new Color(225, 225, 225));
                g2d.fillRect(x, y, size, size);
                break;
            }
        }
    }
    
    /**
     * Nakreslí příslušné symboly do obsazených políček (symboly jsou určeny
     * podle pořadí hráče v herní místnosti, který dané políčko obsadil).
     * 
     * @param g2d grafický kontext
     * @param cells matice políček herního pole (hodnoty představují pořadí hráčů)
     * @param cellSizeInPixels rozměr jednoho políčka v pixelech
     */
    private void drawCellSeeds(Graphics2D g2d, byte[][] cells, int cellSizeInPixels) {
        final int seedMargin = cellSizeInPixels / 5;
        int seedSize = cellSizeInPixels - 2 * seedMargin;
        AffineTransform originalTransform = g2d.getTransform();
        
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                int x = j * cellSizeInPixels + seedMargin;
                int y = i * cellSizeInPixels + seedMargin;
                
                g2d.translate(x, y);
                
                switch (cells[i][j]) {
                    case 1: {
                        CrossIcon.draw(g2d, seedSize);
                        break;
                    }
                    case 2: {
                        CircleIcon.draw(g2d, seedSize);
                        break;
                    }
                    case 3: {
                        YpsilonIcon.draw(g2d, seedSize);
                        break;
                    }
                    case 4: {
                        TildeIcon.draw(g2d, seedSize);
                        break;
                    }
                    default: {
                        // neobsazené políčko
                    break;
                    }
                }

                g2d.setTransform(originalTransform);
            }
        }
    }
    
    /**
     * Nakreslí čáru mezi hraničními políčky souvislé řady políček o délce,
     * která je potřebná pro vítězství daného hráče, čímž symbolizuje konec hry.
     * 
     * @param g2d grafický kontext
     * @param firstX souřadnice X prvního vítězného políčka
     * @param firstY souřadnice Y prvního vítězného políčka
     * @param lastX souřadnice X posledního vítězného políčka
     * @param lastY souřadnice Y posledního vítězného políčka
     * @param cellSizeInPixels rozměr jednoho políčka v pixelech
     */
    private void drawWinnerCellsLine(Graphics2D g2d, int firstX, int firstY,
            int lastX, int lastY, int cellSizeInPixels) {
        if (firstX == lastX && firstY == lastY) {
            return;
        }
        
        int lineMargin = cellSizeInPixels / 2;
        int x0 = firstX * cellSizeInPixels + lineMargin;
        int y0 = firstY * cellSizeInPixels + lineMargin;
        int x1 = lastX * cellSizeInPixels + lineMargin;
        int y1 = lastY * cellSizeInPixels + lineMargin;
        
        g2d.setColor(Color.BLACK);
        g2d.drawLine(x0, y0, x1, y1);
    }
    
}
