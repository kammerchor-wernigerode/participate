package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.image;

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class IconBehavior extends org.apache.wicket.behavior.Behavior {

    private final IModel<IconType> type;
    private final IModel<String> value;

    public IconBehavior(IModel<IconType> type) {
        this.type = type;
        this.value = Model.of("");
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        super.onComponentTag(component, tag);

        tag.put("class", value.getObject());
    }

    @Override
    public void onConfigure(Component component) {
        super.onConfigure(component);

        if (hasIconType()) {
            value.setObject(type.getObject().cssClassName());
        } else {
            value.setObject("");
            component.setVisible(false);
        }
    }

    public boolean hasIconType() {
        return null != type && null != type.getObject();
    }

    public void setType(IconType type) {
        this.type.setObject(type);
    }

    @Override
    public void detach(Component component) {
        super.detach(component);
        type.detach();
        value.detach();
    }
}
