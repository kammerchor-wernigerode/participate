package de.vinado.wicket.participate.behavoirs;

import lombok.RequiredArgsConstructor;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.event.IEvent;

import java.util.Optional;

/**
 * @author Vincent Nadoll
 */
@RequiredArgsConstructor
public abstract class OnEventBehavior<T> extends Behavior {

    private final Class<T> type;

    @Override
    public void bind(Component component) {
        component.setOutputMarkupId(true);
    }

    @Override
    public void onEvent(Component component, IEvent<?> event) {
        getPayload(event, type).ifPresent(payload -> onEvent(component, payload));
    }

    public static <T> Optional<T> getPayload(IEvent<?> event, Class<T> type) {
        return Optional.ofNullable(event.getPayload())
            .filter(type::isInstance)
            .map(type::cast);
    }

    protected abstract void onEvent(Component component, T payload);
}
