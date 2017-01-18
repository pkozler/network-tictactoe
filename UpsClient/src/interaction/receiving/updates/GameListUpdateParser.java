package interaction.receiving.updates;

import communication.TcpClient;
import communication.TcpMessage;
import communication.containers.GameInfo;
import communication.tokens.InvalidMessageArgsException;
import communication.tokens.MissingMessageArgsException;
import configuration.Config;
import interaction.receiving.AUpdateParser;
import java.util.ArrayList;
import visualisation.components.GameListPanel;

/**
 * Třída GameListUpdateParser představuje parser notifikace
 * serveru o změně seznamu her.
 * 
 * @author Petr Kozler
 */
public class GameListUpdateParser extends AUpdateParser {

    /**
     * panel seznamu her
     */
    private final GameListPanel GAME_LIST_PANEL;
    
    /**
     * počet prvků
     */
    private final int ITEM_COUNT;
    
    /**
     * seznam her
     */
    private final ArrayList<GameInfo> GAME_LIST;
    
    /**
     * Vytvoří parser seznamu her.
     * 
     * @param client objekt klienta
     * @param gameListPanel panel seznamu her
     * @param message zpráva
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    public GameListUpdateParser(TcpClient client,
            GameListPanel gameListPanel, TcpMessage message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        super(client, message);
        
        GAME_LIST_PANEL = gameListPanel;
        ITEM_COUNT = message.getNextIntArg(0);
        GAME_LIST = new ArrayList<>();
    }

    /**
     * Otestuje, zda seznam her již obsahuje všechny položky.
     * 
     * @return true, pokud seznam obsahuje všechny položky, jinak false
     */
    @Override
    public boolean hasAllItems() {
        return GAME_LIST.size() == ITEM_COUNT;
    }

    /**
     * Zpracuje další položku seznamu her ze seznamu zpráv.
     * 
     * @param itemMessage zpráva
     * @throws InvalidMessageArgsException
     * @throws MissingMessageArgsException 
     */
    @Override
    public void parseNextItemMessage(TcpMessage itemMessage)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        int id = itemMessage.getNextIntArg(1);
        String name = itemMessage.getNextArg();
        byte playerCount = itemMessage.getNextByteArg(Config.MIN_PLAYERS_SIZE, Config.MAX_PLAYERS_SIZE);
        byte boardSize = itemMessage.getNextByteArg(Config.MIN_BOARD_SIZE, Config.MAX_BOARD_SIZE);
        byte cellCount = itemMessage.getNextByteArg(Config.MIN_CELL_COUNT, Config.MAX_CELL_COUNT);
        byte playerCounter = itemMessage.getNextByteArg((byte) 1, playerCount);
        GAME_LIST.add(new GameInfo(id, name, boardSize, playerCount, cellCount, playerCounter));
    }

    /**
     * Aktualizuje objekty pro komunikaci a vrátí výsledek zpracování zprávy.
     * 
     * @return výsledek
     */
    @Override
    public String updateClient() {
        if (!hasAllItems()) {
            return null;
                /*String.format("Probíhá aktualizace seznamu herních místností (zbývá %d položek)",
                    ITEM_COUNT - GAME_LIST.size());*/
        }
        
        setGameInfoToClient();
        
        return "Aktualizace seznamu herních místností byla dokončena";
    }
    
    /**
     * Aktualizuje stav GUI.
     */
    @Override
    public void updateGui() {
        GAME_LIST_PANEL.setGameList(GAME_LIST);
        GAME_LIST_PANEL.setLabel(CLIENT.getGameInfo());
    }
    
    /**
     * Uloží údaje o hře do objektu klienta, pokud je hráč ve hře,
     * nebo údaje odstraní, pokud není.
     */
    private void setGameInfoToClient() {
        for (GameInfo g : GAME_LIST) {
            if (g.ID == CLIENT.getGameId()) {
                CLIENT.joinGame(g);
                
                return;
            }
        }
        
        CLIENT.leaveGame();
    } 
    
}
