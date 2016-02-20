package visualization;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import communication.ConnectionManager;
import communication.Game;
import configuration.Config;
import interaction.PlaySendWorker;
import interaction.SelectSendWorker;
import interaction.ListReceiveWorker;

/**
 * Třída představující hlavní okno aplikace.
 */
public class MainWindow extends JFrame {

	/** ID komponenty */
	private static final long serialVersionUID = 1L;
	/** výchozí písmo používané pro popisy */
	private static final Font DEFAULT_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 16);
	/** herní pole */
	private GameFieldPanel gameFieldPanel;
	/** seznam her */
	private JList<Game> gameList;
	/** časovač pro spojení se serverem */
	private Timer connectTimer;
	/** tlačítko pro vytvoření hry */
	private JButton createGameButton;
	/** tlačítko pro připojení do hry */
	private JButton joinGameButton;
	/** tlačítko pro odpojení ze hry */
	private JButton leaveGameButton;
	/** správce spojení */
	private ConnectionManager connectionManager;
	
	/**
	 * Vytvoří nové okno, vytvoří jednotlivé komponenty,
	 * umístí do okna, zobrazí okno a zahájí navazování spojení.
	 */
	public MainWindow(ConnectionManager connectionManager) throws IOException {
		this.connectionManager = connectionManager;

		setTitle("Piškvorky - klient");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(Config.DEFAULT_WINDOW_WIDTH, Config.DEFAULT_WINDOW_HEIGHT);
		setLocationRelativeTo(null);

		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));

		contentPane.add(createStatusField(), BorderLayout.NORTH);
		contentPane.add(createGameListPanel(), BorderLayout.WEST);
		contentPane.add(createMatrixPanel(), BorderLayout.CENTER);

		setContentPane(contentPane);
		setVisible(true);
		
		// připojí se k serveru, jakmile je dostupný
		createConnectingTask();
	}
	
	/**
	 * Vytvoří a spustí časovač pro navazování spojení.
	 */
	private void createConnectingTask() {
		StatusBar.printConnectionStatus("Čekám na spojení se serverem na adrese " 
				+ connectionManager.getAddress() + "...");
		
		connectTimer = new Timer(Config.TIMER_DELAY_MILLIS, new ActionListener() {

			/*
			 * pokus o navázání spojení
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				if (connectionManager.connect()) {
					// spojení úspěšné, stažení seznamu her
					gameFieldPanel.deleteButtons();
					connectTimer.stop();
					(new ListReceiveWorker(connectionManager, gameFieldPanel, gameList, 
							createGameButton, leaveGameButton, connectTimer)).execute();
				}
			}

		});
		
		connectTimer.start();
	}

	/**
	 * Vytvoří herní pole.
	 */
	private JPanel createMatrixPanel() {
		gameFieldPanel = new GameFieldPanel();
		
		return gameFieldPanel;
	}

	/**
	 * Vytvoří novou komponentu pro zobrazení seznamu her.
	 */
	private JScrollPane createGameList() {
		DefaultListModel<Game> gameListModel = new DefaultListModel<>();
		
		gameList = new JList<>();
		gameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		gameList.setFont(DEFAULT_FONT);
		gameList.setModel(gameListModel);
		
		gameList.addListSelectionListener(new ListSelectionListener() {

			/*
			 * obsluha události výběru hry 
			 * (aktivace/deaktivace tlačítek seznamu)
			 */
			@Override
			public void valueChanged(ListSelectionEvent e) {
				Game selectedGame = gameList.getSelectedValue();
				
				if (selectedGame != null) {
					if (selectedGame.isRunning()) {
						joinGameButton.setEnabled(false);
					}
					else {
						joinGameButton.setEnabled(true);
					}
				}
				else {
					joinGameButton.setEnabled(false);
				}
			}
			
		});
		
		JScrollPane scrollPane = new JScrollPane(gameList);
		scrollPane.setBorder(BorderFactory.createEtchedBorder());

		return scrollPane;
	}

	/**
	 * Provede obsluhu události tlačítka
	 * pro vytvoření hry.
	 */
	private void createGameButtonActionPerformed() {
		JLabel playersSizeLabel = new JLabel(
				"Zadejte počet hráčů");
		JSpinner playersSizeInput = new JSpinner(new SpinnerNumberModel(
				Config.MIN_PLAYERS_SIZE, Config.MIN_PLAYERS_SIZE, Config.MAX_PLAYERS_SIZE, 1));
		JLabel matrixSizeLabel = new JLabel(
				"Zadejte velikost hracího pole");
		JSpinner matrixSizeInput = new JSpinner(new SpinnerNumberModel(
				Config.MIN_MATRIX_SIZE, Config.MIN_MATRIX_SIZE, Config.MAX_MATRIX_SIZE, 1));
		JLabel winRowLenLabel = new JLabel(
				"Zadejte požadovanou délku řady políček pro vítězství");
		JSpinner winRowLenInput = new JSpinner(new SpinnerNumberModel(
				Config.MIN_MATRIX_SIZE, Config.MIN_MATRIX_SIZE, Config.MAX_MATRIX_SIZE, 1));
		final JComponent[] inputs = new JComponent[] {
				playersSizeLabel, playersSizeInput, 
				matrixSizeLabel, matrixSizeInput, 
				winRowLenLabel, winRowLenInput};
		
		if (JOptionPane.showConfirmDialog(
				this, inputs, "Nová hra", JOptionPane.OK_CANCEL_OPTION)
				== JOptionPane.OK_OPTION) {
			int playersSize = (int) playersSizeInput.getValue();
			int matrixSize = (int) matrixSizeInput.getValue();
			int winRowLen = (int) winRowLenInput.getValue();
			
			(new SelectSendWorker(connectionManager, gameFieldPanel, gameList, 
					new Game(0, (byte) playersSize, (byte) matrixSize, (byte) winRowLen, 
					(byte) 0, false))).execute();
		}
	}

	/**
	 * Provede obsluhu události tlačítka
	 * pro připojení do hry.
	 */
	private void joinGameButtonActionPerformed() {
		(new SelectSendWorker(connectionManager, gameFieldPanel, gameList, 
				gameList.getSelectedValue())).execute();
	}

	/**
	 * Provede obsluhu události tlačítka
	 * pro odpojení ze hry.
	 */
	private void leaveGameButtonActionPerformed() {
		if (!connectionManager.hasCurrentGame()) {
			return;
		}
		
		if (connectionManager.getCurrentGame().isRunning()) {
			(new PlaySendWorker(connectionManager, gameFieldPanel, (byte) -1, (byte) -1)).execute();
		}
		else {
			(new SelectSendWorker(connectionManager, gameFieldPanel, gameList, null)).execute();
		}
		
		connectionManager.quitGame();
	}

	/**
	 * Vytvoří tlačítka seznamu her.
	 */
	private JPanel createGameListButtons() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		createGameButton = new JButton("Vytvořit");
		joinGameButton = new JButton("Připojit");
		joinGameButton.setEnabled(false);
		leaveGameButton = new JButton("Odpojit");
		leaveGameButton.setEnabled(false);

		createGameButton.addActionListener(new ActionListener() {

			/*
			 * obsluha stisku tlačítka
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				createGameButtonActionPerformed();
			}

		});

		joinGameButton.addActionListener(new ActionListener() {

			/*
			 * obsluha stisku tlačítka
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				joinGameButtonActionPerformed();
			}

		});

		leaveGameButton.addActionListener(new ActionListener() {

			/*
			 * obsluha stisku tlačítka
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				leaveGameButtonActionPerformed();
			}

		});

		buttonPanel.setLayout(new GridLayout(3, 1));
		createGameButton.setFont(DEFAULT_FONT);
		buttonPanel.add(createGameButton);
		joinGameButton.setFont(DEFAULT_FONT);
		buttonPanel.add(joinGameButton);
		leaveGameButton.setFont(DEFAULT_FONT);
		buttonPanel.add(leaveGameButton);
		buttonPanel.setPreferredSize(new Dimension(0, DEFAULT_FONT.getSize() * 6));

		return buttonPanel;
	}

	/**
	 * Vytvoří panel obsahující seznam her
	 * spolu s tlačítky pro jeho obsluhu.
	 */
	private JPanel createGameListPanel() {
		JPanel gameListPanel = new JPanel();
		gameListPanel.setLayout(new BorderLayout());
		gameListPanel.add(createGameListButtons(), BorderLayout.SOUTH);
		gameListPanel.add(createGameList(), BorderLayout.CENTER);
		gameListPanel.setPreferredSize(new Dimension(Config.DEFAULT_WINDOW_WIDTH 
				- Config.DEFAULT_WINDOW_HEIGHT + DEFAULT_FONT.getSize() * 5, 0));
		gameListPanel.setBorder(BorderFactory.createTitledBorder("Dostupné hry"));
		
		return gameListPanel;
	}

	/**
	 * Vytvoří panel pro textová pole sloužící
	 * k výpisu událostí na různých místech programu.
	 */
	private JPanel createStatusField() {
		JLabel connectionLabel = new JLabel();
		JLabel gameLabel = new JLabel();
		JLabel userLabel = new JLabel();
		
		connectionLabel.setFont(DEFAULT_FONT);
		gameLabel.setFont(DEFAULT_FONT);
		userLabel.setFont(DEFAULT_FONT);
		
		JPanel statusPanel = new JPanel();
		statusPanel.setBorder(BorderFactory.createTitledBorder("Stav"));
		statusPanel.setPreferredSize(new Dimension(0, DEFAULT_FONT.getSize() * 5));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
		
		statusPanel.add(connectionLabel);
		statusPanel.add(gameLabel);
		statusPanel.add(userLabel);
		
		StatusBar.setTextFields(connectionLabel, gameLabel, userLabel);
		
		return statusPanel;
	}

}
