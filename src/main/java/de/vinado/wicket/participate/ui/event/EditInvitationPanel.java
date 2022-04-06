package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal.Size;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePicker;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePickerConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelect;
import de.vinado.wicket.bt4.datetimepicker.DatetimePickerIconConfig;
import de.vinado.wicket.bt4.datetimepicker.DatetimePickerResetIntent;
import de.vinado.wicket.bt4.datetimepicker.DatetimePickerResettingBehavior;
import de.vinado.wicket.bt4.form.decorator.BootstrapHorizontalFormDecorator;
import de.vinado.wicket.bt4.modal.FormModal;
import de.vinado.wicket.bt4.modal.ModalAnchor;
import de.vinado.wicket.common.UpdateOnEventBehavior;
import de.vinado.wicket.form.AutosizeBehavior;
import de.vinado.wicket.participate.ParticipateSession;
import de.vinado.wicket.participate.common.ParticipateUtils;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.dtos.ParticipantDTO;
import de.vinado.wicket.participate.services.EventService;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.net.URL;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class EditInvitationPanel extends FormModal<ParticipantDTO> {

    @SuppressWarnings("unused")
    @SpringBean
    private EventService eventService;

    @SpringBean
    private ApplicationProperties applicationProperties;

    public EditInvitationPanel(ModalAnchor modal, IModel<ParticipantDTO> model) {
        super(modal, model);

        size(Size.Large);
        title(new ResourceModel("invitation.edit", "Edit Invitation"));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Event event = getModelObject().getEvent();
        final DatetimePickerConfig fromConfig = new DatetimePickerConfig();
        fromConfig.useLocale("de");
        fromConfig.useCurrent(false);
        fromConfig.withMinDate(event.getStartDate());
        fromConfig.withMaxDate(DateUtils.addMilliseconds(DateUtils.addDays(event.getEndDate(), 1), -1));
        fromConfig.withFormat("dd.MM.yyyy HH:mm");
        fromConfig.withMinuteStepping(30);
        fromConfig.with(new DatetimePickerIconConfig());

        final DatetimePickerConfig toConfig = new DatetimePickerConfig();
        toConfig.useLocale("de");
        toConfig.useCurrent(false);
        toConfig.withMinDate(event.getStartDate());
        toConfig.withMaxDate(DateUtils.addMilliseconds(DateUtils.addDays(event.getEndDate(), 1), -1));
        toConfig.withFormat("dd.MM.yyyy HH:mm");
        toConfig.withMinuteStepping(30);
        toConfig.with(new DatetimePickerIconConfig());

        final BootstrapSelect<InvitationStatus> invitationStatusBs = new BootstrapSelect<>("invitationStatus",
            InvitationStatus.stream().collect(toList()), new EnumChoiceRenderer<>());
        invitationStatusBs.add(new AjaxFormComponentUpdatingBehavior("hidden.bs.select") {
            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
                getModelObject().setInvitationStatus(invitationStatusBs.getConvertedInput());
                target.add(form);
            }
        });
        invitationStatusBs.setLabel(Model.of(""));
        invitationStatusBs.add(BootstrapHorizontalFormDecorator.decorate());
        form.add(invitationStatusBs);

        final DatetimePicker toDtP = new DatetimePicker("toDate", toConfig);

        final DatetimePicker fromDtP = new DatetimePicker("fromDate", fromConfig);
        fromDtP.add(new DatetimePickerResettingBehavior(toConfig::withMinDate));
        fromDtP.add(BootstrapHorizontalFormDecorator.decorate(new ResourceModel("from", "From")));
        form.add(fromDtP);

        toDtP.setOutputMarkupId(true);
        toDtP.add(BootstrapHorizontalFormDecorator.decorate(new ResourceModel("till", "Till")));
        toDtP.add(new UpdateOnEventBehavior<>(DatetimePickerResetIntent.class));
        toDtP.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
            }
        });
        form.add(toDtP);

        final CheckBox cateringCb = new CheckBox("catering") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setEnabled(InvitationStatus.ACCEPTED.equals(EditInvitationPanel.this.getModelObject().getInvitationStatus()));
            }
        };
        cateringCb.add(BootstrapHorizontalFormDecorator.decorate());
        cateringCb.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {

            }
        });
        form.add(cateringCb);

        final CheckBox accommodationCb = new CheckBox("accommodation") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setEnabled(InvitationStatus.ACCEPTED.equals(EditInvitationPanel.this.getModelObject().getInvitationStatus()));
            }
        };
        accommodationCb.add(BootstrapHorizontalFormDecorator.decorate());
        accommodationCb.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {

            }
        });
        form.add(accommodationCb);

        NumberTextField<Integer> carSeatCountTf = new NumberTextField<Integer>("carSeatCount") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                if (!EditInvitationPanel.this.getModelObject().isCar()) {
                    EditInvitationPanel.this.getModelObject().setCarSeatCount((short) 0);
                }
                setEnabled(EditInvitationPanel.this.getModelObject().isCar());
            }
        };
        carSeatCountTf.setOutputMarkupId(true);
        carSeatCountTf.setMinimum(0);
        carSeatCountTf.setMaximum(127); // 1 Byte maximum signed integer
        form.add(carSeatCountTf);

        AjaxCheckBox carCb = new AjaxCheckBox("car") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(carSeatCountTf);
            }
        };
        form.add(carCb);

        final TextArea commentTa = new TextArea<>("comment") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setEnabled(InvitationStatus.ACCEPTED.equals(EditInvitationPanel.this.getModelObject().getInvitationStatus()));
            }
        };
        commentTa.add(new AutosizeBehavior());
        commentTa.add(BootstrapHorizontalFormDecorator.decorate());
        commentTa.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {

            }
        });
        form.add(commentTa);

        final BootstrapAjaxLink<ParticipantDTO> inviteSingerBtn = new BootstrapAjaxLink<ParticipantDTO>(
            "inviteSingerBtn", getModel(), Buttons.Type.Default, !InvitationStatus.UNINVITED.equals(getModelObject().getInvitationStatus())
            ? new ResourceModel("email.send.reminder", "Send Reminder")
            : new ResourceModel("email.send.invitation", "Send Invitation")) {
            @Override
            public void onClick(final AjaxRequestTarget target) {
                eventService.inviteParticipant(EditInvitationPanel.this.getModelObject().getParticipant(), ParticipateSession.get().getUser());
                Optional.ofNullable(EditInvitationPanel.this.findParent(ModalAnchor.class))
                    .ifPresent(anchor -> anchor.close(target));
                if (!InvitationStatus.UNINVITED.equals(EditInvitationPanel.this.getModelObject().getInvitationStatus())) {
                    Snackbar.show(target, new ResourceModel("email.send.reminder.success", "A reminder has been sent"));
                } else {
                    Snackbar.show(target, new ResourceModel("email.send.invitation.success", "An invitation has been sent"));
                }
            }
        };
        inviteSingerBtn.setSize(Buttons.Size.Small);
        form.add(inviteSingerBtn);

        URL href = ParticipateUtils.generateInvitationLink(
            applicationProperties.getBaseUrl(),
            getModelObject().getToken());
        Label link = new Label("token", href);
        link.add(AttributeModifier.append("href", href));
        link.add(AttributeModifier.append("target", "_blank"));
        form.add(link);
    }
}
