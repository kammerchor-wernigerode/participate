package de.kammerchorwernigerode.app.participate.person.presentation.ui.overview;

import de.kammerchorwernigerode.app.participate.person.presentation.components.EmailLinkLabel;
import de.kammerchorwernigerode.app.participate.person.presentation.model.PersonEntry;
import de.kammerchorwernigerode.app.participate.person.presentation.model.PersonEntryRepository;
import de.kammerchorwernigerode.app.participate.person.presentation.model.PersonEntrySpecification;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.table.EnumLambdaColumn;
import org.apache.wicket.Session;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.LambdaColumn;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import java.util.ArrayList;
import java.util.List;

import static de.kammerchorwernigerode.app.participate.person.presentation.ui.overview.PersonDataProvider.NAME_SORT_PROPERTY;

public class PersonTablePanel extends GenericPanel<PersonEntrySpecification> {

    @SpringBean
    private PersonEntryRepository personEntryRepository;

    public PersonTablePanel(String id, IModel<PersonEntrySpecification> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        IModel<PersonEntrySpecification> model = getModel();

        List<IColumn<PersonEntry, String[]>> columns = createColumns();
        PersonDataProvider dataProvider = new PersonDataProvider(personEntryRepository, model);
        dataProvider.setSort(NAME_SORT_PROPERTY, SortOrder.ASCENDING);
        int rowsPerPage = getRowsPerPage();
        PersonTable table = new PersonTable("table", columns, dataProvider, rowsPerPage);
        table.setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
        add(table);
    }

    // @checkstyle:off: LineLength
    private List<IColumn<PersonEntry, String[]>> createColumns() {
        List<IColumn<PersonEntry, String[]>> columns = new ArrayList<>();
        columns.add(new LambdaColumn<>(new ResourceModel("person.name"), NAME_SORT_PROPERTY, this::printName));
        columns.add(new EnumLambdaColumn<>(new ResourceModel("musician.voice"), new String[]{"voiceOrder"}, PersonEntry::getVoice));
        columns.add(new EmailAddressColumn<>(new ResourceModel("person.emailAddress")));
        return columns;
    }
    // @checkstyle:on: LineLength

    private String printName(PersonEntry entry) {
        String fileName = entry.getFileName();
        if (!Strings.isEmpty(fileName)) {
            return fileName;
        }

        String lastName = entry.getLastName();
        String firstName = entry.getFirstName();
        return firstName + " " + lastName;
    }

    private int getRowsPerPage() {
        Session session = Session.get();
        Long rowsPerPage = session.getMetaData(PersonTable.personTablePageSize);
        return Math.toIntExact(rowsPerPage);
    }


    private static class EmailAddressColumn<S> extends AbstractColumn<PersonEntry, S> {

        public EmailAddressColumn(IModel<String> displayModel) {
            super(displayModel);
        }

        @Override
        public void populateItem(Item<ICellPopulator<PersonEntry>> cellItem, String componentId,
                                 IModel<PersonEntry> rowModel) {
            cellItem.add(new EmailLinkLabel(componentId, rowModel));
        }
    }
}
