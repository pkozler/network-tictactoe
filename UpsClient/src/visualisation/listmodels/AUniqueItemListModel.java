package visualisation.listmodels;

import java.util.ArrayList;
import javax.swing.AbstractListModel;

/**
 * Abstraktní třída AUniqueItemListModel představuje obecný model seznamu
 * položek (které je možné přijmout prostřednictvím zpráv ze serveru)
 * pro zobrazení v GUI.
 * 
 * @author Petr Kozler
 * @param <E> položka přijatého seznamu
 */
public abstract class AUniqueItemListModel<E> extends AbstractListModel<E> {
    
    /**
     * vnitřní seznam
     */
    protected ArrayList<E> list;
    
    /**
     * aktuálně zvolená/vlastní položka
     */
    protected E current;

    /**
     * Vytvoří model seznamu a nastaví vnitřní seznam (který seřadí)
     * spolu s odkazem na aktuální položku určenou podle zadaného klíče.
     * 
     * @param list předaný seznam položek
     * @param currentKey klíč aktuálně zvolené/vlastní položky
     */
    public AUniqueItemListModel(ArrayList<E> list, int currentKey) {
        sortList(list);
        this.list = list != null ? list : new ArrayList<E>();
        current = getElementByKey(currentKey);
    }

    /**
     * Vrátí aktuálně zvolenou/vlastní položku.
     * 
     * @return aktuálně zvolená/vlastní položka
     */
    public final E getCurrent() {
        return current;
    }
    
    /**
     * Vrátí počet prvků seznamu.
     * 
     * @return počet prvků seznamu
     */
    @Override
    public final int getSize() {
        return list.size();
    }

    /**
     * Vrátí prvek seznamu na daném indexu.
     * 
     * @param index index prvku
     * @return prvek seznamu
     */
    @Override
    public final E getElementAt(int index) {
        return (E) list.get(index);
    }
    
    /**
     * Vrátí prvek seznamu podle klíče.
     * 
     * @param key klíč prvku
     * @return prvek seznamu
     */
    public final E getElementByKey(int key) {
        if (key < 1) {
            return null;
        }
        
        for (E element : list) {
            if (hasKey(element, key)) {
                return element;
            }
        }
        
        return null;
    }
    
    /**
     * Spustí řazení předaného seznamu, pokud není prázdný.
     * 
     * @param list předaný seznam
     */
    private void sortList(ArrayList<E> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        
        doSorting(list);
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
    protected abstract void doSorting(ArrayList<E> list);
    
}
