package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.toast;

import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;

import lombok.RequiredArgsConstructor;

public class ToastContainer extends FeedbackPanel {

    public ToastContainer(String id, IFeedbackMessageFilter filter) {
        super(id, filter);
    }

    @Override
    protected Component newMessageDisplayComponent(String id, FeedbackMessage message) {
        Toast toast = new Toast(id, new FeedbackMessageModel(message));
        toast.add(ClassAttributeModifier.append("class", getCSSClass(message)));
        return toast;
    }


    @RequiredArgsConstructor
    private static class FeedbackMessageModel implements IModel<FeedbackMessage> {

        private final FeedbackMessage message;

        @Override
        public FeedbackMessage getObject() {
            return message;
        }
    }
}
