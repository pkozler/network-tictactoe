package visualisation.listmodels;

import communication.containers.GameInfo;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Petr Kozler
 */
public class GameListModel extends AUniqueItemListModel<GameInfo> {

    @Override
    protected boolean hasKey(GameInfo element, int key) {
        return element.ID == key;
    }

    @Override
    protected void sortList(ArrayList<GameInfo> list) {
        Collections.sort(list);
    }
    
}
