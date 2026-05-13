package de.kammerchorwernigerode.app.participate.event.presentation.components.details.overview;

import de.kammerchorwernigerode.app.participate.event.presentation.components.attendee.PeriodLabel;
import de.kammerchorwernigerode.app.participate.event.presentation.model.AttendeeEntry;
import de.kammerchorwernigerode.app.participate.event.presentation.model.AttendeeEntrySpecification;
import de.kammerchorwernigerode.app.participate.event.presentation.model.EventDates;
import de.kammerchorwernigerode.app.participate.event.presentation.ui.overview.details.AttendeeTablePanel;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.ContentSpan;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.basic.CollapsibleTextPanel.Limit;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.components.TooltipBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.table.CollapsibleColumn;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import java.util.List;

public class DetailedAttendeeTablePanel extends AttendeeTablePanel {

    private final IModel<? extends EventDates> event;

    public DetailedAttendeeTablePanel(String id, IModel<AttendeeEntrySpecification> model,
                                      IModel<? extends EventDates> event) {
        super(id, model);
        this.event = event;
    }

    // @checkstyle:off: LineLength
    protected List<IColumn<AttendeeEntry, String[]>> createColumns() {
        List<IColumn<AttendeeEntry, String[]>> columns = super.createColumns();
        columns.add(new AttributesColumn<>(Model.of()));
        columns.add(new PeriodColumn<>(new ResourceModel("attendee.presence"), event));
        columns.add(new CommentColumn<>(new ResourceModel("attendee.comment")));
        return columns;
    }
    // @checkstyle:on: LineLength

    @Override
    protected int getRowsPerPage() {
        return Integer.MAX_VALUE;
    }


    private static class AttributesColumn<S> extends AbstractColumn<AttendeeEntry, S> {

        public AttributesColumn(IModel<String> displayModel) {
            super(displayModel);
        }

        @Override
        public void populateItem(Item<ICellPopulator<AttendeeEntry>> cellItem, String componentId,
                                 IModel<AttendeeEntry> rowModel) {
            AttributesPanel panel = new AttributesPanel(componentId, rowModel);
            panel.add(ClassAttributeModifier.append("class", "d-inline-flex gap-1"));
            cellItem.add(panel);
        }
    }

    private static class PeriodColumn<S> extends AbstractColumn<AttendeeEntry, S> {

        private final IModel<? extends EventDates> event;

        public PeriodColumn(IModel<String> displayModel, IModel<? extends EventDates> event) {
            super(displayModel);
            this.event = event;
        }

        @Override
        public void populateItem(Item<ICellPopulator<AttendeeEntry>> cellItem, String componentId,
                                 IModel<AttendeeEntry> rowModel) {
            ContentSpan span = new ContentSpan(componentId);
            cellItem.add(span);

            PeriodLabel periodLabel = new PeriodLabel(span.getContentId(), rowModel, event);
            periodLabel.add(new TooltipBehavior(periodLabel.printDates()));
            span.add(periodLabel);
        }
    }

    private static class CommentColumn<S> extends CollapsibleColumn<AttendeeEntry, S> {

        public static final Limit LIMIT = new Limit(50);

        public CommentColumn(IModel<String> displayModel) {
            super(displayModel, LIMIT, AttendeeEntry::getComment);
        }
    }
}
