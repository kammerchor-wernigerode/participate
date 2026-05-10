package de.kammerchorwernigerode.app.participate.event.presentation.ui.overview.details;

import de.kammerchorwernigerode.app.participate.event.infrastructure.AttendeeRecord.InvitationStatus;
import de.kammerchorwernigerode.app.participate.event.presentation.components.InvitationStatusFilter;
import de.kammerchorwernigerode.app.participate.event.presentation.components.InvitationStatusIcon;
import de.kammerchorwernigerode.app.participate.event.presentation.model.AttendeeEntry;
import de.kammerchorwernigerode.app.participate.event.presentation.model.AttendeeEntryRepository;
import de.kammerchorwernigerode.app.participate.event.presentation.model.AttendeeEntrySpecification;
import de.kammerchorwernigerode.app.participate.event.presentation.model.InvitationStatusSelection;
import de.kammerchorwernigerode.app.participate.musician.infrastructure.Voice;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.table.EnumLambdaColumn;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.table.filter.BootstrapMultipleChoiceFilter;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.table.filter.BootstrapTextFilter;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.LambdaColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilteredColumn;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Arrays;
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


        FilterForm<AttendeeEntrySpecification> filterForm = new FilterForm<>("filterForm", dataProvider);
        add(filterForm);

        List<IColumn<AttendeeEntry, String[]>> columns = createColumns();
        int rowsPerPage = getRowsPerPage();
        AttendeeTable table = new AttendeeTable("table", columns, dataProvider, rowsPerPage);
        table.setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
        table.addTopToolbar(new FilterToolbar(table, filterForm));
        filterForm.add(table);
    }

    // @checkstyle:off: LineLength
    private List<IColumn<AttendeeEntry, String[]>> createColumns() {
        List<IColumn<AttendeeEntry, String[]>> columns = new ArrayList<>();
        columns.add(new InvitationStatusColumn<>(new String[]{"invitationStatusOrder"}));
        columns.add(new AttendeeNameColumn<>(new ResourceModel("person.name"), new String[]{"fileName", "firstName", "lastName"}));
        columns.add(new VoiceColumn<>(new ResourceModel("musician.voice"), new String[]{"voiceOrder"}));
        return columns;
    }
    // @checkstyle:on: LineLength

    private int getRowsPerPage() {
        Session session = Session.get();
        Long rowsPerPage = session.getMetaData(AttendeeTable.attendeeTablePageSize);
        return Math.toIntExact(rowsPerPage);
    }


    private static class InvitationStatusColumn<S> extends AbstractColumn<AttendeeEntry, S>
        implements IFilteredColumn<AttendeeEntry, S> {

        public InvitationStatusColumn(S sortProperty) {
            super(Model.of(), sortProperty);
        }

        @Override
        public InvitationStatusFilter getFilter(String componentId, FilterForm<?> form) {
            IModel<AttendeeEntrySpecification> specModel = form.getModel()
                .filter(AttendeeEntrySpecification.class::isInstance)
                .map(AttendeeEntrySpecification.class::cast);
            IModel<InvitationStatusSelection> model = specModel
                .map(AttendeeEntrySpecification::getInvitationStatusSelection);
            return new InvitationStatusFilter(componentId, model, form);
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

    private static class AttendeeNameColumn<S> extends LambdaColumn<AttendeeEntry, S>
        implements IFilteredColumn<AttendeeEntry, S> {

        public AttendeeNameColumn(IModel<String> displayModel, S sortProperty) {
            super(displayModel, sortProperty, AttendeeEntry::getDisplayName);
        }

        @Override
        public BootstrapTextFilter<String> getFilter(String componentId, FilterForm<?> form) {
            IModel<AttendeeEntrySpecification> specModel = form.getModel()
                .filter(AttendeeEntrySpecification.class::isInstance)
                .map(AttendeeEntrySpecification.class::cast);
            IModel<String> model = LambdaModel.of(specModel, AttendeeEntrySpecification::getName,
                AttendeeEntrySpecification::setName);
            return new BootstrapTextFilter<>(componentId, model, form);
        }
    }

    private static class VoiceColumn<S> extends EnumLambdaColumn<AttendeeEntry, Voice, S>
        implements IFilteredColumn<AttendeeEntry, S> {

        public VoiceColumn(IModel<String> displayModel, S sortProperty) {
            super(displayModel, sortProperty, AttendeeEntry::getVoice);
        }

        @Override
        public Component getFilter(String componentId, FilterForm<?> form) {
            IModel<AttendeeEntrySpecification> specModel = form.getModel()
                .filter(AttendeeEntrySpecification.class::isInstance)
                .map(AttendeeEntrySpecification.class::cast);
            IModel<List<Voice>> model = specModel.map(AttendeeEntrySpecification::getVoices);
            List<Voice> choices = Arrays.asList(Voice.values());
            IChoiceRenderer<Voice> renderer = new EnumChoiceRenderer<>(form);
            return new BootstrapMultipleChoiceFilter<>(componentId, model, form, choices, renderer, true);
        }
    }
}
