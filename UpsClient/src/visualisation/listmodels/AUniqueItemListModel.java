package visualisation.listmodels;

import java.util.ArrayList;
import javax.swing.AbstractListModel;

/**
 *
 * @author Petr Kozler
 */
public abstract class AUniqueItemListModel<E> extends AbstractListModel<E> {
    
    protected ArrayList<E> list;

    public AUniqueItemListModel() {
        list = new ArrayList<>();
    }
    
    @Override
    public int getSize() {
        return list.size();
    }

    @Override
    public E getElementAt(int index) {
        return (E) list.get(index);
    }
    
    public E getElementByKey(int key) {
        for (E element : list) {
            if (hasKey(element, key)) {
                return element;
            }
        }
        
        return null;
    }
    
    public final void setListWithSorting(ArrayList<E> list) {
        sortList(list);
        this.list = list;
    }
    
    protected abstract boolean hasKey(E element, int key);
    
    protected abstract void sortList(ArrayList<E> list);
    
}
