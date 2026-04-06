package de.kammerchorwernigerode.app.participate.wicket.markup.html;

import org.apache.wicket.markup.html.panel.Panel;

public class Anchor extends Panel {

    public static final String LINK_WICKET_ID = "anchor";

    public Anchor(String id) {
        super(id);
    }

    public String getLinkId() {
        return LINK_WICKET_ID;
    }
}
