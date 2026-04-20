package de.kammerchorwernigerode.app.participate.wicket.markup.html;

import org.apache.wicket.markup.html.panel.Panel;

public class ContentDivision extends Panel {

    public static final String CONTENT_WICKET_ID = "div";

    public ContentDivision(String id) {
        super(id);
    }

    public String getChildId() {
        return CONTENT_WICKET_ID;
    }
}
