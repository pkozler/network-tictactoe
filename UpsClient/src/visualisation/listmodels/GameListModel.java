package visualisation.listmodels;

import communication.containers.GameInfo;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Třída GameListModel představuje model seznamu her pro zobrazení v GUI.
 * 
 * @author Petr Kozler
 */
public class GameListModel extends AUniqueItemListModel<GameInfo> {

    /**
     * Vytvoří model seznamu her.
     * 
     * @param list předaný seznam položek
     * @param currentKey klíč aktuálně zvolené/vlastní položky
     */
    public GameListModel(ArrayList<GameInfo> list, int currentKey) {
        super(list, currentKey);
    }

    /**
     * Otestuje, zda daný prvek seznamu her obsahuje dané ID.
     * 
     * @param element prvek
     * @param key klíč
     * @return true, pokud prvek obsahuje klíč, jinak false
     */
    @Override
    protected boolean hasKey(GameInfo element, int key) {
        return element.ID == key;
    }

    /**
     * Seřadí vnitřní seznam her.
     * 
     * @param list vnitřní seznam
     */
    @Override
    protected void doSorting(ArrayList<GameInfo> list) {
        Collections.sort(list);
    }
    
}
