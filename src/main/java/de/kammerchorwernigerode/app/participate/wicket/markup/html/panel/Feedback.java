package de.kammerchorwernigerode.app.participate.wicket.markup.html.panel;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.FeedbackMessagesModel;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Feedback extends Panel implements IFeedback {

    private final Messages messages;

    public Feedback(String id, FormComponent<?> formComponent) {
        super(id);

        FeedbackMessagesModel model = new FeedbackMessagesModel(this);
        model.setFilter(message -> Objects.equals(formComponent, message.getReporter()));
        this.messages = new Messages("messages", model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        setOutputMarkupPlaceholderTag(true);

        messages.setVersioned(false);
        add(messages);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        if (!hasAnyMessage()) {
            setVisible(false);
            return;
        }

        FeedbackMessage feedback = getCurrentMessages().get(0);
        String cssClass = switch (feedback.getLevel()) {
            case FeedbackMessage.INFO, FeedbackMessage.SUCCESS -> "valid-feedback";
            case FeedbackMessage.ERROR, FeedbackMessage.FATAL -> "invalid-feedback";
            default -> "form-text";
        };

        add(AttributeModifier.replace("class", cssClass));
        setVisible(true);
    }

    @Override
    public boolean isVersioned() {
        return false;
    }

    public final boolean hasAnyMessage() {
        return hasAnyMessage(FeedbackMessage.UNDEFINED);
    }

    public final boolean hasAnyMessage(int level) {
        return getCurrentMessages().stream()
            .anyMatch(message -> message.isLevel(level));
    }

    protected final List<FeedbackMessage> getCurrentMessages() {
        List<? extends FeedbackMessage> messages = this.messages.getModelObject();
        return Collections.unmodifiableList(messages);
    }


    private class Messages extends ListView<FeedbackMessage> {

        public Messages(String id, IModel<? extends List<FeedbackMessage>> model) {
            super(id, model);
        }

        @Override
        protected IModel<FeedbackMessage> getListItemModel(IModel<? extends List<FeedbackMessage>> listViewModel,
                                                           int index) {
            return () -> {
                List<DedupedFeedbackMessageAdapter> messages = listViewModel.getObject().stream()
                    .map(DedupedFeedbackMessageAdapter::new)
                    .distinct()
                    .toList();
                return index >= messages.size() ? null : messages.get(index);
            };
        }

        @Override
        protected void populateItem(ListItem<FeedbackMessage> item) {
            FeedbackMessage feedback = item.getModelObject();
            if (null == feedback) {
                item.add(new EmptyPanel("message"));
                return;
            }

            feedback.markRendered();
            Serializable message = feedback.getMessage();
            Label label = new Label("message", message);
            label.setEscapeModelStrings(Feedback.this.getEscapeModelStrings());
            item.add(label);
        }
    }
}
