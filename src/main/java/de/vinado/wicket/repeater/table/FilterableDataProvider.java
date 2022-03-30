package de.vinado.wicket.repeater.table;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializablePredicate;

/**
 * @author Vincent Nadoll
 */
@RequiredArgsConstructor
public abstract class FilterableDataProvider<T> extends FunctionalDataProvider<T> {

    private static final long serialVersionUID = 168347101843221031L;

    @Getter
    private final IModel<? extends SerializablePredicate<? super T>> filter;

    @Override
    protected SerializablePredicate<? super T> filter() {
        return filter.getObject();
    }

    @Override
    public void detach() {
        filter.detach();
        super.detach();
    }
}
