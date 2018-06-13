package de.vinado.wicket.participate.ui.event.event;

import de.vinado.wicket.participate.component.panel.AbstractTableFilterPanel;
import de.vinado.wicket.participate.data.Event;
import de.vinado.wicket.participate.data.InvitationStatus;
import de.vinado.wicket.participate.data.MemberToEvent;
import de.vinado.wicket.participate.data.Voice;
import de.vinado.wicket.participate.data.filter.MemberToEventFilter;
import de.vinado.wicket.participate.service.EventService;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class MemberToEventFilterPanel extends AbstractTableFilterPanel<MemberToEvent, MemberToEventFilter> {

    @SuppressWarnings("unused")
    @SpringBean
    private EventService eventService;

    private IModel<Event> event;

    private TextField searchTermTf;

    public MemberToEventFilterPanel(final String id, final IModel<List<MemberToEvent>> model,
                                    final IModel<MemberToEventFilter> filterModel, final IModel<Event> event,
                                    final boolean editable) {
        super(id, model, filterModel);

        this.event = event;

        searchTermTf = new TextField("searchTerm");
        searchTermTf.setLabel(new ResourceModel("search", "Search"));
        inner.add(searchTermTf);

        final DropDownChoice<InvitationStatus> invitationStatusDdc = new DropDownChoice<>("invitationStatus",
            Collections.unmodifiableList(Arrays.asList(InvitationStatus.values())), new EnumChoiceRenderer<>());
        invitationStatusDdc.setLabel(new ResourceModel("invitationStatus", "Invitation Status"));
        inner.add(invitationStatusDdc);

        final DropDownChoice<Voice> voiceDdc = new DropDownChoice<>("voice",
            Collections.unmodifiableList(Arrays.asList(Voice.values())), new EnumChoiceRenderer<>());
        voiceDdc.setLabel(new ResourceModel("voice", "Voice"));
        inner.add(voiceDdc);

        final CheckBox showAllCb = new CheckBox("notInvited");
        showAllCb.setVisible(editable);
        inner.add(showAllCb);

        addBootstrapFormDecorator(form);
    }

    @Override
    protected Component getFocusableFormComponent() {
        return searchTermTf;
    }

    @Override
    public List<MemberToEvent> getFilteredData(final MemberToEventFilter filter) {
        return eventService.getFilteredEventToMemberList(event.getObject(), filter);
    }

    @Override
    public List<MemberToEvent> getDefaultData() {
        return eventService.getMemberToEventList(event.getObject());
    }
}
