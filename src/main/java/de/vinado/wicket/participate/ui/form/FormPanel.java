package de.vinado.wicket.participate.ui.form;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePicker;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePickerConfig;
import de.vinado.wicket.participate.behavoirs.AutosizeBehavior;
import de.vinado.wicket.participate.behavoirs.decorators.BootstrapHorizontalFormDecorator;
import de.vinado.wicket.participate.components.modals.BootstrapModal;
import de.vinado.wicket.participate.components.modals.DismissableBootstrapModalPanel;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.events.EventUpdateEvent;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.dtos.ParticipantDTO;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.ui.pages.BasePage;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.DateUtils;
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
import java.util.Date;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class FormPanel extends BreadCrumbPanel {

    @SuppressWarnings("unused")
    @SpringBean
    private EventService eventService;

    @Getter
    @Setter
    private boolean deadlineMissed;

    public FormPanel(final String id, final IBreadCrumbModel breadCrumbModel, final IModel<ParticipantDTO> model) {
        super(id, breadCrumbModel, model);

        this.deadlineMissed = DateUtils.addDays(new Date(), 14).after(model.getObject().getEvent().getStartDate());

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
        accommodationCb.setEnabled(!deadlineMissed);
        wmc.add(accommodationCb);

        final WebMarkupContainer deadlineWmc = new WebMarkupContainer("deadlineWmc");
        deadlineWmc.setVisible(deadlineMissed);
        wmc.add(deadlineWmc);

        final Label missedDeadlineWarning = new Label("deadlineMissed", new ResourceModel("invitation.missed-deadline.warning", "You are not able to change your accommodation answer because you missed the deadline of two weeks before the event starts."));
        deadlineWmc.add(missedDeadlineWarning);

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
                displaySuccessionModal(target, model);
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

    private void displaySuccessionModal(AjaxRequestTarget target, IModel<ParticipantDTO> model) {
        BootstrapModal modal = ((BasePage) getWebPage()).getModal();
        ResourceModel titleModel = new ResourceModel("invitation.accept.success", "Thank you for your registration!");
        ResourceModel messageModel;

        if (eventService.hasDeadlineExpired(model.getObject().getParticipant())) {
            messageModel = new ResourceModel("invitation.accept.deadline.after", ""
                + "Please contact the responsible person again, as the deadline has already expired. So you can be sure that we have you on the screen.\n\n"
                + "Please note that you have to organize a sleeping place for yourself.\n\n"
                + "If you have general questions about the event, feel free to contact the person in charge.");
        } else {
            messageModel = new ResourceModel("invitation.accept.deadline.before",
                "If you have questions about the event, feel free to contact the responsible person.");
        }

        DismissableBootstrapModalPanel<String> confirmation = new DismissableBootstrapModalPanel<>(modal,
            titleModel, messageModel);
        modal.setContent(confirmation);
        modal.show(target);
    }

    @Override
    public IModel<String> getTitle() {
        return new ResourceModel("form", "Form");
    }
}
