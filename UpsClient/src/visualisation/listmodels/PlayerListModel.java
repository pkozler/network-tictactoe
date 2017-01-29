package visualisation.listmodels;

import communication.containers.PlayerInfo;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Třída PlayerListModel představuje model seznamu hráčů pro zobrazení v GUI.
 * 
 * @author Petr Kozler
 */
public class PlayerListModel extends AUniqueItemListModel<PlayerInfo> {

    /**
     * Vytvoří model seznamu hráčů.
     * 
     * @param list předaný seznam položek
     * @param currentKey klíč aktuálně zvolené/vlastní položky
     */
    public PlayerListModel(ArrayList<PlayerInfo> list, int currentKey) {
        super(list, currentKey);
    }

    /**
     * Otestuje, zda daný prvek seznamu hráčů obsahuje dané ID.
     * 
     * @param element prvek
     * @param key klíč
     * @return true, pokud prvek obsahuje klíč, jinak false
     */
    @Override
    protected boolean hasKey(PlayerInfo element, int key) {
        return element.ID == key;
    }

    /**
     * Seřadí vnitřní seznam hráčů.
     * 
     * @param list vnitřní seznam
     */
    @Override
    protected void doSorting(ArrayList<PlayerInfo> list) {
        Collections.sort(list);
    }
    
}
