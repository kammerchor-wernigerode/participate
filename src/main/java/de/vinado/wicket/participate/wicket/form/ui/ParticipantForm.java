package de.vinado.wicket.participate.wicket.form.ui;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePicker;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePickerConfig;
import de.agilecoders.wicket.jquery.Key;
import de.vinado.wicket.bt4.button.BootstrapAjaxButton;
import de.vinado.wicket.bt4.datetimepicker.DatetimePickerIconConfig;
import de.vinado.wicket.bt4.datetimepicker.DatetimePickerResetIntent;
import de.vinado.wicket.bt4.datetimepicker.DatetimePickerResettingBehavior;
import de.vinado.wicket.bt4.datetimepicker.DatetimePickerWidgetPositioningConfig;
import de.vinado.wicket.bt4.form.decorator.BootstrapHorizontalFormDecorator;
import de.vinado.wicket.common.UpdateOnEventBehavior;
import de.vinado.wicket.form.AutosizeBehavior;
import de.vinado.wicket.participate.model.Accommodation;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.dtos.ParticipantDTO;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.IMarkupSourcingStrategy;
import org.apache.wicket.markup.html.panel.PanelMarkupSourcingStrategy;
import org.apache.wicket.markup.parser.filter.WicketTagIdentifier;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.model.ResourceModel;

import java.util.Date;

/**
 * @author Vincent Nadoll
 */
public abstract class ParticipantForm extends Form<ParticipantDTO> {

    private static final long serialVersionUID = -2198624824622093618L;

    private static final String TAG_NAME = "participantForm";

    static {
        WicketTagIdentifier.registerWellKnownTagName(TAG_NAME);
    }

    public ParticipantForm(String id, IModel<ParticipantDTO> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        setOutputMarkupId(true);

        Event event = getModelObject().getEvent();
        DatetimePickerConfig fromConfig = createDatetimePickerConfig(event);
        DatetimePickerConfig toConfig = createDatetimePickerConfig(event);

        add(new Label("singer.displayName"));

        DatetimePicker toDtP = new DatetimePicker("toDate", toConfig);

        WebMarkupContainer periodHelp;
        add(periodHelp = new WebMarkupContainer("periodHelp"));
        periodHelp.setOutputMarkupId(true);

        DatetimePicker fromDtP = new DatetimePicker("fromDate", fromConfig);
        fromDtP.add(new DatetimePickerResettingBehavior(toConfig::withMinDate));
        fromDtP.add(AttributeAppender.append("aria-describedby", periodHelp.getMarkupId()));
        add(fromDtP, new FormComponentLabel("fromDateLabel", fromDtP));

        toDtP.setOutputMarkupId(true);
        toDtP.add(new UpdateOnEventBehavior<>(DatetimePickerResetIntent.class));
        add(toDtP, new FormComponentLabel("toDateLabel", toDtP));

        CheckBox cateringCb = new CheckBox("catering");
        cateringCb.add(BootstrapHorizontalFormDecorator.decorate());
        add(cateringCb);

        IModel<Accommodation> model = LambdaModel.of(getModel(), ParticipantDTO::getAccommodation, ParticipantDTO::setAccommodation);
        add(new AccommodationFormGroup("accommodation", model));

        NumberTextField<Short> carSeatCountTf = new NumberTextField<>("carSeatCount") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                if (!ParticipantForm.this.getModelObject().isCar()) {
                    ParticipantForm.this.getModelObject().setCarSeatCount((short) 0);
                }
                setEnabled(ParticipantForm.this.getModelObject().isCar());
            }
        };
        carSeatCountTf.setOutputMarkupId(true);
        carSeatCountTf.setMinimum((short) 0);
        carSeatCountTf.setMaximum((short) 127); // 1 Byte maximum signed integer
        add(carSeatCountTf, new FormComponentLabel("carSeatCountLabel", carSeatCountTf));

        AjaxCheckBox carCb = new AjaxCheckBox("car") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(carSeatCountTf);
            }
        };
        add(carCb, new FormComponentLabel("carLabel", carCb));

        TextArea<?> commentTa = new TextArea<>("comment");
        commentTa.setLabel(new ResourceModel("comments", "More comments"));
        commentTa.add(BootstrapHorizontalFormDecorator.decorate());
        commentTa.add(new AutosizeBehavior());
        add(commentTa);

        BootstrapAjaxButton submitBtn = new BootstrapAjaxButton("submit", Buttons.Type.Success) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                ParticipantForm.this.getModelObject().setInvitationStatus(InvitationStatus.ACCEPTED);
                onAcceptEvent(target);
                target.add(ParticipantForm.this);
            }
        };
        submitBtn.setLabel(new ResourceModel("save", "Save"));
        add(submitBtn);

        BootstrapAjaxButton declineBtn = new BootstrapAjaxButton("decline", Buttons.Type.Default) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                ParticipantForm.this.getModelObject().setInvitationStatus(InvitationStatus.DECLINED);
                onDeclineEvent(target);
                target.add(ParticipantForm.this);
            }
        };
        declineBtn.setLabel(new ResourceModel("decline", "Decline"));
        add(declineBtn);

        add(new BootstrapAjaxButton("acceptTentatively", Buttons.Type.Warning) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                ParticipantForm.this.getModelObject().setInvitationStatus(InvitationStatus.TENTATIVE);
                onAcceptTentatively(target);
                target.add(ParticipantForm.this);
            }
        }.setLabel(new ResourceModel("tentative", "Tentative")));
    }

    protected DatetimePickerConfig createDatetimePickerConfig(Event event) {
        DatetimePickerConfig config = new DatetimePickerConfig();
        config.useLocale("de");
        config.useCurrent(false);
        config.withFormat("dd.MM.yyyy HH:mm");
        config.withMinuteStepping(30);
        config.with(new DatetimePickerIconConfig());

        config.withMaxDate(getMaximumEndDate(event));
        config.withMinDate(event.getStartDate());

        Key<DatetimePickerWidgetPositioningConfig> positioningConfigKey = new Key<>("widgetPositioning");
        DatetimePickerWidgetPositioningConfig widgetPositioning = new DatetimePickerWidgetPositioningConfig()
            .withVerticalPositioning("bottom");
        config.put(positioningConfigKey, widgetPositioning);

        return config;
    }

    private Date getMaximumEndDate(Event event) {
        Date date = DateUtils.addDays(event.getEndDate(), 1);
        return DateUtils.addMilliseconds(date, -1);
    }

    protected abstract void onAcceptEvent(AjaxRequestTarget target);

    protected abstract void onDeclineEvent(AjaxRequestTarget target);

    protected abstract void onAcceptTentatively(AjaxRequestTarget target);

    @Override
    protected IMarkupSourcingStrategy newMarkupSourcingStrategy() {
        return new PanelMarkupSourcingStrategy(TAG_NAME, false);
    }
}
