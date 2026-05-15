package de.kammerchorwernigerode.app.participate.event.presentation.ui.overview.details;

import de.kammerchorwernigerode.app.participate.event.presentation.EventPeriodDateTimePrinter;
import de.kammerchorwernigerode.app.participate.event.presentation.EventTitlePrinter;
import de.kammerchorwernigerode.app.participate.event.presentation.model.AttendeeEntrySpecification;
import de.kammerchorwernigerode.app.participate.event.presentation.model.EventEntry;
import de.kammerchorwernigerode.app.participate.event.presentation.ui.details.EventDetailsPage;
import de.kammerchorwernigerode.app.participate.event.presentation.ui.edit.EventEditPage;
import de.kammerchorwernigerode.app.participate.wicket.management.ManagementWicketSession;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.basic.RelativeTimeLabel;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button.BootstrapBookmarkablePageLink;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button.Buttons.Variant;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.icon.Bi;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.extensions.markup.html.basic.SmartLinkMultiLineLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class EventPanel extends GenericPanel<EventEntry> {

    @SpringBean
    private EventPeriodDateTimePrinter eventPeriodDateTimePrinter;

    @SpringBean
    private EventTitlePrinter eventTitlePrinter;

    public EventPanel(String id, IModel<EventEntry> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        IModel<EventEntry> model = getModel();

        Label title = new Label("title", model.map(this::printTitle));
        title.setRenderBodyOnly(true);
        add(title);

        Label date = new Label("date", model.map(this::printDate));
        add(date);

        RelativeTimeLabel createdDate = new RelativeTimeLabel("createdDate", model.map(this::printCreatedDate));
        add(createdDate);

        Label location = new Label("location", model.map(EventEntry::getLocation)) {

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(!Strings.isEmpty(getDefaultModelObjectAsString()));
            }
        };
        add(location);

        SmartLinkMultiLineLabel description = new SmartLinkMultiLineLabel("description",
            model.map(EventEntry::getDescription)) {

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(!Strings.isEmpty(getDefaultModelObjectAsString()));
            }
        };
        add(description);

        AttendeeEntrySpecification attendeeSpecification = new AttendeeEntrySpecification(model.map(EventEntry::getId));
        IModel<AttendeeEntrySpecification> specModel = new CompoundPropertyModel<>(attendeeSpecification);
        AttendeeTablePanel attendeeTablePanel = new AttendeeTablePanel("attendeeTablePanel", specModel);
        add(attendeeTablePanel);

        PageParameters eventParameters = new PageParameters();
        eventParameters.set("id", model.getObject().getId());

        BootstrapBookmarkablePageLink<Void> eventDetailsLink = new BootstrapBookmarkablePageLink<>("eventDetailsLink",
            EventDetailsPage.class, eventParameters);
        eventDetailsLink.setVariant(Variant.PRIMARY);
        eventDetailsLink.setIcon(Bi.box_arrow_up_right);
        eventDetailsLink.setBody(new ResourceModel("EventPanel.event.details"));
        add(eventDetailsLink);

        BootstrapBookmarkablePageLink<Void> editEventLink = new BootstrapBookmarkablePageLink<>("editEventLink",
            EventEditPage.class, eventParameters);
        editEventLink.setVisible(ManagementWicketSession.get().getRoles().hasAnyRole(new Roles("ORGA,ADMIN")));
        editEventLink.setVariant(Variant.NONE);
        editEventLink.setIcon(Bi.pencil_square);
        editEventLink.setBody(new ResourceModel("EventPanel.event.edit"));
        add(editEventLink);
    }

    private Object printCreatedDate(EventEntry eventEntry) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT.localizedBy(getLocale());
        return formatter.format(eventEntry.getCreatedDate());
    }

    private String printDate(EventEntry entry) {
        Locale locale = getLocale();
        return eventPeriodDateTimePrinter.print(entry, locale);
    }

    private String printTitle(EventEntry entry) {
        Locale locale = getLocale();
        return eventTitlePrinter.print(entry, locale);
    }
}
