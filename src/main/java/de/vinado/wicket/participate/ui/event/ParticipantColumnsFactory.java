package de.vinado.wicket.participate.ui.event;

import de.vinado.wicket.participate.model.Participant;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.List;

/**
 * @author Vincent Nadoll
 */
@FunctionalInterface
public interface ParticipantColumnsFactory {

    List<IColumn<Participant, SerializableFunction<Participant, ?>>> create();
}
