package de.kammerchorwernigerode.app.participate.event.presentation.ui;

import de.kammerchorwernigerode.app.participate.event.presentation.ui.table.EventTablePanel;
import de.kammerchorwernigerode.app.participate.wicket.ParticipatePage;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.cycle.RequestCycle;

import java.time.Instant;
import java.time.ZoneId;

public class EventsPage extends ParticipatePage {

    @Override
    protected void onInitialize() {
        super.onInitialize();

        EventEntrySpecification eventSpecification = new EventEntrySpecification(this::minInstant);
        IModel<EventEntrySpecification> specModel = new CompoundPropertyModel<>(eventSpecification);
        EventTablePanel eventTablePanel = new EventTablePanel("events", specModel);
        eventTablePanel.setOutputMarkupId(true);
        add(eventTablePanel);
    }

    private Instant minInstant() {
        RequestCycle requestCycle = RequestCycle.get();
        long startTime = requestCycle.getStartTime();
        ZoneId zone = ZoneId.systemDefault();
        return Instant.ofEpochMilli(startTime).atZone(zone)
            .withDayOfYear(1)
            .toLocalDate()
            .atStartOfDay(zone)
            .toInstant();
    }

    @Override
    protected IModel<?> titleModel() {
        return new ResourceModel("events");
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(EventsCssResourceReference.asHeaderItem());
    }
}
