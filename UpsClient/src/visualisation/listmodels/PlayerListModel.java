package visualisation.listmodels;

import communication.containers.PlayerInfo;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Petr Kozler
 */
public class PlayerListModel extends AUniqueItemListModel<PlayerInfo> {

    @Override
    protected boolean hasKey(PlayerInfo element, int key) {
        return element.ID == key;
    }

    @Override
    protected void sortList(ArrayList<PlayerInfo> list) {
        Collections.sort(list);
    }
    
}
