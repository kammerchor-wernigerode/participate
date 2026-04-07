package de.kammerchorwernigerode.app.participate.event.presentation.ui.overview.table;

import de.kammerchorwernigerode.app.participate.event.presentation.components.AdpPanel;
import de.kammerchorwernigerode.app.participate.event.presentation.model.EventEntry;
import de.kammerchorwernigerode.app.participate.event.presentation.model.EventEntryRepository;
import de.kammerchorwernigerode.app.participate.event.presentation.model.EventEntrySpecification;
import de.kammerchorwernigerode.app.participate.event.presentation.model.EventSelected;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.components.TooltipBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.table.LinkColumn;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
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

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EventTablePanel extends GenericPanel<EventEntrySpecification> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

    @SpringBean
    private EventEntryRepository eventEntryRepository;

    public EventTablePanel(String id, IModel<EventEntrySpecification> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        IModel<EventEntrySpecification> model = getModel();

        EventDataProvider dataProvider = new EventDataProvider(eventEntryRepository, model);
        List<IColumn<EventEntry, String>> columns = createColumns();
        dataProvider.setOrder("startInstant", SortOrder.ASCENDING);
        EventTable table = new EventTable("table", columns, dataProvider, Integer.MAX_VALUE);
        table.setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
        add(table);
    }

    private List<IColumn<EventEntry, String>> createColumns() {
        List<IColumn<EventEntry, String>> columns = new ArrayList<>();
        columns.add(new SummaryColumn(new ResourceModel("event.summary")));
        columns.add(new LambdaColumn<>(new ResourceModel("date"), "startInstant", this::printRange));
        columns.add(new LambdaColumn<>(new ResourceModel("event.location"), EventEntry::getLocation));
        columns.add(new AdpColumn(new ResourceModel("event.adp.abbrev")));
        return columns;
    }

    private String printRange(EventEntry event) {
        Locale locale = getLocale();
        DateTimeFormatter formatter = FORMATTER.localizedBy(locale);
        return formatter.format(event.getStart()) + "–" + formatter.format(event.getEnd());
    }


    private class SummaryColumn extends LinkColumn<EventEntry, String> {

        public SummaryColumn(IModel<String> displayModel) {
            super(displayModel, EventEntry::getSummary);
        }

        @Override
        protected void onClick(AjaxRequestTarget target, IModel<EventEntry> rowModel) {
            EventEntry entry = rowModel.getObject();
            send(getPage(), Broadcast.BREADTH, new EventSelected(entry.getId()));
        }
    }

    private static class AdpColumn extends AbstractColumn<EventEntry, String> {

        public AdpColumn(IModel<String> displayModel) {
            super(displayModel);
        }

        @Override
        public Component getHeader(String componentId) {
            Component header = super.getHeader(componentId);
            header.add(new TooltipBehavior(new ResourceModel("event.adp.full")));
            return header;
        }

        @Override
        public void populateItem(Item<ICellPopulator<EventEntry>> cellItem, String componentId,
                                 IModel<EventEntry> rowModel) {
            cellItem.add(new AdpPanel(componentId, rowModel));
        }

        @Override
        public String getCssClass() {
            return "adp";
        }
    }
}
