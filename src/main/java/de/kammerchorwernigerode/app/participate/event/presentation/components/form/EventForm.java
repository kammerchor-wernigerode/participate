package de.kammerchorwernigerode.app.participate.event.presentation.components.form;

import de.kammerchorwernigerode.app.participate.event.presentation.model.EventDto;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.components.TooltipBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.form.BootstrapForm;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.form.BootstrapFormComponent.Layout;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.form.LocalDateTimeFormControl;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.form.TextAreaFormControl;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.form.TextFormControl;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.IAjaxCallListener;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.model.ResourceModel;

import java.time.LocalDateTime;
import java.util.List;

public abstract class EventForm extends GenericPanel<EventDto> {

    private final Form form;

    public EventForm(String id, IModel<EventDto> model) {
        super(id, model);
        this.form = new Form("form", model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        setRenderBodyOnly(true);
        add(form);

        BackLink backLink = new BackLink("backLink");
        backLink.setBody(new ResourceModel("back"));
        form.add(backLink);

        SaveLink saveLink = new SaveLink("saveLink", form);
        form.add(saveLink);
        form.setDefaultButton(saveLink);

        SaveLink saveButton = new SaveLink("saveButton", form);
        form.add(saveButton);

        BackLink backButton = new BackLink("backButton");
        backButton.add(new TooltipBehavior(new ResourceModel("back")));
        form.add(backButton);

        IModel<EventDto> model = getModel();

        IModel<String> summaryModel = LambdaModel.of(model, EventDto::getSummary, EventDto::setSummary);
        TextFormControl summaryControl = new TextFormControl("summary", summaryModel);
        summaryControl.setLayout(Layout.FLOATING_LABEL);
        summaryControl.setLabel(new ResourceModel("EventForm.summary"));
        form.add(summaryControl);

        IModel<LocalDateTime> startDateTimeModel = LambdaModel.of(model, EventDto::getStartDateTime,
            EventDto::setStartDateTime);
        LocalDateTimeFormControl startDateTimeControl = new LocalDateTimeFormControl("startDateTime",
            startDateTimeModel);
        startDateTimeControl.setLayout(Layout.FLOATING_LABEL);
        startDateTimeControl.setLabel(new ResourceModel("EventForm.startDateTime"));
        form.add(startDateTimeControl);

        Label toLabel = new Label("to", new ResourceModel("EventForm.to"));
        form.add(toLabel);

        IModel<LocalDateTime> endDateTimeModel = LambdaModel.of(model, EventDto::getEndDateTime,
            EventDto::setEndDateTime);
        LocalDateTimeFormControl endDateTimeControl = new LocalDateTimeFormControl("endDateTime", endDateTimeModel);
        endDateTimeControl.setLayout(Layout.FLOATING_LABEL);
        endDateTimeControl.setLabel(new ResourceModel("EventForm.endDateTime"));
        form.add(endDateTimeControl);

        AbstractLink detailsLink = new AbstractLink("detailsLink") { };
        detailsLink.setBody(new ResourceModel("EventForm.details"));
        form.add(detailsLink);

        IModel<String> locationModel = LambdaModel.of(model, EventDto::getLocation, EventDto::setLocation);
        TextFormControl locationControl = new TextFormControl("location", locationModel);
        locationControl.setLayout(Layout.FLOATING_LABEL);
        locationControl.setLabel(new ResourceModel("EventForm.location"));
        form.add(locationControl);

        IModel<String> descriptionModel = LambdaModel.of(model, EventDto::getDescription, EventDto::setDescription);
        TextAreaFormControl descriptionControl = new TextAreaFormControl("description", descriptionModel);
        descriptionControl.setLayout(Layout.FLOATING_LABEL);
        descriptionControl.setHeight(150);
        descriptionControl.setLabel(new ResourceModel("EventForm.description"));
        form.add(descriptionControl);
    }

    protected abstract void onSubmit();


    private class Form extends BootstrapForm<EventDto> {

        public Form(String id, IModel<EventDto> model) {
            super(id, model);
        }

        @Override
        protected void onSubmit() {
            EventForm.this.onSubmit();
        }
    }

    private static class BackLink extends AjaxLink<Void> {

        public BackLink(String id) {
            super(id);
        }

        @Override
        protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
            AjaxCallListener listener = createCloseListener();
            List<IAjaxCallListener> listeners = attributes.getAjaxCallListeners();
            listeners.add(listener);
        }

        private AjaxCallListener createCloseListener() {
            AjaxCallListener listener = new AjaxCallListener();
            listener.onBeforeSend("history.back()");
            return listener;
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
        }
    }

    private static class SaveLink extends AjaxSubmitLink {

        public SaveLink(String id, Form form) {
            super(id, form);
        }
    }
}
