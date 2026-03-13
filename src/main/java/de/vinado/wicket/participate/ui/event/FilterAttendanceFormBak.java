package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapCheckbox;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.services.EventService;
import org.apache.wicket.Application;
import org.apache.wicket.extensions.markup.html.form.select.IOptionRenderer;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOptions;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FilterAttendanceFormBak extends GenericPanel<FilterAttendanceFormBak.Data> {

    @SpringBean
    private EventService eventService;

    private final Form<Data> form;

    public FilterAttendanceFormBak(String id, IModel<Data> model) {
        super(id, model);
        this.form = new Form<>("form", model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(form);

        IModel<List<InvitationStatus>> statusesModel = LambdaModel.of(getModel(), Data::getStatuses, Data::setStatuses);
        Select<List<InvitationStatus>> statusesSelect = new Select<>("statuses", statusesModel);
        statusesSelect.setLabel(new ResourceModel("filter", "Filter"));
        form.add(statusesSelect);

        SimpleFormComponentLabel statusesLabel = new SimpleFormComponentLabel("statusesLabel", statusesSelect);
        form.add(statusesLabel);

        IModel<List<InvitationStatus>> choicesModel = getModel().map(this::choices);
        SelectOptions<InvitationStatus> defaultChoices = new SelectOptions<>("defaultChoices", choicesModel, new IOptionRenderer<>() {

            @Override
            public String getDisplayValue(InvitationStatus status) {
                String key = Classes.simpleName(status.getDeclaringClass()) + '.' + status.name();
                return Application.get().getResourceSettings().getLocalizer().getString(key, null);
            }

            @Override
            public IModel<InvitationStatus> getModel(InvitationStatus value) {
                return Model.of(value);
            }
        });
        statusesSelect.add(defaultChoices);

        IModel<Boolean> negateModel = LambdaModel.of(getModel(), Data::isNegate, Data::setNegate);
        BootstrapCheckbox negateCheckbox = new BootstrapCheckbox("negate", negateModel, new ResourceModel("negate", "Negate"));
        form.add(negateCheckbox);
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

        private final Collection<Event> events;

        private List<InvitationStatus> statuses = new ArrayList<>();

        private boolean negate;
    }

    public enum Choice {


    }
}
