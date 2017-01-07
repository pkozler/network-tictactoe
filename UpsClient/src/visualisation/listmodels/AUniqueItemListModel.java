package visualisation.listmodels;

import java.util.ArrayList;
import javax.swing.AbstractListModel;

/**
 * Abstraktní třída AUniqueItemListModel 
 * 
 * @author Petr Kozler
 */
public abstract class AUniqueItemListModel<E> extends AbstractListModel<E> {
    
    /**
     * 
     */
    protected ArrayList<E> list;

    /**
     * 
     * 
     */
    public AUniqueItemListModel() {
        list = new ArrayList<>();
    }
    
    /**
     * 
     * 
     * @return 
     */
    @Override
    public int getSize() {
        return list.size();
    }

    /**
     * 
     * 
     * @param index
     * @return 
     */
    @Override
    public E getElementAt(int index) {
        return (E) list.get(index);
    }
    
    /**
     * 
     * 
     * @param key
     * @return 
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
     * 
     * 
     * @param list 
     */
    public final void setListWithSorting(ArrayList<E> list) {
        sortList(list);
        this.list = list;
    }
    
    /**
     * 
     * 
     * @param element
     * @param key
     * @return 
     */
    protected abstract boolean hasKey(E element, int key);
    
    /**
     * 
     * 
     * @param list 
     */
    protected abstract void sortList(ArrayList<E> list);
    
}
