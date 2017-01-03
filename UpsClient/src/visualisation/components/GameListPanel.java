package visualisation.components;

import communication.containers.GameInfo;
import configuration.Config;
import interaction.MessageBackgroundSender;
import interaction.sending.requests.CreateGameRequestBuilder;
import interaction.sending.requests.JoinGameRequestBuilder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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
 *
 * @author Petr Kozler
 */
public class GameListPanel extends JPanel {

    private final JList<GameInfo> GAME_LIST_VIEW;
    private final GameListModel GAME_LIST_MODEL;
    private final JButton CREATE_GAME_BUTTON;
    private final JButton JOIN_GAME_BUTTON;
    private final MessageBackgroundSender MESSAGE_SENDER;
    
    public GameListPanel(MessageBackgroundSender messageBackgroundSender) {
        super(new BorderLayout());
        
        MESSAGE_SENDER = messageBackgroundSender;
        GAME_LIST_MODEL = new GameListModel();
        GAME_LIST_VIEW = new JList<>(GAME_LIST_MODEL);
        GAME_LIST_VIEW.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        
        CREATE_GAME_BUTTON = new JButton("Vytvořit hru");
        JOIN_GAME_BUTTON = new JButton("Vstoupit do hry");
        setButtons(false);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(CREATE_GAME_BUTTON);
        buttonPanel.add(JOIN_GAME_BUTTON);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setListeners();
    }
    
    public JList<GameInfo> getGameList() {
        return GAME_LIST_VIEW;
    }

    public void setGameList(ArrayList<GameInfo> gameList) {
        GAME_LIST_MODEL.setListWithSorting(gameList);
    }
    
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
    
    private void createGameActionPerformed() {
        JTextField nameTF = new JTextField("Nová místnost");
        JSpinner playerCountSpinner = new JSpinner(new SpinnerNumberModel(
                Config.MIN_PLAYERS_SIZE, Config.MIN_PLAYERS_SIZE, Config.MAX_PLAYERS_SIZE, 1));
        JSpinner boardSizeSpinner = new JSpinner(new SpinnerNumberModel(
                Config.MIN_BOARD_SIZE, Config.MIN_BOARD_SIZE, Config.MAX_BOARD_SIZE, 1));
        JSpinner cellCountSpinner = new JSpinner(new SpinnerNumberModel(
                Config.MIN_CELL_COUNT, Config.MIN_CELL_COUNT, Config.MAX_CELL_COUNT, 1));
        
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
        
        int result = JOptionPane.showConfirmDialog(null, inputs, "Vytvoření hry", JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String name = nameTF.getText();
            byte playerCount = (byte) playerCountSpinner.getValue();
            byte boardSize = (byte) boardSizeSpinner.getValue();
            byte cellCount = (byte) cellCountSpinner.getValue();
            
            MESSAGE_SENDER.enqueueMessageBuilder(new CreateGameRequestBuilder(
                name, playerCount, boardSize, cellCount));
        }
    }
    
    private void joinGameActionPerformed() {
        int gameId = GAME_LIST_VIEW.getSelectedValue().ID;
        MESSAGE_SENDER.enqueueMessageBuilder(new JoinGameRequestBuilder(gameId));
    }
    
    public void setButtons(boolean enabled) {
        CREATE_GAME_BUTTON.setEnabled(enabled);
        JOIN_GAME_BUTTON.setEnabled(enabled);
    }

}
