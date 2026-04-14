package de.kammerchorwernigerode.app.participate.wicket.markup.html.panel;

import org.apache.wicket.feedback.FeedbackMessage;

import java.util.Objects;

class DedupedFeedbackMessageAdapter extends FeedbackMessage {

    public DedupedFeedbackMessageAdapter(FeedbackMessage subject) {
        super(subject.getReporter(), subject.getMessage(), subject.getLevel());
    }

    // @checkstyle:off: NeedBraces
    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) return false;
        DedupedFeedbackMessageAdapter that = (DedupedFeedbackMessageAdapter) obj;
        return this.getLevel() == that.getLevel()
            && this.isRendered() == that.isRendered()
            && Objects.equals(this.getMessage(), that.getMessage())
            && Objects.equals(this.getReporter(), that.getReporter());
    }
    // @checkstyle:on: NeedBraces

    @Override
    public int hashCode() {
        int result = getLevel();
        result = 31 * result + Objects.hashCode(getMessage());
        result = 31 * result + Objects.hashCode(getReporter());
        result = 31 * result + Boolean.hashCode(isRendered());
        return result;
    }
}
