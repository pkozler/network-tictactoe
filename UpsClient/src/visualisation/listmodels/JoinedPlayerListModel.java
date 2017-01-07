package visualisation.listmodels;

import communication.containers.JoinedPlayer;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Třída JoinedPlayerListModel 
 * 
 * @author Petr Kozler
 */
public class JoinedPlayerListModel extends AUniqueItemListModel<JoinedPlayer> {

    /**
     * 
     * 
     * @param element
     * @param key
     * @return 
     */
    @Override
    protected boolean hasKey(JoinedPlayer element, int key) {
        return element.getCurrentGameIndex() == key;
    }

    /**
     * 
     * 
     * @param list 
     */
    @Override
    protected void sortList(ArrayList<JoinedPlayer> list) {
        Collections.sort(list);
    }
    
}
