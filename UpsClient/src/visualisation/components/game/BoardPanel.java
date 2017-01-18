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
import java.awt.geom.Path2D;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

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
     * herní pole
     */
    private GameBoard gameBoard;
    
    /**
     * index X aktuálního označeného políčka v herním poli
     */
    private Byte selectedX = null;
    
    /**
     * index Y aktuálního označeného políčka v herním poli
     */
    private Byte selectedY = null;
    
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
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        MESSAGE_SENDER = messageBackgroundSender;
        setListeners();
    }
    
    /**
     * Nastaví herní pole.
     * 
     * @param gameBoard herní pole
     */
    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
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
        int boardSizeInCells = gameBoard == null ? 3 : gameBoard.GAME_INFO.BOARD_SIZE;
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
        
        drawBoardGrid(g2d, boardPanelInfo.BOARD_SIZE_IN_CELLS,
                boardPanelInfo.BOARD_SIZE_IN_PIXELS, boardPanelInfo.CELL_SIZE_IN_PIXELS);
        
        drawSelectedCellHighlight(g2d, boardPanelInfo.CELL_SIZE_IN_PIXELS);
        
        if (gameBoard == null) {
            return;
        }
        
        // nastaví grafický kontext pro vykreslení symbolů na políčkách
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
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
     * @param boardSizeInPixels rozměr herního pole v pixelech
     * @param cellSizeInPixels rozměr jednoho políčka v pixelech
     */
    private void drawBoardGrid(Graphics2D g2d, int boardSizeInCells,
            int boardSizeInPixels, int cellSizeInPixels) {
        g2d.setColor(getBackground());
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
        
        int x = selectedX * cellSizeInPixels + 1;
        int y = selectedY * cellSizeInPixels + 1;
        int size = cellSizeInPixels - 1;
        
        g2d.setColor(Color.GRAY);
        g2d.fillRect(x, y, size, size);
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
        final int seedMargin = 5;
        int seedSize = cellSizeInPixels - 2* seedMargin;
        AffineTransform originalTransform = g2d.getTransform();
        
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                int x = j * cellSizeInPixels - seedMargin;
                int y = i * cellSizeInPixels - seedMargin;
                
                g2d.translate(x, y);
                
                switch (cells[i][j]) {
                    case 1: {
                        drawCross(g2d, seedSize);
                        break;
                    }
                    case 2: {
                        drawCircle(g2d, seedSize);
                        break;
                    }
                    case 3: {
                        drawYpsilon(g2d, seedSize);
                        break;
                    }
                    case 4: {
                        drawTilde(g2d, seedSize);
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
     * Nakreslí křížek.
     * 
     * @param g2d grafický kontext
     * @param seedSize velikost v pixelech
     */
    private void drawCross(Graphics2D g2d, int seedSize) {
        g2d.setColor(Color.RED);
        g2d.drawLine(0, 0, seedSize, seedSize);
        g2d.drawLine(0, seedSize, seedSize, 0);
    }
    
    /**
     * Nakreslí kolečko.
     * 
     * @param g2d grafický kontext
     * @param seedSize velikost v pixelech
     */
    private void drawCircle(Graphics2D g2d, int seedSize) {
        g2d.setColor(Color.BLUE);
        g2d.drawOval(0, 0, seedSize, seedSize);
    }
    
    /**
     * Nakreslí ypsilon.
     * 
     * @param g2d grafický kontext
     * @param seedSize velikost v pixelech
     */
    private void drawYpsilon(Graphics2D g2d, int seedSize) {
        g2d.setColor(Color.GREEN);
        g2d.drawLine(0, 0, seedSize / 2, seedSize / 2);
        g2d.drawLine(seedSize, 0, seedSize / 2, seedSize / 2);
        g2d.drawLine(seedSize / 2, seedSize, seedSize / 2, seedSize / 2);
    }
    
    /**
     * Nakreslí vlnovku.
     * 
     * @param g2d grafický kontext
     * @param seedSize velikost v pixelech
     */
    private void drawTilde(Graphics2D g2d, int seedSize) {
        g2d.setColor(Color.ORANGE);
        Path2D.Double path = new Path2D.Double();
        path.moveTo(0, seedSize / 2);
        path.curveTo(seedSize / 2, 0, 
                        seedSize / 2, seedSize,
                        seedSize, seedSize / 2);
        g2d.draw(path);
    }
    
    /**
     * Nakreslí čáru mezi hraničními políčky souvislé řady políček o délce,
     * která je potřebná pro vítězství daného hráče, čímž symbolizuje konec hry.
     * 
     * @param g2d grafický kontext
     * @param firstI souřadnice X prvního vítězného políčka
     * @param firstJ souřadnice Y prvního vítězného políčka
     * @param lastI souřadnice X posledního vítězného políčka
     * @param lastJ souřadnice Y posledního vítězného políčka
     * @param cellSizeInPixels rozměr jednoho políčka v pixelech
     */
    private void drawWinnerCellsLine(Graphics2D g2d, int firstI, int firstJ,
            int lastI, int lastJ, int cellSizeInPixels) {
        if (firstI == lastI && firstJ == lastJ) {
            return;
        }
        
        int lineMargin = cellSizeInPixels / 2;
        int firstX = firstJ * cellSizeInPixels + lineMargin;
        int firstY = firstI * cellSizeInPixels + lineMargin;
        int lastX = lastJ * cellSizeInPixels + lineMargin;
        int lastY = lastI * cellSizeInPixels + lineMargin;
        
        g2d.setColor(Color.BLACK);
        g2d.drawLine(firstX, firstY, lastX, lastY);
    }
    
}
