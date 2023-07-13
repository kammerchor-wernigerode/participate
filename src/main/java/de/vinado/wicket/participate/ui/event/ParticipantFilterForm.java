package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.ButtonBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.Icon;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType;
import de.vinado.wicket.bt4.tooltip.TooltipBehavior;
import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.Voice;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.AbstractSubmitLink;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.IMarkupSourcingStrategy;
import org.apache.wicket.markup.html.panel.PanelMarkupSourcingStrategy;
import org.apache.wicket.markup.parser.filter.WicketTagIdentifier;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.model.ResourceModel;

import java.util.Arrays;
import java.util.stream.Collectors;

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

        add(name("name"));
        add(invitationStatus("invitationStatus"));
        add(voice("voice"));

        add(resetButton("reset"));
        add(applyButton("apply", this));

        add(new CssClassNameAppender("row row-cols-lg-auto g-3 align-items-center"));
    }

    protected MarkupContainer name(String wicketId) {
        WebMarkupContainer container = new WebMarkupContainer(wicketId);

        IModel<String> model = LambdaModel.of(getModel(), ParticipantFilter::getName, ParticipantFilter::setName);
        FormComponent<String> control = new TextField<>("control", model)
            .setLabel(new ResourceModel("filter.names", "Filter by Name"));
        FormComponentLabel label = new SimpleFormComponentLabel("label", control);

        return container.add(control, label);
    }

    protected MarkupContainer invitationStatus(String wicketId) {
        WebMarkupContainer container = new WebMarkupContainer(wicketId);

        IModel<InvitationStatus> model = LambdaModel.of(getModel(), ParticipantFilter::getInvitationStatus, ParticipantFilter::setInvitationStatus);
        FormComponent<InvitationStatus> control = new DropDownChoice<>("control", model,
            InvitationStatus.stream().collect(Collectors.toList()), new EnumChoiceRenderer<>(this))
            .setLabel(new ResourceModel("invitationStatus", "Invitation Status"));
        FormComponentLabel label = new SimpleFormComponentLabel("label", control);

        return container.add(control, label);
    }

    protected MarkupContainer voice(String wicketId) {
        WebMarkupContainer container = new WebMarkupContainer(wicketId);

        IModel<Voice> model = LambdaModel.of(getModel(), ParticipantFilter::getVoice, ParticipantFilter::setVoice);
        FormComponent<Voice> control = new DropDownChoice<>("control", model,
            Arrays.asList(Voice.values()), new EnumChoiceRenderer<>(this))
            .setLabel(new ResourceModel("voice", "Voice"));
        FormComponentLabel label = new SimpleFormComponentLabel("label", control);

        return container.add(control, label);
    }

    protected AbstractLink resetButton(String id) {
        BootstrapAjaxLink<Void> button = new BootstrapAjaxLink<>(id, Buttons.Type.Outline_Secondary) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                resetFilter();
                target.add(ParticipantFilterForm.this);
                onReset();
            }
        };
        button.setIconType(FontAwesome5IconType.undo_s);
        button.add(new TooltipBehavior(new ResourceModel("reset", "Reset")));
        return button;
    }

    private void resetFilter() {
        setModelObject(new ParticipantFilter());
    }

    private AbstractSubmitLink applyButton(String id, Form<ParticipantFilter> form) {
        AjaxSubmitLink button = new AjaxSubmitLink(id) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                target.add(ParticipantFilterForm.this);
                onApply();
            }
        };
        button.add(new ButtonBehavior(Buttons.Type.Primary));
        button.add(new Icon("icon", FontAwesome5IconType.filter_s));
        button.add(new TooltipBehavior(new ResourceModel("filter", "Filter")));
        form.setDefaultButton(button);
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
