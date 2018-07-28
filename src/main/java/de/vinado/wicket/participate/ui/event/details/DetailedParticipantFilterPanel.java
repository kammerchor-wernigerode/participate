package de.vinado.wicket.participate.ui.event.details;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import de.vinado.wicket.participate.ParticipateSession;
import de.vinado.wicket.participate.components.panels.AbstractTableFilterPanel;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Voice;
import de.vinado.wicket.participate.model.filters.DetailedParticipantFilter;
import de.vinado.wicket.participate.services.EventService;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class DetailedParticipantFilterPanel extends AbstractTableFilterPanel<Participant, DetailedParticipantFilter> {

    @SuppressWarnings("unused")
    @SpringBean
    private EventService eventService;

    private final Event event = ParticipateSession.get().getEvent();

    private TextField searchTermTf;

    public DetailedParticipantFilterPanel(final String id, final IModel<List<Participant>> model,
                                          final IModel<DetailedParticipantFilter> filterModel, final boolean editable) {
        super(id, model, filterModel);

        final DateTextFieldConfig fromDateConfig = new DateTextFieldConfig();
        fromDateConfig.withLanguage("de");
        fromDateConfig.withFormat("dd.MM.yyyy");
        fromDateConfig.withStartDate(new DateTime(event.getStartDate()));
        fromDateConfig.withEndDate(new DateTime(event.getEndDate()));
        fromDateConfig.autoClose(true);

        final DateTextFieldConfig toDateConfig = new DateTextFieldConfig();
        toDateConfig.withLanguage("de");
        toDateConfig.withFormat("dd.MM.yyyy");
        toDateConfig.withStartDate(new DateTime(event.getStartDate()));
        toDateConfig.withEndDate(new DateTime(event.getEndDate()));
        toDateConfig.autoClose(true);

        searchTermTf = new TextField("name");
        searchTermTf.setLabel(new ResourceModel("filter.names", "Filter by Name"));
        inner.add(searchTermTf);

        final TextField commentTf = new TextField("comment");
        commentTf.setLabel(new ResourceModel("filter.comments", "Filter by comments"));
        inner.add(commentTf);

        final DropDownChoice<InvitationStatus> invitationStatusDdc = new DropDownChoice<>("invitationStatus",
            Collections.unmodifiableList(Arrays.asList(InvitationStatus.values())), new EnumChoiceRenderer<>());
        invitationStatusDdc.setLabel(new ResourceModel("invitationStatus", "Invitation Status"));
        inner.add(invitationStatusDdc);

        final DropDownChoice<Voice> voiceDdc = new DropDownChoice<>("voice",
            Collections.unmodifiableList(Arrays.asList(Voice.values())), new EnumChoiceRenderer<>());
        voiceDdc.setLabel(new ResourceModel("voice", "Voice"));
        inner.add(voiceDdc);

        final DateTextField endDateTf = new DateTextField("toDate", toDateConfig);
        endDateTf.setLabel(new ResourceModel("to", "To"));

        final DateTextField startDateTf = new DateTextField("fromDate", fromDateConfig);
        startDateTf.setLabel(new ResourceModel("from", "From"));
        startDateTf.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
                toDateConfig.withStartDate(DateTime.parse(startDateTf.getValue(), DateTimeFormat.forPattern("dd.MM.yyyy")));
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

        final CheckBox accommodationCb = new CheckBox("accommodation");
        inner.add(accommodationCb);

        final CheckBox cateringCb = new CheckBox("catering");
        inner.add(cateringCb);

        addBootstrapFormDecorator(form);
    }

    @Override
    public List<Participant> getFilteredData(final DetailedParticipantFilter filter) {
        return eventService.getDetailedFilteredParticipants(event, filter);
    }

    @Override
    public List<Participant> getDefaultData() {
        return eventService.getParticipants(ParticipateSession.get().getEvent());
    }

    @Override
    protected Component getFocusableFormComponent() {
        return searchTermTf;
    }
}
