package de.vinado.wicket.participate.ui.administration.person;

import de.vinado.wicket.participate.components.tables.BootstrapAjaxDataTable;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.repeater.table.FunctionalDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.List;

public class PersonAdministrationTable extends BootstrapAjaxDataTable<Person, SerializableFunction<Person, ?>> {

    private static final long serialVersionUID = -8173386174805574030L;

    public PersonAdministrationTable(String id,
                                     List<? extends IColumn<Person, SerializableFunction<Person, ?>>> columns,
                                     FunctionalDataProvider<Person> dataProvider) {
        super(id, columns, dataProvider, Integer.MAX_VALUE);

        dataProvider.setSort(Person::getSortName, SortOrder.ASCENDING);
        setOutputMarkupId(true);
        condensed().hover();

        setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
    }
}
