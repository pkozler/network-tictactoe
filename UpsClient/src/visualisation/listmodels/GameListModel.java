package visualisation.listmodels;

import communication.containers.GameInfo;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Třída GameListModel 
 * 
 * @author Petr Kozler
 */
public class GameListModel extends AUniqueItemListModel<GameInfo> {

    /**
     * 
     * 
     * @param element
     * @param key
     * @return 
     */
    @Override
    protected boolean hasKey(GameInfo element, int key) {
        return element.ID == key;
    }

    /**
     * 
     * 
     * @param list 
     */
    @Override
    protected void sortList(ArrayList<GameInfo> list) {
        Collections.sort(list);
    }
    
}
