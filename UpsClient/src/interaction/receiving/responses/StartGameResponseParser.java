package interaction.receiving.responses;

import communication.TcpClient;
import communication.TcpMessage;
import communication.tokens.InvalidMessageArgsException;
import communication.tokens.MissingMessageArgsException;
import configuration.Protocol;
import interaction.receiving.AResponseParser;
import visualisation.components.GameListPanel;
import visualisation.components.StatusBarPanel;
/**
 *
 * @author Petr Kozler
 */
public class StartGameResponseParser extends AResponseParser {

    protected int gameId;
    
    public StartGameResponseParser(TcpClient client,
            GameListPanel gameListPanel, StatusBarPanel statusBarPanel, TcpMessage message)
            throws InvalidMessageArgsException, MissingMessageArgsException {
        super(client, gameListPanel, statusBarPanel, message);
        
        if (MESSAGE.getNextBoolArg()) {
            gameId = CLIENT.getGameId();

            return;
        }
        
        messageError = Protocol.MSG_START_GAME.getErrorType(MESSAGE.getNextArg());
    }

    @Override
    public String getStatusAndUpdateGUI() {
        if (messageError == null) {
            return String.format("Hráč zahájil nové kolo hry v místnosti s ID %d", gameId);
        }
        
        if (messageError.equals(Protocol.MSG_ERR_NOT_IN_ROOM)) {
            return "Hráč se nenachází v herní místnosti se zadaným ID";
        }
        
        if (messageError.equals(Protocol.MSG_ERR_ROUND_ALREADY_STARTED)) {
            return "Nové kolo hry bylo již zahájeno";
        }
        
        if (messageError.equals(Protocol.MSG_ERR_NOT_ENOUGH_PLAYERS)) {
            return "V herní místnosti není dostatek hráčů pro zahájení nového kola hry";
        }
        
        return null;
    }

}
