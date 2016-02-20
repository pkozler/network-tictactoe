package visualization;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * Třída představující tlačítko, které 
 * slouží jako políčko herního pole.
 */
public class GradientButton extends JButton{
	
	/** ID komponenty */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Výčtový typ pro symboly vykreslené do políček
	 * hracího pole při obsazení hráčem. Jednotlivé symboly
	 * představují různá herní ID. 
	 */
	private static enum PlayerCellSymbol {
		Empty, Cross, Circle, Triangle, Wave, CrossAlt, CircleAlt, TriangleAlt, WaveAlt;
	}
	
	/** asociativní pole tvarů symbolů podle herního ID */
	private static final HashMap<Byte, PlayerCellSymbol> SYMBOLS = new HashMap<>();
	/** asociativní pole barev symbolů podle herního ID */
	private static final HashMap<Byte, Color> FOREGROUND_COLORS = new HashMap<>();
	/** asociativní pole barev pozadí podle herního ID */
	private static final HashMap<Byte, Color> BACKGROUND_GRADIENTS = new HashMap<>();
	
	/*
	 * naplnění jednotlivých asociativních polí
	 */
	static {
		byte b = 0;
		SYMBOLS.put(b, PlayerCellSymbol.Empty);
		FOREGROUND_COLORS.put(b, new Color(0, 0, 0));
		BACKGROUND_GRADIENTS.put(b, new Color(191, 191, 191));
		b++;
		SYMBOLS.put(b, PlayerCellSymbol.Cross);
		FOREGROUND_COLORS.put(b, new Color(191, 0, 0));
		BACKGROUND_GRADIENTS.put(b, new Color(255, 127, 127));
		b++;
		SYMBOLS.put(b, PlayerCellSymbol.Circle);
		FOREGROUND_COLORS.put(b, new Color(0, 0, 191));
		BACKGROUND_GRADIENTS.put(b, new Color(127, 127, 255));
		b++;
		SYMBOLS.put(b, PlayerCellSymbol.Triangle);
		FOREGROUND_COLORS.put(b, new Color(0, 191, 0));
		BACKGROUND_GRADIENTS.put(b, new Color(127, 255, 127));
		b++;
		SYMBOLS.put(b, PlayerCellSymbol.Wave);
		FOREGROUND_COLORS.put(b, new Color(191, 127, 0));
		BACKGROUND_GRADIENTS.put(b, new Color(255, 191, 127));
		b++;
		SYMBOLS.put(b, PlayerCellSymbol.CrossAlt);
		FOREGROUND_COLORS.put(b, new Color(0, 191, 191));
		BACKGROUND_GRADIENTS.put(b, new Color(127, 255, 255));
		b++;
		SYMBOLS.put(b, PlayerCellSymbol.CircleAlt);
		FOREGROUND_COLORS.put(b, new Color(191, 191, 0));
		BACKGROUND_GRADIENTS.put(b, new Color(255, 255, 127));
		b++;
		SYMBOLS.put(b, PlayerCellSymbol.TriangleAlt);
		FOREGROUND_COLORS.put(b, new Color(191, 0, 191));
		BACKGROUND_GRADIENTS.put(b, new Color(255, 127, 255));
		b++;
		SYMBOLS.put(b, PlayerCellSymbol.WaveAlt);
		FOREGROUND_COLORS.put(b, new Color(0, 127, 191));
		BACKGROUND_GRADIENTS.put(b, new Color(127, 191, 255));
	}
	
	/** pozadí políčka */
	private Color gradientColor = BACKGROUND_GRADIENTS.get((byte)0);
	/** barva symbolu v políčku */
	private Color symbolColor = FOREGROUND_COLORS.get((byte)0);
	/** tvar symbolu v políčku */
	private PlayerCellSymbol symbol = SYMBOLS.get((byte)0);
	/** X-ová souřadnice políčka v herním poli */
	private byte coordinateX;
	/** Y-ová souřadnice políčka v herním poli */
	private byte coordinateY;
	/** herní ID hráče, který obsadil políčko (0 při neobsazení) */
	private byte playerPosition;
	
	/**
	 * Vytvoří nové políčko s definovanými souřadnicemi.
	 */
	public GradientButton(byte x, byte y) {
        super();
        setContentAreaFilled(false);
        setFocusPainted(false);
        
        coordinateX = x;
        coordinateY = y;
        playerPosition = 0;
    }
	
	/*
	 * Vykreslí komponentu.
	 */
    @Override
    protected void paintComponent(Graphics g){
    	int width = (int) getSize().getWidth();
    	int height = (int) getSize().getHeight();
    	
    	BufferedImage bufferedImage = new BufferedImage(
    			width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        
        g2d.setPaint(new GradientPaint( new Point(0, 0), Color.WHITE, 
    			new Point(0, height), gradientColor));
        g2d.fillRect(0, 0, width, height);
		
        g2d.setColor(symbolColor);
        g2d.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        g2d.translate(5, 5);
        width -= 10;
        height -= 10;
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
		switch (symbol) {
			case Cross: {
				g2d.drawLine(0, 0, width, height);
				g2d.drawLine(0, height, width, 0);
				break;
			}
			case Circle: {
				g2d.drawOval(0, 0, width, height);
				break;
			}
			case Triangle: {
				g2d.drawLine(0, 0, width / 2, height / 2);
				g2d.drawLine(width, 0, width / 2, height / 2);
				g2d.drawLine(width / 2, height, width / 2, height / 2);
				break;
			}
			case Wave: {
				Path2D.Double path = new Path2D.Double();
				path.moveTo(0, height / 2);
				path.curveTo(width / 2, 0, 
						width / 2, height,
						width, height / 2);
				g2d.draw(path);
				break;
			}
			case CrossAlt: {
				g2d.drawLine(0, height / 2, width, height / 2);
				g2d.drawLine(width / 2, 0, width / 2, height);
				break;
			}
			case CircleAlt: {
				g2d.drawArc(-width / 2, -height / 2, width, height, 270, 90);
				g2d.drawArc(-width / 2, height / 2, width, height, 0, 90);
				g2d.drawArc(width / 2, height / 2, width, height, 90, 90);
				g2d.drawArc(width / 2, - height / 2, width, height, 180, 90);
				break;
			}
			case TriangleAlt: {
				g2d.drawLine(0, height, width / 2, height / 2);
				g2d.drawLine(width, height, width / 2, height / 2);
				g2d.drawLine(width / 2, 0, width / 2, height / 2);
				break;
			}
			case WaveAlt: {
				Path2D.Double path = new Path2D.Double();
				path.moveTo(width / 2, 0);
				path.curveTo(0, height / 2,
						width, height / 2,
						width / 2, height);
				g2d.draw(path);
				break;
			}
			default: {
		        break;
			}
		}
		
		this.setIcon(new ImageIcon(bufferedImage));
		
		g2d.dispose();
        super.paintComponent(g);
    }
    
    /**
     * Obarví pozadí políčka při vítězství daného hráče.
     */
    public void drawBackground() {
		this.gradientColor = BACKGROUND_GRADIENTS.get(playerPosition);
	}

    /**
     * Umístí barevný symbol do políčka při obsazení hráčem.
     */
	public void drawPlayerIcon(byte playerPosition) {
		this.playerPosition = playerPosition;
		this.symbolColor = FOREGROUND_COLORS.get(playerPosition);
		this.symbol = SYMBOLS.get(playerPosition);
	}

	/**
	 * Vrátí X-ovou souřadnici.
	 */
	public byte getCoordinateX() {
		return coordinateX;
	}

	/**
	 * Vrátí Y-ovou souřadnici.
	 */
	public byte getCoordinateY() {
		return coordinateY;
	}
	
	/**
	 * Vrátí herní ID hráče, který obsadil políčko.
	 */
	public byte getPlayerPosition() {
		return playerPosition;
	}

}