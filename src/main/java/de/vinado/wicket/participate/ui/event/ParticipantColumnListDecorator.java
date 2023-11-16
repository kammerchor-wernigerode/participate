package de.vinado.wicket.participate.ui.event;

import de.vinado.wicket.participate.model.Participant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.List;

@RequiredArgsConstructor
public abstract class ParticipantColumnListDecorator implements ParticipantColumnList {

    @Getter
    @Delegate
    private final List<IColumn<Participant, SerializableFunction<Participant, ?>>> delegate;

    protected static <T, R> SerializableFunction<T, R> with(SerializableFunction<T, R> function) {
        return function;
    }
}
