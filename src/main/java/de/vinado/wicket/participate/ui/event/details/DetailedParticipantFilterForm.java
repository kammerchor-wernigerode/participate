package de.vinado.wicket.participate.ui.event.details;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.ButtonBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.Icon;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.behavoirs.decorators.BootstrapInlineFormDecorator;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.Voice;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.AbstractSubmitLink;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
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
import org.danekja.java.util.function.serializable.SerializableBiConsumer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Arrays;
import java.util.Date;

/**
 * @author Vincent Nadoll
 */
public abstract class DetailedParticipantFilterForm extends Form<ParticipantFilter> {

    private static final String TAG_NAME = "detailedParticipantForm";

    static {
        WicketTagIdentifier.registerWellKnownTagName(TAG_NAME);
    }

    private final IModel<Event> eventModel;

    public DetailedParticipantFilterForm(String id, IModel<ParticipantFilter> model, IModel<Event> eventModel) {
        super(id, model);
        this.eventModel = eventModel;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        setOutputMarkupId(true);

        add(nameInput("name"));
        add(commentInput("comment"));
        add(invitationStatusSelect("invitationStatus"));
        add(voiceSelect("voice"));
        add(accommodationInput("accommodation"));
        add(cateringInput("catering"));

        DateTextFieldConfig toDateConfig = createDateTextFieldConfig();
        MarkupContainer toDateField = add(toDateInput("toDate", toDateConfig));
        toDateField.setOutputMarkupId(true);
        add(fromDateInput("fromDate", (value, target) -> {
            toDateConfig.withStartDate(DateTime.parse(value, DateTimeFormat.forPattern("dd.MM.yyyy")));
            target.add(toDateField);
        }));

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

    protected FormComponent<String> commentInput(String id) {
        TextField<String> field = new TextField<>(id);
        field.setLabel(new ResourceModel("filter.comments", "Filter by comments"));
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

    protected FormComponent<Date> fromDateInput(String id, SerializableBiConsumer<String, AjaxRequestTarget> onChange) {
        DateTextFieldConfig config = createDateTextFieldConfig();

        DateTextField field = new DateTextField(id, config);
        field.setLabel(new ResourceModel("from", "From"));
        field.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                onChange.accept(getFormComponent().getValue(), target);
            }
        });
        return field;
    }

    protected FormComponent<Date> toDateInput(String id, DateTextFieldConfig config) {
        FormComponent<Date> field = new DateTextField(id, config);
        field.setLabel(new ResourceModel("to", "To"));
        field.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });
        return field;
    }

    protected FormComponent<Boolean> accommodationInput(String id) {
        return new CheckBox(id);
    }

    protected FormComponent<Boolean> cateringInput(String id) {
        return new CheckBox(id);
    }

    protected DateTextFieldConfig createDateTextFieldConfig() {
        Event event = eventModel.getObject();
        DateTextFieldConfig config = new DateTextFieldConfig();
        config.withLanguage("de");
        config.withFormat("dd.MM.yyyy");
        config.withStartDate(new DateTime(event.getStartDate()));
        config.withEndDate(new DateTime(event.getEndDate()));
        config.autoClose(true);
        return config;
    }

    protected AbstractLink resetButton(String id) {
        BootstrapAjaxLink button = new BootstrapAjaxLink(id, Buttons.Type.Default) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                resetFilter();
                target.add(DetailedParticipantFilterForm.this);
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
                target.add(DetailedParticipantFilterForm.this);
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

    @Override
    protected void onDetach() {
        eventModel.detach();
        super.onDetach();
    }
}
