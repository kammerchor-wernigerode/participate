package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal.Size;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType;
import de.vinado.wicket.bt4.form.decorator.BootstrapHorizontalFormDecorator;
import de.vinado.wicket.bt4.modal.FormModal;
import de.vinado.wicket.bt4.modal.ModalAnchor;
import de.vinado.wicket.common.AjaxFocusBehavior;
import de.vinado.wicket.form.AutosizeBehavior;
import de.vinado.wicket.participate.ParticipateSession;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.dtos.EventDTO;
import de.vinado.wicket.participate.providers.Select2StringProvider;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.services.PersonService;
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
 * Panel for creation of a new {@link de.vinado.wicket.participate.model.Event}
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class AddEditEventPanel extends FormModal<EventDTO> {

    @SpringBean
    @SuppressWarnings("unused")
    private EventService eventService;

    @SpringBean
    @SuppressWarnings("unused")
    private PersonService personService;

    private boolean edit;
    private boolean remove = false;

    private IModel<Boolean> severalDays;

    public AddEditEventPanel(final ModalAnchor modal, final IModel<String> titleModel, final IModel<EventDTO> model) {
        super(modal, model);

        size(Size.Large);
        title(titleModel);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        edit = null != getModelObject().getEvent();

        severalDays = new CompoundPropertyModel<>(edit ? getModelObject().isSeveralDays() : Boolean.TRUE);

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
        form.add(nameTf);

        final AjaxCheckBox isSeveralDaysCb = new AjaxCheckBox("isSeveralDays", severalDays) {
            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
                target.add(form);
            }
        };
        isSeveralDaysCb.setLabel(new ResourceModel("event.multi-day", "Multi-day Event"));
        isSeveralDaysCb.add(BootstrapHorizontalFormDecorator.decorate());
        form.add(isSeveralDaysCb);

        final Select2Choice<String> eventTypeS2c = new Select2Choice<>("eventType",
            new Select2StringProvider(eventService::getEventTypes));
        eventTypeS2c.add(new AjaxFocusBehavior());
        eventTypeS2c.setLabel(new ResourceModel("event", "Event"));
        eventTypeS2c.add(BootstrapHorizontalFormDecorator.decorate());
        eventTypeS2c.getSettings().setLanguage(getLocale().getLanguage());
        eventTypeS2c.getSettings().setCloseOnSelect(true);
        eventTypeS2c.getSettings().setTheme(new Select2BootstrapTheme(true));
        eventTypeS2c.getSettings().setDropdownParent(form.getMarkupId());
        eventTypeS2c.setRequired(true);
        eventTypeS2c.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });
        form.add(eventTypeS2c);

        final DateTextField endDateTf = new DateTextField("endDate", endDateConfig) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(severalDays.getObject());
            }
        };

        final DateTextField startDateTf = new DateTextField("startDate", startDateConfig) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
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
        form.add(startDateTf);

        endDateTf.setOutputMarkupId(true);
        endDateTf.add(BootstrapHorizontalFormDecorator.decorate(new ResourceModel("till", "Till")));
        endDateTf.setRequired(true);
        endDateTf.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
            }
        });
        form.add(endDateTf);

        final Select2Choice<String> locationS2c = new Select2Choice<>("location",
            new Select2StringProvider(eventService::getLocationList));
        locationS2c.setLabel(new ResourceModel("location", "Location"));
        locationS2c.add(BootstrapHorizontalFormDecorator.decorate());
        locationS2c.getSettings().setLanguage(getLocale().getLanguage());
        locationS2c.getSettings().setCloseOnSelect(true);
        locationS2c.getSettings().setTheme(new Select2BootstrapTheme(true));
        locationS2c.getSettings().setDropdownParent(form.getMarkupId());
        locationS2c.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
            }
        });
        form.add(locationS2c);

        final TextArea<String> descriptionTa = new TextArea<>("description");
        descriptionTa.add(BootstrapHorizontalFormDecorator.decorate());
        descriptionTa.add(new AutosizeBehavior());
        descriptionTa.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
            }
        });
        form.add(descriptionTa);

        final BootstrapAjaxLink<Void> removeBtn = new BootstrapAjaxLink<>("removeBtn", Buttons.Type.Link) {
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
                    setIconType(FontAwesome5IconType.exclamation_circle_s);
                } else {
                    setLabel(new ResourceModel("event.remove", "Remove event"));
                    setIconType(FontAwesome5IconType.trash_s);
                }
                target.add(this);
            }
        };
        removeBtn.setLabel(new ResourceModel("event.remove", "Remove Event"));
        removeBtn.setIconType(FontAwesome5IconType.trash_s);
        removeBtn.setSize(Buttons.Size.Small);
        removeBtn.setOutputMarkupId(true);
        form.add(removeBtn);
    }

    @Override
    protected void onSubmit(AjaxRequestTarget target) {
        EventDTO dto = getModelObject();
        if (!severalDays.getObject()) {
            dto.setEndDate(dto.getStartDate());
        }

        if (edit) {
            if (remove) {
                eventService.removeEvent(dto.getEvent());
                getSession().setMetaData(ParticipateSession.event, null);
                send(getWebPage(), Broadcast.BREADTH, new EventSelectedEvent(null));
                Snackbar.show(target, new ResourceModel("event.remove.success", "The event has been removed"));
                return;
            }
            onUpdate(eventService.saveEvent(dto), target);
        } else {
            onUpdate(eventService.createEvent(dto), target);
        }
    }

    public abstract void onUpdate(final Event savedEvent, final AjaxRequestTarget target);
}
