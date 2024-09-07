package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome6IconType;
import de.vinado.app.participate.wicket.form.FormComponentLabel;
import de.vinado.wicket.common.AjaxFocusBehavior;
import de.vinado.wicket.form.AutosizeBehavior;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.dtos.EventDTO;
import de.vinado.wicket.participate.providers.Select2StringProvider;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.services.PersonService;
import lombok.SneakyThrows;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.select2.Select2BootstrapTheme;
import org.wicketstuff.select2.Select2Choice;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEditEventPanel extends GenericPanel<EventDTO> {

    @SpringBean
    @SuppressWarnings("unused")
    private EventService eventService;

    @SpringBean
    @SuppressWarnings("unused")
    private PersonService personService;

    private boolean edit;
    private boolean remove = false;

    private IModel<Boolean> severalDays;

    private final Form<EventDTO> form;

    public AddEditEventPanel(String id, IModel<EventDTO> model) {
        super(id, model);

        this.form = form("form");
    }

    protected Form<EventDTO> form(String wicketId) {
        return new Form<>(wicketId, getModel()) {

            @Override
            protected void onSubmit() {
                super.onSubmit();

                AddEditEventPanel.this.onSubmit();
            }
        };
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        edit = null != getModelObject().getEvent();
        form.setOutputMarkupId(true);
        severalDays = new CompoundPropertyModel<>(edit ? getModelObject().isSeveralDays() : Boolean.TRUE);

        add(form);

        DateTextFieldConfig startDateConfig = new DateTextFieldConfig();
        startDateConfig.withLanguage("de");
        startDateConfig.withFormat("dd.MM.yyyy");
        startDateConfig.withStartDate(new Date());
        startDateConfig.autoClose(true);

        DateTextFieldConfig endDateConfig = new DateTextFieldConfig();
        endDateConfig.withLanguage("de");
        endDateConfig.withFormat("dd.MM.yyyy");
        endDateConfig.withStartDate(new Date());
        endDateConfig.autoClose(true);

        AjaxCheckBox isSeveralDaysCb = new AjaxCheckBox("isSeveralDays", severalDays) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(form);
            }
        };
        isSeveralDaysCb.setLabel(new ResourceModel("event.multi-day", "Multi-day Event"));
        form.add(isSeveralDaysCb, new FormComponentLabel("isSeveralDaysLabel", isSeveralDaysCb));

        Select2Choice<String> eventTypeS2c = new Select2Choice<>("eventType",
            new Select2StringProvider(eventService::getEventTypes));
        eventTypeS2c.add(new AjaxFocusBehavior());
        eventTypeS2c.setLabel(new ResourceModel("event", "Event"));
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
        form.add(eventTypeS2c, new FormComponentLabel("eventTypeLabel", eventTypeS2c));

        WebMarkupContainer endDateContainer = new WebMarkupContainer("endDateContainer") {

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(severalDays.getObject());
            }
        };
        endDateContainer.setOutputMarkupId(true);
        DateTextField endDateTf = new DateTextField("endDate", endDateConfig);

        DateTextField startDateTf = new DateTextField("startDate", startDateConfig) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setLabel(severalDays.getObject()
                    ? new ResourceModel("from", "From")
                    : new ResourceModel("on", "On"));
            }
        };
        startDateTf.add(new AjaxFormComponentUpdatingBehavior("change") {

            @SneakyThrows
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                String pattern = endDateConfig.getFormat();
                SimpleDateFormat formatter = new SimpleDateFormat(pattern);
                endDateConfig.withStartDate(formatter.parse(startDateTf.getValue()));
                target.add(endDateTf);
            }
        });
        startDateTf.setRequired(true);
        startDateTf.setLabel(Model.of());
        FormComponentLabel startDateLabel = new FormComponentLabel("startDateLabel", startDateTf);
        startDateLabel.setOutputMarkupId(true);
        form.add(startDateTf, startDateLabel);

        endDateTf.setOutputMarkupId(true);
        endDateTf.setLabel(new ResourceModel("till", "Till"));
        endDateTf.setRequired(true);
        endDateTf.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });
        endDateContainer.add(endDateTf, new FormComponentLabel("endDateLabel", endDateTf));
        form.add(endDateContainer);

        Select2Choice<String> locationS2c = new Select2Choice<>("location",
            new Select2StringProvider(eventService::getLocationList));
        locationS2c.setLabel(new ResourceModel("location", "Location"));
        locationS2c.getSettings().setLanguage(getLocale().getLanguage());
        locationS2c.getSettings().setCloseOnSelect(true);
        locationS2c.getSettings().setTheme(new Select2BootstrapTheme(true));
        locationS2c.getSettings().setDropdownParent(form.getMarkupId());
        locationS2c.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });
        form.add(locationS2c, new FormComponentLabel("locationLabel", locationS2c));

        TextArea<String> descriptionTa = new TextArea<>("description");
        descriptionTa.setLabel(new ResourceModel("description", "Description"));
        descriptionTa.add(new AutosizeBehavior());
        descriptionTa.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });
        form.add(descriptionTa, new FormComponentLabel("descriptionLabel", descriptionTa));

        BootstrapAjaxLink<Void> removeBtn = new BootstrapAjaxLink<>("removeBtn", Buttons.Type.Link) {
            @Override
            protected void onInitialize() {
                super.onInitialize();
                setVisible(edit);
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                remove = !remove;
                if (remove) {
                    setLabel(new ResourceModel("event.remove.hint", "Event will be removed"));
                    setIconType(FontAwesome6IconType.circle_exclamation_s);
                } else {
                    setLabel(new ResourceModel("event.remove", "Remove event"));
                    setIconType(FontAwesome6IconType.trash_s);
                }
                target.add(this);
            }
        };
        removeBtn.setLabel(new ResourceModel("event.remove", "Remove Event"));
        removeBtn.setIconType(FontAwesome6IconType.trash_s);
        removeBtn.setSize(Buttons.Size.Small);
        removeBtn.setOutputMarkupId(true);
        form.add(removeBtn);
    }

    protected void onSubmit() {
        EventDTO dto = getModelObject();
        if (!severalDays.getObject()) {
            dto.setEndDate(dto.getStartDate());
        }

        if (edit) {
            if (remove) {
                delete(dto);
            } else {
                update(dto);
            }
        } else {
            create(dto);
        }
    }

    private void create(EventDTO dto) {
        Locale locale = getLocale();
        Event event = eventService.createEvent(dto, locale);
        dto.setEvent(event);
    }

    private void update(EventDTO dto) {
        Locale locale = getLocale();
        Event event = eventService.saveEvent(dto, locale);
        dto.setEvent(event);
    }

    private void delete(EventDTO dto) {
        Event event = dto.getEvent();
        eventService.removeEvent(event);
        event.setActive(false);
    }
}
