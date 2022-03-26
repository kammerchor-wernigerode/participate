package de.vinado.wicket.participate.wicket.form.ui;

import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormType;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarForm;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelect;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.services.EventService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class EventDropDownForm extends NavbarForm<Participant> {

    @SpringBean
    private EventService eventService;

    public EventDropDownForm(String id, IModel<Participant> model) {
        super(id, model);

        type(FormType.Inline);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        ChoiceRenderer<Participant> renderer = new ChoiceRenderer<>("event.name");
        add(new BootstrapSelect<>("event", getModel(), choiceModel(), renderer)
            .add(new AjaxFormSubmitBehavior(this, "hidden.bs.select") {
                private static final long serialVersionUID = -3833360531341280253L;

                @Override
                protected void onSubmit(AjaxRequestTarget target) {
                    super.onSubmit(target);
                    EventDropDownForm.this.onSelect(target);
                }
            }));
    }

    protected IModel<? extends List<? extends Participant>> choiceModel() {
        return getModel().map(Participant::getSinger).map(eventService::getParticipants);
    }

    protected abstract void onSelect(AjaxRequestTarget target);
}
