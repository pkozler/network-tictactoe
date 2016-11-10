package interaction.receiving.responses;

import communication.ConnectionManager;
import communication.Message;
import communication.tokens.InvalidMessageArgsException;
import communication.tokens.ClientMessageErrorArg;
import communication.tokens.MissingMessageArgsException;
import configuration.Config;
import interaction.receiving.AResponseParser;
import visualisation.components.GameListPanel;
import visualisation.components.StatusBarPanel;

/**
 *
 * @author Petr Kozler
 */
public class CreateGameResponseParser extends AResponseParser {

    protected int gameId;
    
    public CreateGameResponseParser(ConnectionManager connectionManager,
            GameListPanel gameListPanel, StatusBarPanel statusBarPanel, Message message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        super(connectionManager, gameListPanel, statusBarPanel, message);
        
        if (MESSAGE.getNextBoolArg()) {
            CONNECTION_MANAGER.joinGame(MESSAGE.getNextIntArg(1));
            gameId = CONNECTION_MANAGER.getGameId();
            
            return;
        }
        
        ClientMessageErrorArg error = Config.MSG_CREATE_GAME.getErrorType(MESSAGE.getNextArg());

        if (!(Config.MSG_ERR_INVALID_PLAYER_COUNT.equals(messageError)
                || Config.MSG_ERR_INVALID_BOARD_SIZE.equals(messageError)
                || Config.MSG_ERR_INVALID_CELL_COUNT.equals(messageError))) {
            messageError = error;
            
            return;
        }

        int minValue = MESSAGE.getNextIntArg(1);
        int maxValue = MESSAGE.getNextIntArg(1);
        
        if (minValue > maxValue) {
            throw new InvalidMessageArgsException();
        }

        messageError = error;
    }

    @Override
    public String getStatusAndUpdateGUI() {
        if (messageError == null) {
            
            return String.format("Hráč vytvořil herní místnost ID %d", gameId);
        }
        
        if (messageError.equals(Config.MSG_ERR_INVALID_NAME)) {
            return "Zadaný název herní místnosti je neplatný";
        }
        
        if (messageError.equals(Config.MSG_ERR_EXISTING_NAME)) {
            return "Herní místnost se zadaným názvem již existuje";
        }
        
        if (messageError.equals(Config.MSG_ERR_INVALID_BOARD_SIZE)) {
            return "Zadaný rozměr hracího pole je neplatný";
        }
        
        if (messageError.equals(Config.MSG_ERR_INVALID_PLAYER_COUNT)) {
            return "Zadaný počet hráčů je neplatný";
        }
        
        if (messageError.equals(Config.MSG_ERR_INVALID_CELL_COUNT)) {
            return "Zadaný počet políček potřebných k obsazení je neplatný";
        }
        
        return null;
    }

}
