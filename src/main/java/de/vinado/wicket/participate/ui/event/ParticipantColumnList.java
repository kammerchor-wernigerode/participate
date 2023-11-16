package de.vinado.wicket.participate.ui.event;

import de.vinado.wicket.participate.model.Participant;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public interface ParticipantColumnList extends List<IColumn<Participant, SerializableFunction<Participant, ?>>>, Serializable {

    static ParticipantColumnList emptyList() {
        return new ParticipantColumnListDecorator(new ArrayList<>()) {};
    }
}
