package visualization;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import communication.ConnectionManager;
import communication.Game;
import interaction.PlaySendWorker;

/**
 * Třída představující panel s tlačítky, který
 * slouží jako herní pole.
 */
public class GameFieldPanel extends JPanel {

	/** ID komponenty */
	private static final long serialVersionUID = 1L;
	/** matice tlačítek představujících políčka */
	private GradientButton[][] buttons;
	/** správce spojení */
	private ConnectionManager connectionManager;

	/**
	 * Vytvoří herní pole pro zobrazení v hlavním okně.
	 */
	public GameFieldPanel() {
		setBorder(BorderFactory.createTitledBorder("Hrací pole"));
	}
	
	/**
	 * Provede obsluhu tlačítka.
	 */
	private void buttonActionPerformed(ActionEvent e) {
		(new PlaySendWorker(connectionManager, this,
				((GradientButton)e.getSource()).getCoordinateX(),
				((GradientButton)e.getSource()).getCoordinateY())).execute();
	}
	
	/**
	 * Vyčistí hrací pole.
	 */
	public void deleteButtons() {
		removeAll();
		revalidate();
		repaint();
	}
	
	/**
	 * Vytvoří políčka v herním poli při vstupu hráče do hry.
	 */
	public void createButtons(ConnectionManager connectionManager) {
		deleteButtons();
		
		this.connectionManager = connectionManager;
		Game currentGame = connectionManager.getCurrentGame();
		
		byte matrixSize = currentGame.MATRIX_SIZE;
		setLayout(new GridLayout(matrixSize, matrixSize, 0, 0));
		
		buttons = new GradientButton[matrixSize][matrixSize];
		
		for (byte i = 0; i < (byte) buttons.length; i++) {
			for (byte j = 0; j < (byte) buttons.length; j++) {
				GradientButton button = new GradientButton(j, i);
				
				button.addActionListener(new ActionListener() {
					
					/*
					 * obsluha stisku tlačítka
					 */
					@Override
					public void actionPerformed(ActionEvent e) {
						buttonActionPerformed(e);
					}
					
				});
				
				buttons[i][j] = button;
				buttons[i][j].drawPlayerIcon((byte) 0);
				
				add(button);
			}
		}
		
		revalidate();
		repaint();
	}
	
	/**
	 * Aktualizuje stav herního políčka po příjmu nového stavu hry.
	 */
	public void updateButtons() {
		Game currentGame = connectionManager.getCurrentGame();
		
		if (currentGame.getLastMovePlayer() == 0) {
			createButtons(connectionManager);
		}
		
		byte x = currentGame.getLastMoveX();
		byte y = currentGame.getLastMoveY();
		
		if (x < 0 || y < 0) {
			return;
		}
		
		if (buttons[y][x].getPlayerPosition() != 0) {
			return;
		}
		
		buttons[y][x].drawPlayerIcon(currentGame.getLastMovePlayer());
		
		if (currentGame.getWinner() == 0) {
			return;
		}
		
		byte[] winRow = currentGame.getWinRow();
		byte length = (byte) (currentGame.WIN_ROW_LEN * 2);
		
		for (byte k = 0; k < length; k += 2) {
			byte i = winRow[k];
			byte j = winRow[k + 1];
			buttons[i][j].drawBackground();
		}
		
		revalidate();
		repaint();
	}
	
}
