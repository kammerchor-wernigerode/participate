package de.vinado.wicket.participate.ui.event;

import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.services.EventService;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FilterAttendanceForm extends GenericPanel<FilterAttendanceForm.Data> {

    @SpringBean
    private EventService eventService;

    private final Form<Data> form;

    public FilterAttendanceForm(String id, IModel<Data> model) {
        super(id, model);
        this.form = new Form<>("form", model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(form);

        IModel<List<InvitationStatus>> choicesModel = getModel().map(this::choices);
        EnumChoiceRenderer<InvitationStatus> renderer = new EnumChoiceRenderer<>();
        ListMultipleChoice<InvitationStatus> statusesSelect = new ListMultipleChoice<>("statuses", choicesModel, renderer);
        statusesSelect.setLabel(new ResourceModel("filter", "Filter"));
        form.add(statusesSelect);

        SimpleFormComponentLabel statusesLabel = new SimpleFormComponentLabel("statusesLabel", statusesSelect);
        form.add(statusesLabel);
    }

    private List<InvitationStatus> choices(Data data) {
        return data.getEvents().stream()
            .map(eventService::getParticipants)
            .flatMap(Collection::stream)
            .map(Participant::getInvitationStatus)
            .distinct()
            .toList();
    }


    @lombok.Data
    public static class Data implements Serializable {

        private final List<Event> events;

        private List<InvitationStatus> statuses = new ArrayList<>();
    }
}
