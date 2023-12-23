package de.vinado.wicket.participate.wicket.form.ui;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.tempusdominus.TempusDominusConfig;
import de.vinado.app.participate.wicket.bt5.button.BootstrapAjaxButton;
import de.vinado.app.participate.wicket.bt5.form.DateTimeTextField;
import de.vinado.wicket.form.AutosizeBehavior;
import de.vinado.wicket.participate.common.DateUtils;
import de.vinado.wicket.participate.model.Accommodation;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.dtos.ParticipantDTO;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.IMarkupSourcingStrategy;
import org.apache.wicket.markup.html.panel.PanelMarkupSourcingStrategy;
import org.apache.wicket.markup.parser.filter.WicketTagIdentifier;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.model.ResourceModel;

import java.util.Date;
import java.util.stream.Stream;

public abstract class ParticipantForm extends Form<ParticipantDTO> {

    private static final String TAG_NAME = "participantForm";

    static {
        WicketTagIdentifier.registerWellKnownTagName(TAG_NAME);
    }

    private FeedbackPanel feedback;

    public ParticipantForm(String id, IModel<ParticipantDTO> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        setOutputMarkupId(true);

        Event event = getModelObject().getEvent();
        TempusDominusConfig fromConfig = createTempusDominusConfig(event);
        TempusDominusConfig toConfig = createTempusDominusConfig(event);

        add(feedback = feedback("feedback"));
        feedback.setOutputMarkupId(true);

        add(new Label("singer.displayName"));

        FormComponent<Date> toDtP = new DateTimeTextField("toDate", toConfig);

        WebMarkupContainer periodHelp;
        add(periodHelp = new WebMarkupContainer("periodHelp"));
        periodHelp.setOutputMarkupId(true);

        FormComponent<Date> fromDtP = new DateTimeTextField("fromDate", fromConfig) {

            @Override
            protected Stream<HeaderItem> additionalHeaderItems(Component component) {
                String source = component.getMarkupId();
                String target = toDtP.getMarkupId();
                return Stream.of(linkMinDate(source, target));
            }
        };
        fromDtP.add(AttributeAppender.append("aria-describedby", periodHelp.getMarkupId()));
        add(fromDtP, new FormComponentLabel("fromDateLabel", fromDtP));

        toDtP.setOutputMarkupId(true);
        add(toDtP, new FormComponentLabel("toDateLabel", toDtP));

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
        commentTa.add(new AutosizeBehavior());
        add(commentTa, new SimpleFormComponentLabel("commentLabel", commentTa));

        BootstrapAjaxButton submitBtn = new BootstrapAjaxButton("submit", Buttons.Type.Success) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                ParticipantForm.this.getModelObject().setInvitationStatus(InvitationStatus.ACCEPTED);
                onAcceptEvent(target);
                target.add(ParticipantForm.this);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                super.onError(target);

                target.add(feedback);
            }
        };
        submitBtn.setLabel(new ResourceModel("save", "Save"));
        add(submitBtn);

        BootstrapAjaxButton declineBtn = new BootstrapAjaxButton("decline", Buttons.Type.Default) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                ParticipantForm.this.getModelObject().setInvitationStatus(InvitationStatus.DECLINED);
                ParticipantForm.this.getModelObject().setAccommodation(new Accommodation());
                onDeclineEvent(target);
                target.add(ParticipantForm.this);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                super.onError(target);

                target.add(feedback);
            }
        };
        declineBtn.setLabel(new ResourceModel("decline", "Decline"));
        declineBtn.setDefaultFormProcessing(false);
        add(declineBtn);

        add(new BootstrapAjaxButton("acceptTentatively", Buttons.Type.Warning) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                ParticipantForm.this.getModelObject().setInvitationStatus(InvitationStatus.TENTATIVE);
                onAcceptTentatively(target);
                target.add(ParticipantForm.this);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                super.onError(target);

                target.add(feedback);
            }
        }.setLabel(new ResourceModel("tentative", "Tentative")));
    }

    private static TempusDominusConfig createTempusDominusConfig(Event event) {
        return new TempusDominusConfig()
            .withUseCurrent(false)
            .withStepping(30)
            .withViewDate(new Date(event.getStartDate().getTime()))
            .withDisplay(display -> display
                .withSideBySide(true))
            .withRestrictions(restrictions -> restrictions
                .withMinDate(DateUtils.atStartOfDay(event.getStartDate()))
                .withMaxDate(DateUtils.atEndOfDay(event.getEndDate())));
    }

    protected abstract void onAcceptEvent(AjaxRequestTarget target);

    protected abstract void onDeclineEvent(AjaxRequestTarget target);

    protected abstract void onAcceptTentatively(AjaxRequestTarget target);

    protected FeedbackPanel feedback(String wicketId) {
        return new NotificationPanel(wicketId, this);
    }

    @Override
    protected IMarkupSourcingStrategy newMarkupSourcingStrategy() {
        return new PanelMarkupSourcingStrategy(TAG_NAME, false);
    }
}
