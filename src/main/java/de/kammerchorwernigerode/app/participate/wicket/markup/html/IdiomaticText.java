package de.kammerchorwernigerode.app.participate.wicket.markup.html;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class IdiomaticText extends Panel {

    public static final String CONTENT_WICKET_ID = "i";

    public IdiomaticText(String id) {
        this(id, null);
    }

    public IdiomaticText(String id, IModel<?> model) {
        super(id, model);
    }

    public String getContentId() {
        return CONTENT_WICKET_ID;
    }
}
