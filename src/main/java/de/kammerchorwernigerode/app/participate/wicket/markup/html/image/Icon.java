package de.kammerchorwernigerode.app.participate.wicket.markup.html.image;

import org.apache.wicket.IGenericComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;

public class Icon extends WebMarkupContainer implements IGenericComponent<IconType, Icon> {

    private final IconBehavior iconBehavior;

    public Icon(String id, IconType type) {
        super(id, Model.of(type));
        this.iconBehavior = new IconBehavior(getModel());
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(iconBehavior);
    }

    public Icon setType(IconType type) {
        iconBehavior.setType(type);
        return this;
    }
}
