package de.vinado.wicket.participate.ui.event;

import de.vinado.wicket.participate.components.panels.AbstractTableFilterPanel;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Voice;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import de.vinado.wicket.participate.services.EventService;
import org.apache.wicket.Component;
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
public abstract class ParticipantFilterPanel extends AbstractTableFilterPanel<Participant, ParticipantFilter> {

    @SuppressWarnings("unused")
    @SpringBean
    private EventService eventService;

    private IModel<Event> event;

    private TextField searchTermTf;

    public ParticipantFilterPanel(final String id, final IModel<List<Participant>> model,
                                  final IModel<ParticipantFilter> filterModel, final IModel<Event> event,
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

        addBootstrapFormDecorator(form);
    }

    @Override
    protected Component getFocusableFormComponent() {
        return searchTermTf;
    }

    @Override
    public List<Participant> getFilteredData(final ParticipantFilter filter) {
        return eventService.getFilteredParticipants(event.getObject(), filter);
    }

    @Override
    public List<Participant> getDefaultData() {
        return eventService.getParticipants(event.getObject());
    }
}
