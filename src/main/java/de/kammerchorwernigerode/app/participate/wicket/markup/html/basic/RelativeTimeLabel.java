package de.kammerchorwernigerode.app.participate.wicket.markup.html.basic;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

public class RelativeTimeLabel extends Label {

    public RelativeTimeLabel(String id, IModel<?> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new RelativeTimeBehavior());
    }

    @Override
    public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
        replaceComponentTagBody(markupStream, openTag, "&nbsp;");
    }
}
