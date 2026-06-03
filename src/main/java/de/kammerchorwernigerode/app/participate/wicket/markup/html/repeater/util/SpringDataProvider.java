package de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.util;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.springframework.data.util.Streamable;

import java.io.Serializable;
import java.util.Iterator;

public abstract class SpringDataProvider<T extends Serializable> implements IDataProvider<T> {

    @Override
    public Iterator<? extends T> iterator(long first, long count) {
        Streamable<T> streamable = streamable(first, count);
        return streamable.iterator();
    }

    public abstract Streamable<T> streamable(long first, long count);

    @Override
    public IModel<T> model(T object) {
        return Model.of(object);
    }
}
