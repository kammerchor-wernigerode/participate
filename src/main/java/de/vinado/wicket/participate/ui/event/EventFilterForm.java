package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.ButtonBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.Icon;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType;
import de.vinado.wicket.participate.behavoirs.decorators.BootstrapInlineFormDecorator;
import de.vinado.wicket.participate.model.filters.EventFilter;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.AbstractSubmitLink;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
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

import java.util.Date;

public abstract class EventFilterForm extends Form<EventFilter> {

    private static final String TAG_NAME = "eventFilterForm";

    static {
        WicketTagIdentifier.registerWellKnownTagName(TAG_NAME);
    }

    public EventFilterForm(String id, IModel<EventFilter> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        setOutputMarkupId(true);

        add(termInput("searchTerm"));
        add(showAllCheck("showAll"));

        DateTextFieldConfig endDateConfig = createDateTextFieldConfig();
        MarkupContainer endDateInput;
        add(endDateInput = endDateInput("endDate", endDateConfig));
        endDateInput.setOutputMarkupId(true);
        add(startDateInput("startDate", (value, target) -> {
            endDateConfig.withStartDate(DateTime.parse(value, DateTimeFormat.forPattern("dd.MM.yyyy")));
            target.add(endDateInput);
        }));

        add(resetButton("reset"));
        add(applyButton("apply"));

        add(new CssClassNameAppender("form-inline clearfix"));
        visitChildren(FormComponent.class, (IVisitor<FormComponent<?>, Void>) (component, visit) -> {
            if (!(component instanceof Button)) {
                component.add(BootstrapInlineFormDecorator.decorate());
            }
            visit.dontGoDeeper();
        });
    }

    protected FormComponent<String> termInput(String id) {
        return new TextField<String>(id)
            .setLabel(new ResourceModel("search", "Search"));
    }

    protected FormComponent<Boolean> showAllCheck(String id) {
        return new CheckBox(id);
    }

    protected DateTextFieldConfig createDateTextFieldConfig() {
        DateTextFieldConfig config = new DateTextFieldConfig();
        config.withLanguage("de");
        config.withFormat("dd.MM.yyyy");
        config.autoClose(true);
        return config;
    }

    protected FormComponent<Date> startDateInput(String id, SerializableBiConsumer<String, AjaxRequestTarget> onChange) {
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

    protected FormComponent<Date> endDateInput(String id, DateTextFieldConfig config) {
        FormComponent<Date> field = new DateTextField(id, config);
        field.setLabel(new ResourceModel("to", "To"));
        field.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });
        return field;
    }

    protected AbstractLink resetButton(String id) {
        BootstrapAjaxLink<Void> button = new BootstrapAjaxLink<>(id, Buttons.Type.Default) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                resetFilter();
                target.add(EventFilterForm.this);
                onReset();
            }
        };
        button.setIconType(FontAwesome5IconType.undo_s);
        button.add(new TooltipBehavior(new ResourceModel("reset", "Reset")));
        return button;
    }

    private void resetFilter() {
        setModelObject(new EventFilter());
    }

    private AbstractSubmitLink applyButton(String id) {
        AjaxSubmitLink button = new AjaxSubmitLink(id) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                target.add(EventFilterForm.this);
                onApply();
            }
        };
        button.add(new ButtonBehavior(Buttons.Type.Primary));
        button.add(new Icon("icon", FontAwesome5IconType.filter_s));
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