package visualisation.listmodels;

import communication.containers.JoinedPlayer;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Petr Kozler
 */
public class JoinedPlayerListModel extends AUniqueItemListModel<JoinedPlayer> {

    @Override
    protected boolean hasKey(JoinedPlayer element, int key) {
        return element.getCurrentGameIndex() == key;
    }

    @Override
    protected void sortList(ArrayList<JoinedPlayer> list) {
        Collections.sort(list);
    }
    
}
