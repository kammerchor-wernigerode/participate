package de.kammerchorwernigerode.app.participate.event.presentation.ui.overview.details;

import de.kammerchorwernigerode.app.participate.event.infrastructure.AttendeeRecord.InvitationStatus;
import de.kammerchorwernigerode.app.participate.event.presentation.components.InvitationStatusIcon;
import de.kammerchorwernigerode.app.participate.event.presentation.model.AttendeeEntry;
import de.kammerchorwernigerode.app.participate.event.presentation.model.AttendeeEntryRepository;
import de.kammerchorwernigerode.app.participate.event.presentation.model.AttendeeEntrySpecification;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.table.BootstrapDataTable;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.table.EnumLambdaColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
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
        dataProvider.setOrder("invitationStatusOrder", SortOrder.ASCENDING);
        List<IColumn<AttendeeEntry, String>> columns = createColumns();
        DataTable<AttendeeEntry, String> table = new BootstrapDataTable<>("table", columns, dataProvider,
            Integer.MAX_VALUE);
        table.setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
        add(table);
    }

    private List<IColumn<AttendeeEntry, String>> createColumns() {
        List<IColumn<AttendeeEntry, String>> columns = new ArrayList<>();
        columns.add(new InvitationStatusColumn());
        columns.add(new LambdaColumn<>(new ResourceModel("person.name"), this::printName));
        columns.add(new EnumLambdaColumn<>(new ResourceModel("musician.voice"), "voiceOrder", AttendeeEntry::getVoice));
        return columns;
    }

    private String printName(AttendeeEntry entry) {
        String fileName = entry.getFileName();
        if (!Strings.isEmpty(fileName)) {
            return fileName;
        }

        String lastName = entry.getLastName();
        String firstName = entry.getFirstName();
        return firstName + " " + lastName;
    }


    private static class InvitationStatusColumn extends AbstractColumn<AttendeeEntry, String> {

        public InvitationStatusColumn() {
            super(Model.of(), "invitationStatusOrder");
        }

        @Override
        public void populateItem(Item<ICellPopulator<AttendeeEntry>> cellItem, String componentId,
                                 IModel<AttendeeEntry> rowModel) {
            IModel<InvitationStatus> model = rowModel.map(AttendeeEntry::getInvitationStatus);
            cellItem.add(new InvitationStatusIcon(componentId, model));
        }
    }
}
