package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.component.Snackbar;
import de.vinado.wicket.participate.component.behavoir.AjaxFocusBehavior;
import de.vinado.wicket.participate.component.behavoir.AutosizeBehavior;
import de.vinado.wicket.participate.component.behavoir.decorator.BootstrapHorizontalFormDecorator;
import de.vinado.wicket.participate.component.modal.BootstrapModal;
import de.vinado.wicket.participate.component.modal.BootstrapModalPanel;
import de.vinado.wicket.participate.component.provider.Select2StringProvider;
import de.vinado.wicket.participate.data.Event;
import de.vinado.wicket.participate.data.dto.EventDTO;
import de.vinado.wicket.participate.event.AjaxUpdateEvent;
import de.vinado.wicket.participate.event.RemoveEventUpdateEvent;
import de.vinado.wicket.participate.service.EventService;
import de.vinado.wicket.participate.service.PersonService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.wicketstuff.select2.Select2BootstrapTheme;
import org.wicketstuff.select2.Select2Choice;

/**
 * Panel for creation of a new {@link de.vinado.wicket.participate.data.Event}
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class AddEditEventPanel extends BootstrapModalPanel<EventDTO> {

    @SpringBean
    @SuppressWarnings("unused")
    private EventService eventService;

    @SpringBean
    @SuppressWarnings("unused")
    private PersonService personService;

    private boolean edit;
    private boolean remove = false;

    private IModel<Boolean> severalDays;

    /**
     * @param modal {@link de.vinado.wicket.participate.component.modal.BootstrapModal}
     * @param model {@link EventDTO EventDTO model}
     */
    public AddEditEventPanel(final BootstrapModal modal, final IModel<String> titleModel, final IModel<EventDTO> model) {
        super(modal, titleModel, model);

        edit = null != model.getObject().getEvent();

        severalDays = new CompoundPropertyModel<>(edit ? model.getObject().isSeveralDays() : Boolean.TRUE);

        final DateTextFieldConfig startDateConfig = new DateTextFieldConfig();
        startDateConfig.withLanguage("de");
        startDateConfig.withFormat("dd.MM.yyyy");
        startDateConfig.withStartDate(new DateTime());
        startDateConfig.autoClose(true);

        final DateTextFieldConfig endDateConfig = new DateTextFieldConfig();
        endDateConfig.withLanguage("de");
        endDateConfig.withFormat("dd.MM.yyyy");
        endDateConfig.withStartDate(new DateTime());
        endDateConfig.autoClose(true);

        final TextField<String> nameTf = new TextField<>("name");
        nameTf.setLabel(new ResourceModel("event.name", "Event Name"));
        nameTf.add(BootstrapHorizontalFormDecorator.decorate());
        nameTf.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
            }
        });
        inner.add(nameTf);

        final AjaxCheckBox isSeveralDaysCb = new AjaxCheckBox("isSeveralDays", severalDays) {
            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
                target.add(inner);
            }
        };
        isSeveralDaysCb.setLabel(new ResourceModel("event.multi-day", "Multi-day Event"));
        isSeveralDaysCb.add(BootstrapHorizontalFormDecorator.decorate());
        inner.add(isSeveralDaysCb);

        final Select2Choice<String> eventTypeS2c = new Select2Choice<>("eventType",
            new Select2StringProvider(eventService.getEventTypeList()));
        eventTypeS2c.add(new AjaxFocusBehavior());
        eventTypeS2c.setLabel(new ResourceModel("event", "Event"));
        eventTypeS2c.add(BootstrapHorizontalFormDecorator.decorate());
        eventTypeS2c.getSettings().setLanguage(getLocale().getLanguage());
        eventTypeS2c.getSettings().setCloseOnSelect(true);
        eventTypeS2c.getSettings().setTheme(new Select2BootstrapTheme(true));
        eventTypeS2c.setRequired(true);
        eventTypeS2c.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });
        inner.add(eventTypeS2c);

        final DateTextField endDateTf = new DateTextField("endDate", endDateConfig) {
            @Override
            protected void onConfigure() {
                setVisible(severalDays.getObject());
            }
        };

        final DateTextField startDateTf = new DateTextField("startDate", startDateConfig) {
            @Override
            protected void onConfigure() {
                setLabel(severalDays.getObject()
                    ? new ResourceModel("from", "From")
                    : new ResourceModel("on", "On"));
            }
        };
        startDateTf.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
                endDateConfig.withStartDate(DateTime.parse(startDateTf.getValue(), DateTimeFormat.forPattern("dd.MM.yyyy")));
                target.add(endDateTf);
            }
        });
        startDateTf.add(BootstrapHorizontalFormDecorator.decorate());
        startDateTf.setRequired(true);
        inner.add(startDateTf);

        endDateTf.setOutputMarkupId(true);
        endDateTf.add(BootstrapHorizontalFormDecorator.decorate(new ResourceModel("till", "Till")));
        endDateTf.setRequired(true);
        endDateTf.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
            }
        });
        inner.add(endDateTf);

        final Select2Choice<String> locationS2c = new Select2Choice<>("location",
            new Select2StringProvider(eventService.getLocationList()));
        locationS2c.setLabel(new ResourceModel("location", "Location"));
        locationS2c.add(BootstrapHorizontalFormDecorator.decorate());
        locationS2c.getSettings().setLanguage(getLocale().getLanguage());
        locationS2c.getSettings().setCloseOnSelect(true);
        locationS2c.getSettings().setTheme(new Select2BootstrapTheme(true));
        locationS2c.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
            }
        });
        inner.add(locationS2c);

        final TextArea<String> descriptionTa = new TextArea<>("description");
        descriptionTa.add(BootstrapHorizontalFormDecorator.decorate());
        descriptionTa.add(new AutosizeBehavior());
        descriptionTa.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
            }
        });
        inner.add(descriptionTa);

        final BootstrapAjaxLink removeBtn = new BootstrapAjaxLink("removeBtn", Buttons.Type.Link) {
            @Override
            protected void onInitialize() {
                super.onInitialize();
                setVisible(edit);
            }

            @Override
            public void onClick(final AjaxRequestTarget target) {
                remove = !remove;
                if (remove) {
                    setLabel(new ResourceModel("event.remove.hint", "Event will be removed"));
                    setIconType(FontAwesomeIconType.exclamation_circle);
                } else {
                    setLabel(new ResourceModel("event.remove", "Remove event"));
                    setIconType(FontAwesomeIconType.trash);
                }
                target.add(this);
            }
        };
        removeBtn.setLabel(new ResourceModel("event.remove", "Remove Event"));
        removeBtn.setIconType(FontAwesomeIconType.trash);
        removeBtn.setSize(Buttons.Size.Mini);
        removeBtn.setOutputMarkupId(true);
        inner.add(removeBtn);
    }

    /**
     * @param model  {@link EventDTO EventDTO model}
     * @param target Target
     * @inheritDoc
     */
    @Override
    protected void onSaveSubmit(final IModel<EventDTO> model, final AjaxRequestTarget target) {
        if (!severalDays.getObject()) {
            model.getObject().setEndDate(model.getObject().getStartDate());
        }

        if (edit) {
            if (remove) {
                eventService.removeEvent(model.getObject().getEvent());
                send(getPage(), Broadcast.BREADTH, new AjaxUpdateEvent(target));
                send(getWebPage(), Broadcast.BREADTH, new RemoveEventUpdateEvent(target));
                Snackbar.show(target, new ResourceModel("event.remove.success", "The event has been removed"));
                return;
            }
            onUpdate(eventService.saveEvent(model.getObject()), target);
        } else {
            onUpdate(eventService.createEvent(model.getObject()), target);
        }
    }

    public abstract void onUpdate(final Event savedEvent, final AjaxRequestTarget target);
}
