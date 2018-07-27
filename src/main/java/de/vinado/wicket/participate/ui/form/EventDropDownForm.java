package de.vinado.wicket.participate.ui.form;

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarForm;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelect;
import de.vinado.wicket.participate.data.Participant;
import de.vinado.wicket.participate.service.EventService;
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

    private BootstrapSelect<Participant> memberToEventBs;

    public EventDropDownForm(final String componentId, final IModel<Participant> model) {
        super(componentId, model);

        setDefaultValue(model.getObject());

        memberToEventBs = new BootstrapSelect<>("event", new PropertyModel<>(this, "defaultValue"),
            new LoadableDetachableModel<List<? extends Participant>>() {
                    @Override
                    protected List<? extends Participant> load() {
                        return eventService.getMemberToEventList(model.getObject().getMember());
                    }
                },
                new ChoiceRenderer<>("event.name"));
        memberToEventBs.add(new AjaxFormComponentUpdatingBehavior("hidden.bs.select") {
            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
                if (!model.getObject().equals(memberToEventBs.getConvertedInput())) {
                    onEventChange(memberToEventBs.getConvertedInput());
                }
            }
        });
        memberToEventBs.setLabel(Model.of(""));
        add(memberToEventBs);
    }

    public Participant getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(final Participant defaultValue) {
        this.defaultValue = defaultValue;
    }

    protected abstract void onEventChange(final Participant participant);
}
