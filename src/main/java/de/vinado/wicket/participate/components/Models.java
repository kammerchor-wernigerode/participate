package de.vinado.wicket.participate.components;

import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableFunction;

/**
 * @author Vincent Nadoll
 */
public final class Models {

    public static <T, R> IModel<R> map(IModel<T> source, SerializableFunction<? super T, R> mapper) {
        return new IModel<R>() {
            @Override
            public R getObject() {
                T object = source.getObject();
                if (object == null) {
                    return null;
                } else {
                    return mapper.apply(object);
                }
            }

            @Override
            public void setObject(R object) {
                throw new UnsupportedOperationException(
                    "Override this method to support setObject(Object)");
            }

            @Override
            public void detach() {
                source.detach();
            }
        };
    }
}
