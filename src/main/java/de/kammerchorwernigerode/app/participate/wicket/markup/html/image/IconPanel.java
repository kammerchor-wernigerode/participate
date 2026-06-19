package de.kammerchorwernigerode.app.participate.wicket.markup.html.image;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.IdiomaticText;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class IconPanel extends IdiomaticText implements IGenericComponent<IconType, IconPanel> {

    public IconPanel(String id, IconType type) {
        this(id, Model.of(type));
    }

    public IconPanel(String id, IModel<IconType> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Icon icon = new Icon(getContentId(), getModelObject());
        add(icon);
    }
}
