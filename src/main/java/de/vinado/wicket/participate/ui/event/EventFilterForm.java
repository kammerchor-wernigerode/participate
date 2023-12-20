package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.ButtonBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.Icon;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePicker;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePickerConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType;
import de.vinado.wicket.bt4.datetimepicker.DatetimePickerIconConfig;
import de.vinado.wicket.bt4.datetimepicker.DatetimePickerResetIntent;
import de.vinado.wicket.bt4.datetimepicker.DatetimePickerResettingBehavior;
import de.vinado.wicket.bt4.tooltip.TooltipBehavior;
import de.vinado.wicket.common.UpdateOnEventBehavior;
import de.vinado.wicket.participate.model.filters.EventFilter;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.AbstractSubmitLink;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.model.ResourceModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;

import java.util.Date;

public abstract class EventFilterForm extends GenericPanel<EventFilter> {

    public EventFilterForm(String id, IModel<EventFilter> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        setOutputMarkupId(true);

        queue(form("form"));
        queue(searchTerm("searchTerm"));
        queue(showAll("showAll"));

        DatetimePickerConfig endDateConfig = createDatetimePickerConfig();
        queue(endDate("endDate", endDateConfig));
        queue(startDate("startDate", endDateConfig::withMinDate));

        queue(resetButton("reset"));
        queue(applyButton("apply"));
    }

    private Form<EventFilter> form(String wicketId) {
        return new Form<>(wicketId);
    }

    protected MarkupContainer searchTerm(String wicketId) {
        WebMarkupContainer container = new WebMarkupContainer(wicketId);

        IModel<String> model = LambdaModel.of(getModel(), EventFilter::getSearchTerm, EventFilter::setSearchTerm);
        FormComponent<String> control = new TextField<>("control", model)
            .setLabel(new ResourceModel("filter.event.form.control.search", "Search"));
        FormComponentLabel label = new SimpleFormComponentLabel("label", control);

        return container.add(control, label);
    }

    protected MarkupContainer showAll(String wicketId) {
        WebMarkupContainer container = new WebMarkupContainer(wicketId);

        IModel<Boolean> model = LambdaModel.of(getModel(), EventFilter::isShowAll, EventFilter::setShowAll);
        FormComponent<Boolean> control = new CheckBox("control", model)
            .setLabel(new ResourceModel("filter.event.form.control.show-all", "Show all"));
        FormComponentLabel label = new SimpleFormComponentLabel("label", control);

        return container.add(control, label);
    }

    protected MarkupContainer endDate(String wicketId, DatetimePickerConfig config) {
        WebMarkupContainer container = new WebMarkupContainer(wicketId);

        IModel<Date> model = LambdaModel.of(getModel(), EventFilter::getEndDate, EventFilter::setEndDate);
        FormComponent<Date> control = new DatetimePicker("control", model, config);
        control.setLabel(new ResourceModel("filter.event.form.control.to", "To"));
        control.add(new UpdateOnEventBehavior<>(DatetimePickerResetIntent.class));
        FormComponentLabel label = new SimpleFormComponentLabel("label", control);

        return container.add(control, label);
    }

    protected MarkupContainer startDate(String wicketId, SerializableConsumer<Date> onChange) {
        WebMarkupContainer container = new WebMarkupContainer(wicketId);

        IModel<Date> model = LambdaModel.of(getModel(), EventFilter::getStartDate, EventFilter::setStartDate);
        FormComponent<Date> control = new DatetimePicker("control", model, createDatetimePickerConfig());
        control.setLabel(new ResourceModel("filter.event.form.control.from", "From"));
        control.add(new DatetimePickerResettingBehavior(onChange));
        FormComponentLabel label = new SimpleFormComponentLabel("label", control);

        return container.add(control, label);
    }

    protected DatetimePickerConfig createDatetimePickerConfig() {
        DatetimePickerConfig config = new DatetimePickerConfig();
        config.with(new DatetimePickerIconConfig());
        config.useLocale(getLocale().getLanguage());
        config.withFormat("dd.MM.yyyy");
        config.useCurrent(false);
        return config;
    }

    protected AbstractLink resetButton(String id) {
        BootstrapAjaxLink<Void> button = new BootstrapAjaxLink<>(id, Buttons.Type.Outline_Secondary) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                resetFilter();
                target.add(EventFilterForm.this);
                onReset();
            }
        };
        button.setIconType(FontAwesome5IconType.undo_s);
        button.add(new TooltipBehavior(new ResourceModel("filter.event.form.button.reset", "Reset")));
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
        button.add(new TooltipBehavior(new ResourceModel("filter.event.form.button.submit", "Filter")));
        return button;
    }

    protected abstract void onApply();

    protected void onReset() {
        onApply();
    }
}
