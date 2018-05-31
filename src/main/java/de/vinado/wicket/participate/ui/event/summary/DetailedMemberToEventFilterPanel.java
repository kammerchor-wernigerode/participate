package de.vinado.wicket.participate.ui.event.summary;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import de.vinado.wicket.participate.ParticipateSession;
import de.vinado.wicket.participate.component.panel.AbstractTableFilterPanel;
import de.vinado.wicket.participate.data.Configurable;
import de.vinado.wicket.participate.data.Event;
import de.vinado.wicket.participate.data.InvitationStatus;
import de.vinado.wicket.participate.data.MemberToEvent;
import de.vinado.wicket.participate.data.Voice;
import de.vinado.wicket.participate.data.filter.DetailedMemberToEventFilter;
import de.vinado.wicket.participate.service.EventService;
import de.vinado.wicket.participate.service.ListOfValueService;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
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
public abstract class DetailedMemberToEventFilterPanel extends AbstractTableFilterPanel<MemberToEvent, DetailedMemberToEventFilter> {

    @SuppressWarnings("unused")
    @SpringBean
    private EventService eventService;

    @SuppressWarnings("unused")
    @SpringBean
    private ListOfValueService listOfValueService;

    private final Event event = ParticipateSession.get().getEvent();

    private TextField searchTermTf;

    public DetailedMemberToEventFilterPanel(final String id, final IModel<List<MemberToEvent>> model,
                                            final IModel<DetailedMemberToEventFilter> filterModel, final boolean editable) {
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
        searchTermTf.setLabel(new ResourceModel("searchNames", "Search"));
        inner.add(searchTermTf);

        final TextField commentTf = new TextField("comment");
        commentTf.setLabel(new ResourceModel("searchComment", "Search for Comments"));
        inner.add(commentTf);

        final DropDownChoice<Configurable> invitationStatusDdc = new DropDownChoice<>("invitationStatus",
                listOfValueService.getConfigurableList(InvitationStatus.class), new ChoiceRenderer<>("name"));
        invitationStatusDdc.setLabel(new ResourceModel("invitationStatus", "Invitation status"));
        inner.add(invitationStatusDdc);

        final DropDownChoice<Configurable> voiceDdc = new DropDownChoice<>("voice",
                listOfValueService.getConfigurableList(Voice.class), new ChoiceRenderer<>("name"));
        voiceDdc.setLabel(new ResourceModel("voiceGroup", "Voice"));
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

        final CheckBox needsPlaceToSleepCb = new CheckBox("needsPlaceToSleep");
        inner.add(needsPlaceToSleepCb);

        final CheckBox needsDinnerCb = new CheckBox("needsDinner");
        inner.add(needsDinnerCb);

        final CheckBox notInvitedCb = new CheckBox("notInvited");
        notInvitedCb.setVisible(editable);
        inner.add(notInvitedCb);

        addBootstrapFormDecorator(form);
    }

    @Override
    public List<MemberToEvent> getFilteredData(final DetailedMemberToEventFilter filter) {
        return eventService.getDetailedFilteredMemberToEventList(event, filter);
    }

    @Override
    public List<MemberToEvent> getDefaultData() {
        return eventService.getMemberToEventList(ParticipateSession.get().getEvent());
    }

    @Override
    protected Component getFocusableFormComponent() {
        return searchTermTf;
    }
}
