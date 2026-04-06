package de.kammerchorwernigerode.app.participate.event.presentation.ui.table;

import de.kammerchorwernigerode.app.participate.event.presentation.ui.AdpPanel;
import de.kammerchorwernigerode.app.participate.event.presentation.ui.EventEntry;
import de.kammerchorwernigerode.app.participate.event.presentation.ui.EventEntryRepository;
import de.kammerchorwernigerode.app.participate.event.presentation.ui.EventEntrySpecification;
import de.kammerchorwernigerode.app.participate.wicket.bootstrap.tooltip.TooltipBehavior;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
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
        DataTable<EventEntry, String> table = new AjaxFallbackDefaultDataTable<>("table", columns, dataProvider,
            Integer.MAX_VALUE);
        table.setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
        add(table);
    }

    private List<IColumn<EventEntry, String>> createColumns() {
        List<IColumn<EventEntry, String>> columns = new ArrayList<>();
        columns.add(new LambdaColumn<>(new ResourceModel("event.summary"), EventEntry::getSummary));
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
