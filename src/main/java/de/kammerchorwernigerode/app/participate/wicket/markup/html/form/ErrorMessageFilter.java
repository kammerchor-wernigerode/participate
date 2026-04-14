package de.kammerchorwernigerode.app.participate.wicket.markup.html.form;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.FormComponent;

import java.util.Objects;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ErrorMessageFilter implements IFeedbackMessageFilter {

    private final FormComponent<?> reporter;

    @Override
    public boolean accept(FeedbackMessage message) {
        return Objects.equals(reporter, message.getReporter())
            && FeedbackMessage.ERROR == message.getLevel();
    }
}
