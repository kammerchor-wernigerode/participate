package de.vinado.wicket.participate.ui.event.event;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePicker;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePickerConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelect;
import de.vinado.wicket.participate.common.ParticipateUtils;
import de.vinado.wicket.participate.component.Snackbar;
import de.vinado.wicket.participate.component.behavoir.AutosizeBehavior;
import de.vinado.wicket.participate.component.behavoir.decorator.BootstrapHorizontalFormDecorator;
import de.vinado.wicket.participate.component.modal.BootstrapModal;
import de.vinado.wicket.participate.component.modal.BootstrapModalPanel;
import de.vinado.wicket.participate.data.InvitationStatus;
import de.vinado.wicket.participate.data.dto.MemberToEventDTO;
import de.vinado.wicket.participate.service.EventService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class EditMemberInvitationPanel extends BootstrapModalPanel<MemberToEventDTO> {

    @SuppressWarnings("unused")
    @SpringBean
    private EventService eventService;

    public EditMemberInvitationPanel(final BootstrapModal modal, final IModel<MemberToEventDTO> model) {
        super(modal, new ResourceModel("invitation.edit", "Edit Invitation"), model);

        final DatetimePickerConfig fromConfig = new DatetimePickerConfig();
        fromConfig.useLocale("de");
        fromConfig.useCurrent(false);
        fromConfig.withMinDate(model.getObject().getEvent().getStartDate());
        fromConfig.withMaxDate(model.getObject().getEvent().getEndDate());
        fromConfig.withFormat("dd.MM.yyyy HH:mm");
        fromConfig.withMinuteStepping(30);

        final DatetimePickerConfig toConfig = new DatetimePickerConfig();
        toConfig.useLocale("de");
        toConfig.useCurrent(false);
        toConfig.withMinDate(model.getObject().getEvent().getStartDate());
        toConfig.withMaxDate(model.getObject().getEvent().getEndDate());
        toConfig.withFormat("dd.MM.yyyy HH:mm");
        toConfig.withMinuteStepping(30);

        final BootstrapSelect<InvitationStatus> invitationStatusBs = new BootstrapSelect<>("invitationStatus",
            Collections.unmodifiableList(Arrays.asList(InvitationStatus.values())), new EnumChoiceRenderer<>());
        invitationStatusBs.add(new AjaxFormComponentUpdatingBehavior("hidden.bs.select") {
            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
                model.getObject().setInvitationStatus((InvitationStatus) invitationStatusBs.getConvertedInput());
                target.add(inner);
            }
        });
        invitationStatusBs.setLabel(Model.of(""));
        invitationStatusBs.add(BootstrapHorizontalFormDecorator.decorate());
        inner.add(invitationStatusBs);

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
        inner.add(fromDtP);

        toDtP.setOutputMarkupId(true);
        toDtP.add(BootstrapHorizontalFormDecorator.decorate(new ResourceModel("till", "Till")));
        toDtP.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
            }
        });
        inner.add(toDtP);

        final CheckBox needsDinnerCb = new CheckBox("needsDinner") {
            @Override
            protected void onConfigure() {
                setEnabled(InvitationStatus.ACCEPTED.equals(model.getObject().getInvitationStatus()));
            }
        };
        needsDinnerCb.add(BootstrapHorizontalFormDecorator.decorate());
        needsDinnerCb.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {

            }
        });
        inner.add(needsDinnerCb);

        final TextArea needsDinnerCommentTa = new TextArea("needsDinnerComment") {
            @Override
            protected void onConfigure() {
                setEnabled(InvitationStatus.ACCEPTED.equals(model.getObject().getInvitationStatus()));
            }
        };
        needsDinnerCommentTa.add(new AutosizeBehavior());
        needsDinnerCommentTa.add(BootstrapHorizontalFormDecorator.decorate());
        needsDinnerCb.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {

            }
        });
        inner.add(needsDinnerCommentTa);

        final CheckBox needsPlaceToSleepCb = new CheckBox("needsPlaceToSleep") {
            @Override
            protected void onConfigure() {
                setEnabled(InvitationStatus.ACCEPTED.equals(model.getObject().getInvitationStatus()));
            }
        };
        needsPlaceToSleepCb.add(BootstrapHorizontalFormDecorator.decorate());
        needsPlaceToSleepCb.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {

            }
        });
        inner.add(needsPlaceToSleepCb);

        final TextArea needsPlaceToSleepCommentTa = new TextArea("needsPlaceToSleepComment") {
            @Override
            protected void onConfigure() {
                setEnabled(InvitationStatus.ACCEPTED.equals(model.getObject().getInvitationStatus()));
            }
        };
        needsPlaceToSleepCommentTa.add(new AutosizeBehavior());
        needsPlaceToSleepCommentTa.add(BootstrapHorizontalFormDecorator.decorate());
        needsPlaceToSleepCommentTa.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {

            }
        });
        inner.add(needsPlaceToSleepCommentTa);

        final TextArea commentTa = new TextArea("comment") {
            @Override
            protected void onConfigure() {
                setEnabled(InvitationStatus.ACCEPTED.equals(model.getObject().getInvitationStatus()));
            }
        };
        commentTa.add(new AutosizeBehavior());
        commentTa.add(BootstrapHorizontalFormDecorator.decorate());
        commentTa.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {

            }
        });
        inner.add(commentTa);

        final BootstrapAjaxLink<MemberToEventDTO> inviteMemberBtn = new BootstrapAjaxLink<MemberToEventDTO>(
            "inviteMemberBtn", model, Buttons.Type.Default, !InvitationStatus.UNINVITED.equals(model.getObject().getInvitationStatus())
            ? new ResourceModel("email.send.reminder", "Send Reminder")
            : new ResourceModel("email.send.invitation", "Send Invitation")) {
            @Override
            public void onClick(final AjaxRequestTarget target) {
                eventService.inviteMembersToEvent(model.getObject().getEvent(),
                    Collections.singletonList(model.getObject().getMemberToEvent()), !InvitationStatus.UNINVITED.equals(model.getObject().getInvitationStatus()));
                modal.close(target);
                if (!InvitationStatus.UNINVITED.equals(model.getObject().getInvitationStatus())) {
                    Snackbar.show(target, new ResourceModel("email.send.reminder.success", "A reminder has been sent"));
                } else {
                    Snackbar.show(target, new ResourceModel("email.send.invitation.success", "An invitation has been sent"));
                }
            }

            @Override
            protected void onConfigure() {
                setVisible(!model.getObject().isReviewed());
            }
        };
        inviteMemberBtn.setSize(Buttons.Size.Mini);
        inner.add(inviteMemberBtn);

        inner.add(new Label("token", ParticipateUtils.generateInvitationLink(model.getObject().getToken())));
    }
}
