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

public abstract class SimpleDataProvider<T, S> extends SortableDataProvider<T, S> {

    protected List<T> data;

    private SortableDataProviderComparator comparator = new SortableDataProviderComparator();

    public SimpleDataProvider(Collection<T> data) {
        set(data);
        setSort(this.getDefaultSort(), SortOrder.ASCENDING);
    }

    public abstract S getDefaultSort();

    protected List<T> filter(List<T> list) {
        return list;
    }

    public void set(Collection<T> data) {
        this.data = new ArrayList<T>(data);
    }

    @Override
    public Iterator<T> iterator(long first, long count) {
        Collections.sort(data, comparator);
        return (filter(data).subList((int) first, (int) (first + count))).iterator();
    }

    @Override
    public long size() {
        return data.size();
    }

    @Override
    public IModel<T> model(T object) {
        return new CompoundPropertyModel<T>(object);
    }

    public List<T> getAll() {
        return data;
    }

    class SortableDataProviderComparator implements Comparator<T>, Serializable {

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
