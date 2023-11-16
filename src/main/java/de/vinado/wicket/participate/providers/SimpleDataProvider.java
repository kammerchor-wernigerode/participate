package de.vinado.wicket.participate.providers;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class SimpleDataProvider<T, S> extends SortableDataProvider<T, S> {

    /**
     * {@link Collection} of data
     */
    protected List<T> data;

    /**
     * Comparator
     */
    private SortableDataProviderComparator comparator = new SortableDataProviderComparator();

    /**
     * Construct
     */
    public SimpleDataProvider() {
        this(new ArrayList<T>());
    }

    /**
     * Construct.
     *
     * @param data {@link Collection} of data to sort.
     */
    public SimpleDataProvider(Collection<T> data) {
        set(data);
        setSort(this.getDefaultSort(), SortOrder.ASCENDING);
    }

    /**
     * Construct.
     * @param data {@link Collection} of data to sort
     * @param asc Set <code>false</code> if you want to sort in the opposite direction.
     */
    public SimpleDataProvider(Collection<T> data, boolean asc) {
        set(data);
        setSort(this.getDefaultSort(), asc ? SortOrder.ASCENDING : SortOrder.DESCENDING);
    }

    /**
     * Sets the default sort attribute
     * @return {@link S}
     */
    public abstract S getDefaultSort();

    protected List<T> filter(List<T> list) {
        return list;
    }

    /**
     * Sets the {@link Collection} of data
     * @param data Collection of data
     */
    public void set(Collection<T> data) {
        this.data = new ArrayList<T>(data);
    }

    /**
     * {@inheritDoc}
     * @param first First index of sublist
     * @param count Count of sublist items, starting with first
     * @return {@link Iterator} of sorted sublist
     */
    @Override
    public Iterator<T> iterator(long first, long count) {
        Collections.sort(data, comparator);
        return (filter(data).subList((int) first, (int) (first + count))).iterator();
    }

    /**
     * {@inheritDoc}
     * @return Size of the {@link Collection}
     */
    @Override
    public long size() {
        return data.size();
    }

    /**
     * {@inheritDoc}
     * @param object T-Object
     * @return Model of given object
     */
    @Override
    public IModel<T> model(T object) {
        return new CompoundPropertyModel<T>(object);
    }

    /**
     * Returns the {@link List} with all data
     * @return List
     */
    public List<T> getAll() {
        return data;
    }

    /**
     * Selfmade comparator
     */
    class SortableDataProviderComparator implements Comparator<T>, Serializable {

        /**
         * Compares two objects of the same type.
         * @param o1 {@link T}
         * @param o2 {@link T}
         * @return Result
         */
        @SuppressWarnings("rawtypes,unchecked")
        public int compare(T o1, T o2) {
            PropertyModel<Comparable> model1 = new PropertyModel<Comparable>(o1, getSort().getProperty().toString());
            PropertyModel<Comparable> model2 = new PropertyModel<Comparable>(o2, getSort().getProperty().toString());

            int result = 0;

            if ((null != model1.getObject()) && (null == model2.getObject())) {
                result = 1;
            } else if ((null == model1.getObject()) && (null != model2.getObject())) {
                result = -1;
            } else if ((null != model1.getObject()) && (null != model2.getObject())) {
                result = model1.getObject().compareTo(model2.getObject());
            }

            if (!getSort().isAscending()) {
                result = -result;
            }

            return result;
        }
    }
}
