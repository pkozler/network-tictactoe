package visualisation.listmodels;

import communication.containers.PlayerInfo;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Třída PlayerListModel 
 * 
 * @author Petr Kozler
 */
public class PlayerListModel extends AUniqueItemListModel<PlayerInfo> {

    /**
     * 
     * 
     * @param element
     * @param key
     * @return 
     */
    @Override
    protected boolean hasKey(PlayerInfo element, int key) {
        return element.ID == key;
    }

    /**
     * 
     * 
     * @param list 
     */
    @Override
    protected void sortList(ArrayList<PlayerInfo> list) {
        Collections.sort(list);
    }
    
}
