package visualisation.components;

import communication.TcpClient;
import communication.containers.GameInfo;
import configuration.Config;
import configuration.Protocol;
import interaction.MessageBackgroundSender;
import interaction.sending.requests.CreateGameRequestBuilder;
import interaction.sending.requests.JoinGameRequestBuilder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;
import visualisation.listmodels.GameListModel;

/**
 * Třída GameListPanel představuje panel pro zobrazení seznamu her.
 * 
 * @author Petr Kozler
 */
public class GameListPanel extends JPanel implements Observer {

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
        GAME_LIST_VIEW.setBorder(BorderFactory.createLoweredSoftBevelBorder());
        GAME_LIST_VIEW.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        GAME_LIST_VIEW.setCellRenderer(getRenderer());
        
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        listPanel.add(GAME_LIST_VIEW, BorderLayout.CENTER);
        
        GAME_LABEL = new JLabel("Místnost nevybrána");
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
        setButtons(false, false, false);
    }
    
    private ListCellRenderer<? super GameInfo> getRenderer() {
        return new DefaultListCellRenderer() {
            
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                    Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                JLabel listCellRendererComponent = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,cellHasFocus);
                listCellRendererComponent.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
                return listCellRendererComponent;
            }
            
        };
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
        int number = GAME_LIST_VIEW.getModel() == null ? 1 :
                GAME_LIST_VIEW.getModel().getSize() + 1;
        
        JTextField nameTF = new JTextField("Hra" + number);
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
        if (GAME_LIST_VIEW.getSelectedValue() == null) {
            return;
        }
        
        int id = GAME_LIST_VIEW.getSelectedValue().ID;
        MESSAGE_SENDER.enqueueMessageBuilder(new JoinGameRequestBuilder(id));
    }
    
    /**
     * Nastaví aktivaci tlačítek.
     * 
     * @param connected příznak připojení
     * @param loggedIn příznak přihlášení
     * @param inGame příznak přítomnosti ve hře
     */
    public void setButtons(boolean connected, boolean loggedIn, boolean inGame) {
        CREATE_GAME_BUTTON.setEnabled(connected && loggedIn && !inGame);
        JOIN_GAME_BUTTON.setEnabled(connected && loggedIn && !inGame);
    }

    @Override
    public void update(Observable o, Object o1) {
        TcpClient client = (TcpClient) o;
        ArrayList<GameInfo> gameList = client.getGameList();
        
        if (gameList == null) {
            gameList = new ArrayList<>();
        }
        
        GameListModel gameListModel = new GameListModel();
        gameListModel.setListWithSorting(gameList);
        GAME_LIST_VIEW.setModel(gameListModel);
        
        if (client.isConnected() && client.hasPlayerInfo() && client.hasGameInfo()) {
            GAME_LABEL.setText(String.format(
                "<html>Zvolená místnost:<br />%s (ID %d)</html>",
                client.getGameInfo().NAME, client.getGameInfo().ID));
        }
        else {
            GAME_LABEL.setText("Místnost nevybrána");
        }
        
        setButtons(client.isConnected(), client.hasPlayerInfo(), client.hasGameInfo());
    }

}
