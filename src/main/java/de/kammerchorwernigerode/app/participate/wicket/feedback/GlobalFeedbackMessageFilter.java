package de.kammerchorwernigerode.app.participate.wicket.feedback;

import org.apache.wicket.Component;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.feedback.IFeedbackMessageFilter;

public class GlobalFeedbackMessageFilter implements IFeedbackMessageFilter {

    @Override
    public boolean accept(FeedbackMessage message) {
        Component reporter = message.getReporter();

        if (null == reporter) {
            return true;
        }

        return null == reporter.findParent(IFeedback.class);
    }
}
