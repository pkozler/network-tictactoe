package visualisation.listmodels;

import communication.containers.JoinedPlayer;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Třída JoinedPlayerListModel představuje model seznamu hráčů v herní místnosti
 * pro zobrazení v GUI.
 * 
 * @author Petr Kozler
 */
public class JoinedPlayerListModel extends AUniqueItemListModel<JoinedPlayer> {

    /**
     * Otestuje, zda daný prvek seznamu hráčů v místnosti obsahuje daný klíč.
     * 
     * @param element prvek
     * @param key klíč
     * @return true, pokud prvek obsahuje klíč, jinak false
     */
    @Override
    protected boolean hasKey(JoinedPlayer element, int key) {
        return element.getCurrentGameIndex() == key;
    }

    /**
     * Seřadí vnitřní seznam hráčů v herní místnosti.
     * 
     * @param list vnitřní seznam
     */
    @Override
    protected void sortList(ArrayList<JoinedPlayer> list) {
        Collections.sort(list);
    }
    
}
