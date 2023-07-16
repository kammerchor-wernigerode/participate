package de.vinado.app.participate.event.ui;

import de.vinado.wicket.participate.model.Accommodation;
import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableBiConsumer;
import org.danekja.java.util.function.serializable.SerializableFunction;

public abstract class AccommodationModel implements IModel<Accommodation> {

    public static <T> AccommodationModel compatible(IModel<T> target, SerializableFunction<T, Boolean> getter,
                                                    SerializableBiConsumer<T, Boolean> setter) {
        return new AccommodationModel() {

            @Override
            public Accommodation getObject() {
                T targetObject = target.getObject();
                if (null == targetObject) {
                    return null;
                }

                return getObject(targetObject);
            }

            private Accommodation getObject(T target) {
                return getter.apply(target)
                    ? new Accommodation(Accommodation.Status.SEARCHING, 1)
                    : Accommodation.noNeed();
            }

            @Override
            public void setObject(Accommodation object) {
                T targetObject = target.getObject();
                if (null != targetObject) {
                    setObject(object, targetObject);
                }
            }

            private void setObject(Accommodation accommodation, T target) {
                Accommodation.Status status = accommodation.getStatus();
                setter.accept(target, Accommodation.Status.SEARCHING.equals(status));
            }

            @Override
            public void detach() {
                target.detach();
                super.detach();
            }
        };
    }
}
