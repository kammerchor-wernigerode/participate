package de.kammerchorwernigerode.app.participate.person.presentation.components.form;

import de.kammerchorwernigerode.app.participate.person.infrastructure.PersonRecordRepository;
import de.kammerchorwernigerode.app.participate.person.presentation.components.UniqueEmailAddressValidator;
import de.kammerchorwernigerode.app.participate.person.presentation.components.UniqueFileNameValidator;
import de.kammerchorwernigerode.app.participate.person.presentation.model.PersonDto;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.form.BootstrapForm;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.form.ErrorMessageFilter;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.panel.Feedback;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.util.Attributes;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.feedback.FeedbackMessages;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

public abstract class BasePersonForm extends GenericPanel<PersonDto> {

    @SpringBean
    private PersonRecordRepository personRecordRepository;

    private final Form form;

    public BasePersonForm(String id, IModel<PersonDto> model) {
        super(id, model);
        this.form = new Form("form", model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        IModel<PersonDto> model = getModel();


        // First name
        IModel<String> firstNameModel = LambdaModel.of(model, PersonDto::getFirstName, PersonDto::setFirstName);
        TextField<String> firstNameInput = new TextField<>("firstName", firstNameModel) {

            private final AttributeAppender valid = ClassAttributeModifier.append("class", "is-valid");
            private final AttributeAppender invalid = ClassAttributeModifier.append("class", "is-invalid");

            @Override
            protected void onValid() {
                removeIfPresent(valid, invalid);
                updateFeedbackPanel("firstNameFeedback");
            }

            @Override
            protected void onInvalid() {
                FeedbackMessages messages = getFeedbackMessages();
                ErrorMessageFilter filter = new ErrorMessageFilter(this);

                if (messages.hasMessage(filter)) {
                    removeIfPresent(valid);
                    addIfMissing(invalid);
                } else {
                    removeIfPresent(invalid);
                    addIfMissing(valid);
                }

                updateFeedbackPanel("firstNameFeedback");
            }

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);

                Attributes.addClass(tag, "form-control");
            }

            private void updateFeedbackPanel(String feedbackWicketId) {
                WebMarkupContainer form = getForm();
                Component feedback = form.get(feedbackWicketId);
                add(AttributeModifier.replace("aria-describedby", feedback.getMarkupId()));
                RequestCycle.get().find(AjaxRequestTarget.class).ifPresent(target -> target.add(feedback));
            }

            private void removeIfPresent(Behavior... behaviors) {
                for (Behavior behavior : behaviors) {
                    if (hasBehavior(behavior)) {
                        remove(behavior);
                    }
                }
            }

            private void addIfMissing(Behavior... behaviors) {
                for (Behavior behavior : behaviors) {
                    if (!hasBehavior(behavior)) {
                        add(behavior);
                    }
                }
            }

            private boolean hasBehavior(Behavior behavior) {
                List<? extends Behavior> behaviors = getBehaviors();
                return behaviors.contains(behavior);
            }
        };
        firstNameInput.setRequired(true);
        firstNameInput.setLabel(new ResourceModel("person.firstName"));
        form.add(firstNameInput);

        SimpleFormComponentLabel firstNameLabel = new SimpleFormComponentLabel("firstNameLabel", firstNameInput);
        form.add(firstNameLabel);

        Feedback firstNameFeedback = new Feedback("firstNameFeedback", firstNameInput);
        form.add(firstNameFeedback);


        // Last name
        IModel<String> lastNameModel = LambdaModel.of(model, PersonDto::getLastName, PersonDto::setLastName);
        TextField<String> lastNameInput = new TextField<>("lastName", lastNameModel) {

            private final AttributeAppender valid = ClassAttributeModifier.append("class", "is-valid");
            private final AttributeAppender invalid = ClassAttributeModifier.append("class", "is-invalid");

            @Override
            protected void onValid() {
                removeIfPresent(valid, invalid);
                updateFeedbackPanel("lastNameFeedback");
            }

            @Override
            protected void onInvalid() {
                FeedbackMessages messages = getFeedbackMessages();
                ErrorMessageFilter filter = new ErrorMessageFilter(this);

                if (messages.hasMessage(filter)) {
                    removeIfPresent(valid);
                    addIfMissing(invalid);
                } else {
                    removeIfPresent(invalid);
                    addIfMissing(valid);
                }

                updateFeedbackPanel("lastNameFeedback");
            }

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);

                Attributes.addClass(tag, "form-control");
            }

            private void updateFeedbackPanel(String feedbackWicketId) {
                WebMarkupContainer form = getForm();
                Component feedback = form.get(feedbackWicketId);
                add(AttributeModifier.replace("aria-describedby", feedback.getMarkupId()));
                RequestCycle.get().find(AjaxRequestTarget.class).ifPresent(target -> target.add(feedback));
            }

            private void removeIfPresent(Behavior... behaviors) {
                for (Behavior behavior : behaviors) {
                    if (hasBehavior(behavior)) {
                        remove(behavior);
                    }
                }
            }

            private void addIfMissing(Behavior... behaviors) {
                for (Behavior behavior : behaviors) {
                    if (!hasBehavior(behavior)) {
                        add(behavior);
                    }
                }
            }

            private boolean hasBehavior(Behavior behavior) {
                List<? extends Behavior> behaviors = getBehaviors();
                return behaviors.contains(behavior);
            }
        };
        lastNameInput.setRequired(true);
        lastNameInput.setLabel(new ResourceModel("person.lastName"));
        form.add(lastNameInput);

        SimpleFormComponentLabel lastNameLabel = new SimpleFormComponentLabel("lastNameLabel", lastNameInput);
        form.add(lastNameLabel);

        Feedback lastNameFeedback = new Feedback("lastNameFeedback", lastNameInput);
        form.add(lastNameFeedback);


        // File name
        IModel<String> fileNameModel = LambdaModel.of(model, PersonDto::getFileName, PersonDto::setFileName);
        TextField<String> fileNameInput = new TextField<>("fileName", fileNameModel) {

            private final AttributeAppender valid = ClassAttributeModifier.append("class", "is-valid");
            private final AttributeAppender invalid = ClassAttributeModifier.append("class", "is-invalid");

            @Override
            protected void onValid() {
                removeIfPresent(valid, invalid);
                updateFeedbackPanel("fileNameFeedback");
            }

            @Override
            protected void onInvalid() {
                FeedbackMessages messages = getFeedbackMessages();
                ErrorMessageFilter filter = new ErrorMessageFilter(this);

                if (messages.hasMessage(filter)) {
                    removeIfPresent(valid);
                    addIfMissing(invalid);
                } else {
                    removeIfPresent(invalid);
                    addIfMissing(valid);
                }

                updateFeedbackPanel("fileNameFeedback");
            }

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);

                Attributes.addClass(tag, "form-control");
            }

            private void updateFeedbackPanel(String feedbackWicketId) {
                WebMarkupContainer form = getForm();
                Component feedback = form.get(feedbackWicketId);
                add(AttributeModifier.replace("aria-describedby", feedback.getMarkupId()));
                RequestCycle.get().find(AjaxRequestTarget.class).ifPresent(target -> target.add(feedback));
            }

            private void removeIfPresent(Behavior... behaviors) {
                for (Behavior behavior : behaviors) {
                    if (hasBehavior(behavior)) {
                        remove(behavior);
                    }
                }
            }

            private void addIfMissing(Behavior... behaviors) {
                for (Behavior behavior : behaviors) {
                    if (!hasBehavior(behavior)) {
                        add(behavior);
                    }
                }
            }

            private boolean hasBehavior(Behavior behavior) {
                List<? extends Behavior> behaviors = getBehaviors();
                return behaviors.contains(behavior);
            }
        };
        fileNameInput.add(new UniqueFileNameValidator(personRecordRepository));
        fileNameInput.setLabel(new ResourceModel("person.fileName"));
        form.add(fileNameInput);

        SimpleFormComponentLabel fileNameLabel = new SimpleFormComponentLabel("fileNameLabel", fileNameInput);
        form.add(fileNameLabel);

        Feedback fileNameFeedback = new Feedback("fileNameFeedback", fileNameInput);
        form.add(fileNameFeedback);


        // Email address
        IModel<String> emailAddressModel = LambdaModel.of(model,
            PersonDto::getEmailAddress, PersonDto::setEmailAddress);
        EmailTextField emailAddressInput = new EmailTextField("emailAddress", emailAddressModel) {

            private final AttributeAppender valid = ClassAttributeModifier.append("class", "is-valid");
            private final AttributeAppender invalid = ClassAttributeModifier.append("class", "is-invalid");

            @Override
            protected void onValid() {
                removeIfPresent(valid, invalid);
                updateFeedbackPanel("emailAddressFeedback");
            }

            @Override
            protected void onInvalid() {
                FeedbackMessages messages = getFeedbackMessages();
                ErrorMessageFilter filter = new ErrorMessageFilter(this);

                if (messages.hasMessage(filter)) {
                    removeIfPresent(valid);
                    addIfMissing(invalid);
                } else {
                    removeIfPresent(invalid);
                    addIfMissing(valid);
                }

                updateFeedbackPanel("emailAddressFeedback");
            }

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);

                Attributes.addClass(tag, "form-control");
            }

            private void updateFeedbackPanel(String feedbackWicketId) {
                WebMarkupContainer form = getForm();
                Component feedback = form.get(feedbackWicketId);
                add(AttributeModifier.replace("aria-describedby", feedback.getMarkupId()));
                RequestCycle.get().find(AjaxRequestTarget.class).ifPresent(target -> target.add(feedback));
            }

            private void removeIfPresent(Behavior... behaviors) {
                for (Behavior behavior : behaviors) {
                    if (hasBehavior(behavior)) {
                        remove(behavior);
                    }
                }
            }

            private void addIfMissing(Behavior... behaviors) {
                for (Behavior behavior : behaviors) {
                    if (!hasBehavior(behavior)) {
                        add(behavior);
                    }
                }
            }

            private boolean hasBehavior(Behavior behavior) {
                List<? extends Behavior> behaviors = getBehaviors();
                return behaviors.contains(behavior);
            }
        };
        emailAddressInput.setRequired(true);
        emailAddressInput.add(new UniqueEmailAddressValidator(personRecordRepository));
        emailAddressInput.setLabel(new ResourceModel("person.emailAddress"));
        form.add(emailAddressInput);

        SimpleFormComponentLabel emailAddressLabel =
            new SimpleFormComponentLabel("emailAddressLabel", emailAddressInput);
        form.add(emailAddressLabel);

        Feedback emailAddressFeedback = new Feedback("emailAddressFeedback", emailAddressInput);
        form.add(emailAddressFeedback);


        add(form);
    }

    protected abstract void onSubmit();


    private class Form extends BootstrapForm<PersonDto> {

        public Form(String id, IModel<PersonDto> model) {
            super(id, model);
        }

        @Override
        protected void onSubmit() {
            BasePersonForm.this.onSubmit();
        }
    }
}
