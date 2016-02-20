package visualization;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * Třída umožňující přístup k textovým polím okna
 * pro výpisy z různých částí programu.
 */
public class StatusBar {

	/** stav spojení */
	private static JLabel connectionLabel;
	/** poslední událost ve hře */
	private static JLabel gameLabel;
	/** pokyn uživateli */
	private static JLabel userLabel;
	
	/**
	 * Nastaví textová pole okna.
	 */
	public static void setTextFields(JLabel connectionLabel, 
			JLabel gameLabel, JLabel userLabel) {
		StatusBar.connectionLabel = connectionLabel;
		StatusBar.gameLabel = gameLabel;
		StatusBar.userLabel = userLabel;
	}
	
	/**
	 * Vypíše stav spojení do textového pole.
	 */
	public static void printConnectionStatus(final String t) {
		//System.out.println(t);
		
		SwingUtilities.invokeLater(new Runnable() {
			
			/*
			 * provedení výpisu
			 */
			@Override
			public void run() {
				connectionLabel.setText(t);
			}
			
		});
	}
	
	/**
	 * Vypíše stav hry do textového pole.
	 */
	public static void printGameStatus(final String t) {
		//System.out.println(t);
		
		SwingUtilities.invokeLater(new Runnable() {
			
			/*
			 * provedení výpisu
			 */
			@Override
			public void run() {
				gameLabel.setText(t);
			}
			
		});
	}
	
	/**
	 * Vypíše pokyn do textového pole.
	 */
	public static void printUserInstruction(final String t) {
		//System.out.println(t);
		
		SwingUtilities.invokeLater(new Runnable() {
			
			/*
			 * provedení výpisu
			 */
			@Override
			public void run() {
				userLabel.setText(t);
			}
			
		});
	}
	
}
