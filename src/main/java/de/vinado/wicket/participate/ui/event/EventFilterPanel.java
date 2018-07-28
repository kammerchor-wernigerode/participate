package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import de.vinado.wicket.participate.components.panels.AbstractTableFilterPanel;
import de.vinado.wicket.participate.data.EventDetails;
import de.vinado.wicket.participate.data.filter.EventFilter;
import de.vinado.wicket.participate.service.EventService;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class EventFilterPanel extends AbstractTableFilterPanel<EventDetails, EventFilter> {

    @SpringBean
    @SuppressWarnings("unused")
    private EventService eventService;

    private TextField searchTermTf;

    public EventFilterPanel(final String id, final IModel<List<EventDetails>> model, final IModel<EventFilter> filterIModel) {
        super(id, model, filterIModel);

        final DateTextFieldConfig startDateConfig = new DateTextFieldConfig();
        startDateConfig.withLanguage("de");
        startDateConfig.withFormat("dd.MM.yyyy");
        startDateConfig.autoClose(true);

        final DateTextFieldConfig endDateConfig = new DateTextFieldConfig();
        endDateConfig.withLanguage("de");
        endDateConfig.withFormat("dd.MM.yyyy");
        endDateConfig.autoClose(true);

        searchTermTf = new TextField("searchTerm");
        searchTermTf.setLabel(new ResourceModel("search", "Search"));
        inner.add(searchTermTf);

        final DateTextField endDateTf = new DateTextField("endDate", endDateConfig);
        endDateTf.setLabel(new ResourceModel("to", "To"));

        final DateTextField startDateTf = new DateTextField("startDate", startDateConfig);
        startDateTf.setLabel(new ResourceModel("from", "From"));
        startDateTf.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
                endDateConfig.withStartDate(DateTime.parse(startDateTf.getValue(), DateTimeFormat.forPattern("dd.MM.yyyy")));
                target.add(endDateTf);
            }
        });
        inner.add(startDateTf);

        endDateTf.setOutputMarkupId(true);
        endDateTf.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
            }
        });
        inner.add(endDateTf);

        final CheckBox showAllCb = new CheckBox("showAll");
        inner.add(showAllCb);

        addBootstrapFormDecorator(form);
    }

    @Override
    protected Component getFocusableFormComponent() {
        return searchTermTf;
    }

    @Override
    public List<EventDetails> getFilteredData(final EventFilter filter) {
        return eventService.getFilteredEventList(filter);
    }

    @Override
    public List<EventDetails> getDefaultData() {
        return eventService.getUpcomingDetailedEventListList();
    }
}
