package de.vinado.wicket.participate.ui.form;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePicker;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePickerConfig;
import de.vinado.wicket.participate.behavoirs.AutosizeBehavior;
import de.vinado.wicket.participate.behavoirs.decorators.BootstrapHorizontalFormDecorator;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.data.Participant;
import de.vinado.wicket.participate.data.dtos.ParticipantDTO;
import de.vinado.wicket.participate.events.EventUpdateEvent;
import de.vinado.wicket.participate.services.EventService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class FormPanel extends BreadCrumbPanel {

    @SuppressWarnings("unused")
    @SpringBean
    private EventService eventService;

    public FormPanel(final String id, final IBreadCrumbModel breadCrumbModel, final IModel<ParticipantDTO> model) {
        super(id, breadCrumbModel, model);

        final DatetimePickerConfig fromConfig = new DatetimePickerConfig();
        fromConfig.useLocale("de");
        fromConfig.useCurrent(false);
        fromConfig.withFormat("dd.MM.yyyy HH:mm");
        fromConfig.withMinuteStepping(30);

        final DatetimePickerConfig toConfig = new DatetimePickerConfig();
        toConfig.useLocale("de");
        toConfig.useCurrent(false);
        toConfig.withFormat("dd.MM.yyyy HH:mm");
        toConfig.withMinuteStepping(30);

        if (null != model.getObject().getEvent()) {
            fromConfig.withMinDate(model.getObject().getEvent().getStartDate());
            fromConfig.withMaxDate(model.getObject().getEvent().getEndDate());
            toConfig.withMinDate(model.getObject().getEvent().getStartDate());
            toConfig.withMaxDate(model.getObject().getEvent().getEndDate());
        }

        final Form form = new Form("form");
        add(form);

        final WebMarkupContainer wmc = new WebMarkupContainer("wmc");
        wmc.setOutputMarkupId(true);
        form.add(wmc);

        wmc.add(new Label("singer.displayName"));

        final DatetimePicker toDtP = new DatetimePicker("toDate", fromConfig);

        final DatetimePicker fromDtP = new DatetimePicker("fromDate", toConfig);
        fromDtP.add(new AjaxFormComponentUpdatingBehavior("dp.hide") {
            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
                if (!Strings.isEmpty(fromDtP.getValue())) {
                    try {
                        toConfig.withMinDate(new SimpleDateFormat("dd.MM.yyyy").parse(fromDtP.getValue()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    target.add(toDtP);
                }
            }
        });
        fromDtP.add(BootstrapHorizontalFormDecorator.decorate(new ResourceModel("from", "From")));
        wmc.add(fromDtP);

        toDtP.setOutputMarkupId(true);
        toDtP.add(BootstrapHorizontalFormDecorator.decorate(new ResourceModel("till", "Till")));
        toDtP.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
            }
        });
        wmc.add(toDtP);

        final CheckBox cateringCb = new CheckBox("catering");
        cateringCb.add(BootstrapHorizontalFormDecorator.decorate());
        wmc.add(cateringCb);

        final CheckBox accommodationCb = new CheckBox("accommodation");
        accommodationCb.add(BootstrapHorizontalFormDecorator.decorate());
        wmc.add(accommodationCb);

        final TextArea commentTa = new TextArea("comment");
        commentTa.setLabel(new ResourceModel("comments", "More comments"));
        commentTa.add(BootstrapHorizontalFormDecorator.decorate());
        commentTa.add(new AutosizeBehavior());
        wmc.add(commentTa);

        final BootstrapAjaxButton submitBtn = new BootstrapAjaxButton("submit", Buttons.Type.Success) {
            @Override
            protected void onSubmit(final AjaxRequestTarget target, final Form<?> inner) {
                send(getPage(), Broadcast.BREADTH, new EventUpdateEvent(
                    eventService.acceptEvent(model.getObject()).getEvent(),
                    target));
                Snackbar.show(target, new ResourceModel("invitation.accept.success", "Your data has been saved. You can leave this page now."));
                target.add(form);
            }
        };
        submitBtn.setLabel(new ResourceModel("save", "Save"));
        submitBtn.setSize(Buttons.Size.Small);
        wmc.add(submitBtn);

        final BootstrapAjaxButton declineBtn = new BootstrapAjaxButton("decline", Buttons.Type.Default) {
            @Override
            protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                final Participant savedParticipant = eventService.declineEvent(model.getObject());
                send(getPage(), Broadcast.BREADTH, new EventUpdateEvent(savedParticipant.getEvent(), target));
                Snackbar.show(target, new ResourceModel("invitation.decline.success", "Your cancellation has been saved. You can leave this page now."));
                target.add(form);
            }
        };
        declineBtn.setLabel(new ResourceModel("decline", "Decline"));
        declineBtn.setSize(Buttons.Size.Small);
        wmc.add(declineBtn);
    }

    @Override
    public IModel<String> getTitle() {
        return new ResourceModel("form", "Form");
    }
}
