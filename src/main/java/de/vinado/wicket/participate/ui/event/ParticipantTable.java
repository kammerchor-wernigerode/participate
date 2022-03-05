package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.vinado.wicket.participate.components.PersonContext;
import de.vinado.wicket.participate.components.tables.BootstrapAjaxDataTable;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.ui.event.details.ParticipantDataProvider;
import lombok.RequiredArgsConstructor;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.Collections;

/**
 * @author Vincent Nadoll
 */
public class ParticipantTable extends BootstrapAjaxDataTable<Participant, SerializableFunction<Participant, ?>> {

    private final PersonContext personContext;

    private ParticipantTable(String id,
                             ParticipantColumnsFactory columnsFactory,
                             ParticipantDataProvider dataProvider,
                             int rowsPerPage,
                             PersonContext personContext) {
        super(id, columnsFactory.create(), dataProvider, rowsPerPage);
        this.personContext = personContext;

        dataProvider.setSort(with(Participant::getInvitationStatus).andThen(Enum::ordinal), SortOrder.ASCENDING);
        setOutputMarkupId(true);
        condensed().hover();

        setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
    }

    private static <T, R> SerializableFunction<T, R> with(SerializableFunction<T, R> function) {
        return function;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new CssClassNameAppender("participants"));
    }

    @Override
    protected Item<Participant> newRowItem(String id, int index, IModel<Participant> model) {
        Item<Participant> row = super.newRowItem(id, index, model);
        Person person = model.getObject().getSinger();
        if (person.equals(personContext.get())) row.add(new CssClassNameAppender("bg-info"));
        return row;
    }

    public static Builder builder(String id, ParticipantDataProvider dataProvider) {
        return new Builder(id, dataProvider);
    }

    @RequiredArgsConstructor
    public static final class Builder {

        private final String id;
        private final ParticipantDataProvider dataProvider;

        private ParticipantColumnsFactory columnsFactory = Collections::emptyList;
        private int rowsPerPage = Integer.MAX_VALUE;
        private PersonContext personContext = () -> null;

        public Builder columnsFactory(ParticipantColumnsFactory columnsFactory) {
            this.columnsFactory = columnsFactory;
            return this;
        }

        public Builder rowsPerPage(int rowsPerPage) {
            this.rowsPerPage = rowsPerPage;
            return this;
        }

        public Builder personContext(PersonContext personContext) {
            this.personContext = personContext;
            return this;
        }

        public ParticipantTable build() {
            return new ParticipantTable(id, columnsFactory, dataProvider, rowsPerPage, personContext);
        }
    }
}
