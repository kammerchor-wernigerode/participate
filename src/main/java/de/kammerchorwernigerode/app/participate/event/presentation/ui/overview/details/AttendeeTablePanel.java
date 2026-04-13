package de.kammerchorwernigerode.app.participate.event.presentation.ui.overview.details;

import de.kammerchorwernigerode.app.participate.event.infrastructure.AttendeeRecord.InvitationStatus;
import de.kammerchorwernigerode.app.participate.event.presentation.components.InvitationStatusIcon;
import de.kammerchorwernigerode.app.participate.event.presentation.model.AttendeeEntry;
import de.kammerchorwernigerode.app.participate.event.presentation.model.AttendeeEntryRepository;
import de.kammerchorwernigerode.app.participate.event.presentation.model.AttendeeEntrySpecification;
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
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import java.util.ArrayList;
import java.util.List;

public class AttendeeTablePanel extends GenericPanel<AttendeeEntrySpecification> {

    @SpringBean
    private AttendeeEntryRepository attendeeEntryRepository;

    public AttendeeTablePanel(String id, IModel<AttendeeEntrySpecification> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        IModel<AttendeeEntrySpecification> model = getModel();

        AttendeeDataProvider dataProvider = new AttendeeDataProvider(attendeeEntryRepository, model);
        dataProvider.setSort(new String[]{"invitationStatusOrder"}, SortOrder.ASCENDING);
        List<IColumn<AttendeeEntry, String[]>> columns = createColumns();
        int rowsPerPage = getRowsPerPage();
        AttendeeTable table = new AttendeeTable("table", columns, dataProvider, rowsPerPage);
        table.setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
        add(table);
    }

    // @checkstyle:off: LineLength
    private List<IColumn<AttendeeEntry, String[]>> createColumns() {
        List<IColumn<AttendeeEntry, String[]>> columns = new ArrayList<>();
        columns.add(new InvitationStatusColumn<>(new String[]{"invitationStatusOrder"}));
        columns.add(new LambdaColumn<>(new ResourceModel("person.name"), new String[]{"fileName", "firstName", "lastName"}, this::printName));
        columns.add(new EnumLambdaColumn<>(new ResourceModel("musician.voice"), new String[]{"voiceOrder"}, AttendeeEntry::getVoice));
        return columns;
    }
    // @checkstyle:on: LineLength

    private String printName(AttendeeEntry entry) {
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
        Long rowsPerPage = session.getMetaData(AttendeeTable.attendeeTablePageSize);
        return Math.toIntExact(rowsPerPage);
    }


    private static class InvitationStatusColumn<S> extends AbstractColumn<AttendeeEntry, S> {

        public InvitationStatusColumn(S sortProperty) {
            super(Model.of(), sortProperty);
        }

        @Override
        public void populateItem(Item<ICellPopulator<AttendeeEntry>> cellItem, String componentId,
                                 IModel<AttendeeEntry> rowModel) {
            IModel<InvitationStatus> model = rowModel.map(AttendeeEntry::getInvitationStatus);
            cellItem.add(new InvitationStatusIcon(componentId, model));
        }

        @Override
        public String getCssClass() {
            return "w-1 text-nowrap";
        }
    }
}
