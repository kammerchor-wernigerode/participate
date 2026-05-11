package de.kammerchorwernigerode.app.participate.wicket.markup.html;

import org.apache.wicket.markup.html.panel.Panel;

public class ContentSpan extends Panel {

    public static final String CONTENT_WICKET_ID = "span";

    public ContentSpan(String id) {
        super(id);
    }

    public String getContentId() {
        return CONTENT_WICKET_ID;
    }
}
