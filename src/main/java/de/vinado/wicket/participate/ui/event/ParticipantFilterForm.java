package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.ButtonBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.Icon;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.behavoirs.decorators.BootstrapInlineFormDecorator;
import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.Voice;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.AbstractSubmitLink;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.IMarkupSourcingStrategy;
import org.apache.wicket.markup.html.panel.PanelMarkupSourcingStrategy;
import org.apache.wicket.markup.parser.filter.WicketTagIdentifier;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.visit.IVisitor;

import java.util.Arrays;

/**
 * @author Vincent Nadoll
 */
public abstract class ParticipantFilterForm extends Form<ParticipantFilter> {

    private static final String TAG_NAME = "participantFilterForm";

    static {
        WicketTagIdentifier.registerWellKnownTagName(TAG_NAME);
    }

    public ParticipantFilterForm(String id, IModel<ParticipantFilter> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        setOutputMarkupId(true);

        add(nameInput("name"));
        add(invitationStatusSelect("invitationStatus"));
        add(voiceSelect("voice"));

        add(resetButton("reset"));
        AbstractSubmitLink apply;
        add(apply = applyButton("apply"));
        setDefaultButton(apply);

        add(new CssClassNameAppender("form-inline clearfix"));
        visitChildren(FormComponent.class, (IVisitor<FormComponent<?>, Void>) (component, visit) -> {
            if (!(component instanceof Button)) {
                component.add(BootstrapInlineFormDecorator.decorate());
            }
            visit.dontGoDeeper();
        });
    }

    protected FormComponent<String> nameInput(String id) {
        TextField<String> field = new TextField<>(id);
        field.setLabel(new ResourceModel("filter.names", "Filter by Name"));
        return field;
    }

    protected FormComponent<InvitationStatus> invitationStatusSelect(String id) {
        DropDownChoice<InvitationStatus> select = new DropDownChoice<>(id, Arrays.asList(InvitationStatus.values()),
            new EnumChoiceRenderer<>());
        select.setLabel(new ResourceModel("invitationStatus", "Invitation Status"));
        return select;
    }

    protected FormComponent<Voice> voiceSelect(String id) {
        DropDownChoice<Voice> select = new DropDownChoice<>(id, Arrays.asList(Voice.values()),
            new EnumChoiceRenderer<>());
        select.setLabel(new ResourceModel("voice", "Voice"));
        return select;
    }

    protected AbstractLink resetButton(String id) {
        BootstrapAjaxLink button = new BootstrapAjaxLink(id, Buttons.Type.Default) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                resetFilter();
                target.add(ParticipantFilterForm.this);
                onReset();
            }
        };
        button.setIconType(FontAwesomeIconType.refresh);
        button.add(new TooltipBehavior(new ResourceModel("reset", "Reset")));
        return button;
    }

    private void resetFilter() {
        setModelObject(new ParticipantFilter());
    }

    private AbstractSubmitLink applyButton(String id) {
        AjaxSubmitLink button = new AjaxSubmitLink(id) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.add(ParticipantFilterForm.this);
                onApply();
            }
        };
        button.add(new ButtonBehavior(Buttons.Type.Primary));
        button.add(new Icon("icon", FontAwesomeIconType.filter));
        button.add(new TooltipBehavior(new ResourceModel("filter", "Filter")));
        return button;
    }

    protected abstract void onApply();

    protected void onReset() {
        onApply();
    }

    @Override
    protected IMarkupSourcingStrategy newMarkupSourcingStrategy() {
        return new PanelMarkupSourcingStrategy(TAG_NAME, false);
    }
}
