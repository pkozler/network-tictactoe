package visualisation.components;

import communication.containers.GameInfo;
import configuration.Config;
import configuration.Protocol;
import interaction.MessageBackgroundSender;
import interaction.sending.requests.CreateGameRequestBuilder;
import interaction.sending.requests.JoinGameRequestBuilder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import visualisation.listmodels.GameListModel;

/**
 * Třída GameListPanel představuje panel pro zobrazení seznamu her.
 * 
 * @author Petr Kozler
 */
public class GameListPanel extends JPanel {

    /**
     * seznam her
     */
    private final JList<GameInfo> GAME_LIST_VIEW;
    
    /**
     * popisek zvolené herní místnosti
     */
    private final JLabel GAME_LABEL;
    
    /**
     * tlačítko vytvoření hry
     */
    private final JButton CREATE_GAME_BUTTON;
    
    /**
     * tlačítko připojení ke hře
     */
    private final JButton JOIN_GAME_BUTTON;
    
    /**
     * vysílač zpráv
     */
    private final MessageBackgroundSender MESSAGE_SENDER;
    
    /**
     * Vytvoří panel seznamu her.
     * 
     * @param messageBackgroundSender vysílač zpráv
     */
    public GameListPanel(MessageBackgroundSender messageBackgroundSender) {
        super(new BorderLayout());
        setPreferredSize(new Dimension(240, 0));
        setBorder(BorderFactory.createTitledBorder("Herní místnosti"));
        
        MESSAGE_SENDER = messageBackgroundSender;
        GAME_LIST_VIEW = new JList<>();
        GAME_LIST_VIEW.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        GAME_LIST_VIEW.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        listPanel.add(GAME_LIST_VIEW, BorderLayout.CENTER);
        
        GAME_LABEL = new JLabel("Místnost nezvolena");
        CREATE_GAME_BUTTON = new JButton("Vytvořit");
        JOIN_GAME_BUTTON = new JButton("Vstoupit");
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(CREATE_GAME_BUTTON);
        buttonPanel.add(JOIN_GAME_BUTTON);
        
        JPanel labelPanel = new JPanel(new FlowLayout());
        labelPanel.add(GAME_LABEL);
        
        add(labelPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
        add(listPanel, BorderLayout.CENTER);
        
        setListeners();
        setButtons(false);
    }
    
    /**
     * Vrátí seznam her.
     * 
     * @return seznam her
     */
    public JList<GameInfo> getGameList() {
        return GAME_LIST_VIEW;
    }

    /**
     * Nastaví seznam her.
     * 
     * @param gameList seznam her
     */
    public void setGameList(ArrayList<GameInfo> gameList) {
        GameListModel gameListModel = new GameListModel();
        gameListModel.setListWithSorting(gameList);
        GAME_LIST_VIEW.setModel(gameListModel);
    }
    
    /**
     * Vypíše aktuálně zvolenou herní místnost.
     * 
     * @param gameInfo struktura stavu hry
     */
    public void setLabel(GameInfo gameInfo) {
        GAME_LABEL.setText(gameInfo != null ? String.format(
                "<html>Zvolená místnost:<br />%s (ID %d)</html>",
                gameInfo.NAME, gameInfo.ID) : "Místnost nezvolena");
    }
    
    /**
     * Nastaví listenery pro stisk tlačítek.
     */
    private void setListeners() {
        CREATE_GAME_BUTTON.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                createGameActionPerformed();
            }
            
        });
        
        JOIN_GAME_BUTTON.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                joinGameActionPerformed();
            }
            
        });
    }
    
    /**
     * Zpracuje stisk tlačítka pro vytvoření hry.
     */
    private void createGameActionPerformed() {
        if (!MESSAGE_SENDER.CLIENT.isConnected() || !MESSAGE_SENDER.CLIENT.isLogged()) {
            return;
        }
        
        JTextField nameTF = new JTextField("Hra");
        JSpinner playerCountSpinner = new JSpinner(new SpinnerNumberModel(
                2, Config.MIN_PLAYERS_SIZE, Config.MAX_PLAYERS_SIZE, 1));
        JSpinner boardSizeSpinner = new JSpinner(new SpinnerNumberModel(
                10, Config.MIN_BOARD_SIZE, Config.MAX_BOARD_SIZE, 1));
        JSpinner cellCountSpinner = new JSpinner(new SpinnerNumberModel(
                5, Config.MIN_CELL_COUNT, Config.MAX_CELL_COUNT, 1));
        
        final JComponent[] inputs = new JComponent[] {
                new JLabel("Název herní místnosti:"),
                nameTF,
                new JLabel("Maximální počet hráčů:"),
                playerCountSpinner,
                new JLabel("Rozměr hracího pole:"),
                boardSizeSpinner,
                new JLabel("Na kolik políček se hraje:"),
                cellCountSpinner
        };
        
        int result = JOptionPane.showConfirmDialog(null, inputs, "Vytvoření hry", JOptionPane.CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String name = nameTF.getText();
            
            if (name == null || name.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Musí být zadán název hry.",
                        "Chybějící vstup", JOptionPane.ERROR_MESSAGE);
                
                return;
            }

            if (name.contains(Protocol.SEPARATOR)) {
                JOptionPane.showMessageDialog(null, "Název hry nesmí obsahovat znak \""
                        + Protocol.SEPARATOR + "\".", "Neplatný vstup", JOptionPane.ERROR_MESSAGE);

                return;
            }
            
            byte playerCount = ((Integer) playerCountSpinner.getValue()).byteValue();
            byte boardSize = ((Integer) boardSizeSpinner.getValue()).byteValue();
            byte cellCount = ((Integer) cellCountSpinner.getValue()).byteValue();
            
            MESSAGE_SENDER.enqueueMessageBuilder(new CreateGameRequestBuilder(
                name, playerCount, boardSize, cellCount));
        }
    }
    
    /**
     * Zpracuje stisk tlačítka pro připojení ke hře.
     */
    private void joinGameActionPerformed() {
        if (!MESSAGE_SENDER.CLIENT.isConnected() || !MESSAGE_SENDER.CLIENT.isLogged()) {
            return;
        }
        
        int id = GAME_LIST_VIEW.getSelectedValue().ID;
        MESSAGE_SENDER.CLIENT.setGameId(id);
        MESSAGE_SENDER.enqueueMessageBuilder(new JoinGameRequestBuilder(id));
    }
    
    /**
     * Nastaví aktivaci tlačítek.
     * 
     * @param connected příznak aktivace
     */
    public void setButtons(boolean connected) {
        CREATE_GAME_BUTTON.setEnabled(connected);
        JOIN_GAME_BUTTON.setEnabled(connected);
    }

}
