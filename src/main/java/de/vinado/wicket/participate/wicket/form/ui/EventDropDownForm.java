package de.vinado.wicket.participate.wicket.form.ui;

import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormType;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarForm;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelect;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.services.EventService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class EventDropDownForm extends NavbarForm<Participant> {

    @SuppressWarnings("unused")
    @SpringBean
    private EventService eventService;

    private Participant defaultValue;

    private BootstrapSelect<Participant> participantBs;

    public EventDropDownForm(final String componentId, final IModel<Participant> model) {
        super(componentId, model);
        type(FormType.Inline);

        setDefaultValue(model.getObject());

        participantBs = new BootstrapSelect<>("event", new PropertyModel<>(this, "defaultValue"),
            new LoadableDetachableModel<List<? extends Participant>>() {
                    @Override
                    protected List<? extends Participant> load() {
                        return eventService.getParticipants(model.getObject().getSinger());
                    }
                },
                new ChoiceRenderer<>("event.name"));
        participantBs.add(new AjaxFormComponentUpdatingBehavior("hidden.bs.select") {
            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
                if (!model.getObject().equals(participantBs.getConvertedInput())) {
                    onEventChange(participantBs.getConvertedInput());
                }
            }
        });
        participantBs.setLabel(Model.of(""));
        add(participantBs);
    }

    public Participant getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(final Participant defaultValue) {
        this.defaultValue = defaultValue;
    }

    protected abstract void onEventChange(final Participant participant);
}
