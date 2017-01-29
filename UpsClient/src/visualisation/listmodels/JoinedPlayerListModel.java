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
     * Vytvoří model seznamu hráčů ve hře.
     * 
     * @param list předaný seznam položek
     * @param currentKey klíč aktuálně zvolené/vlastní položky
     */
    public JoinedPlayerListModel(ArrayList<JoinedPlayer> list, int currentKey) {
        super(list, currentKey);
    }

    /**
     * Otestuje, zda daný prvek seznamu hráčů v místnosti obsahuje daný klíč.
     * 
     * @param element prvek
     * @param key klíč
     * @return true, pokud prvek obsahuje klíč, jinak false
     */
    @Override
    protected boolean hasKey(JoinedPlayer element, int key) {
        return element.getId() == key;
    }

    /**
     * Seřadí vnitřní seznam hráčů v herní místnosti.
     * 
     * @param list vnitřní seznam
     */
    @Override
    protected void doSorting(ArrayList<JoinedPlayer> list) {
        Collections.sort(list);
    }
    
    /**
     * Vyhledá hráče podle pořadí ve hře.
     * 
     * @param index pořadí ve hře
     * @return hráč
     */
    public JoinedPlayer getByGameIndex(byte index) {
        if (index < 1) {
            return null;
        }
        
        for (JoinedPlayer player : list) {
            if (player.getCurrentGameIndex() == index) {
                return player;
            }
        }
        
        return null;
    }
    
}
