package de.vinado.wicket.common;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.cycle.RequestCycle;
import org.danekja.java.util.function.serializable.SerializableConsumer;

import java.util.function.Consumer;

/**
 * @author Vincent Nadoll
 */
public class UpdateOnEventBehavior<T> extends OnEventBehavior<T> {

    private final SerializableConsumer<T> callback;

    public UpdateOnEventBehavior(Class<T> type) {
        this(type, payload -> {});
    }

    public UpdateOnEventBehavior(Class<T> type, SerializableConsumer<T> callback) {
        super(type);
        this.callback = callback;
    }

    @Override
    protected void onEvent(Component component, T payload) {
        if (ready(component)) {
            callback.accept(payload);
            registerUpdate(component);
        }
    }

    protected boolean ready(Component component) {
        return component.isVisibleInHierarchy();
    }

    private static void registerUpdate(Component component) {
        update(target -> target.add(component));
    }

    private static void update(Consumer<AjaxRequestTarget> callback) {
        RequestCycle.get().find(AjaxRequestTarget.class).ifPresent(callback);
    }
}
