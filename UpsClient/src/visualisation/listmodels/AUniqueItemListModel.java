package visualisation.listmodels;

import java.util.ArrayList;
import javax.swing.AbstractListModel;

/**
 * Abstraktní třída AUniqueItemListModel představuje obecný model seznamu
 * položek (které je možné přijmout prostřednictvím zpráv ze serveru)
 * pro zobrazení v GUI.
 * 
 * @author Petr Kozler
 */
public abstract class AUniqueItemListModel<E> extends AbstractListModel<E> {
    
    /**
     * vnitřní seznam
     */
    protected ArrayList<E> list;

    /**
     * Vytvoří model seznamu.
     */
    public AUniqueItemListModel() {
        list = new ArrayList<>();
    }
    
    /**
     * Vrátí počet prvků seznamu.
     * 
     * @return počet prvků seznamu
     */
    @Override
    public int getSize() {
        return list.size();
    }

    /**
     * Vrátí prvek seznamu na daném indexu.
     * 
     * @param index index prvku
     * @return prvek seznamu
     */
    @Override
    public E getElementAt(int index) {
        return (E) list.get(index);
    }
    
    /**
     * Vrátí prvek seznamu podle klíče.
     * 
     * @param key klíč prvku
     * @return prvek seznamu
     */
    public E getElementByKey(int key) {
        for (E element : list) {
            if (hasKey(element, key)) {
                return element;
            }
        }
        
        return null;
    }
    
    /**
     * Nastaví vnitřní seznam.
     * 
     * @param list vnitřní seznam
     */
    public final void setListWithSorting(ArrayList<E> list) {
        sortList(list);
        this.list = list;
    }
    
    /**
     * Otestuje, zda daný prvek seznamu obsahuje daný klíč.
     * 
     * @param element prvek seznamu
     * @param key klíč prvku
     * @return true, pokud prvek obsahuje klíč, jinak false
     */
    protected abstract boolean hasKey(E element, int key);
    
    /**
     * Seřadí vnitřní seznam.
     * 
     * @param list vnitřní seznam
     */
    protected abstract void sortList(ArrayList<E> list);
    
}
