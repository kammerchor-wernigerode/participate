package de.kammerchorwernigerode.app.participate.wicket.markup.html.image;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.IdiomaticText;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class Icon extends WebMarkupContainer implements IGenericComponent<IconType, Icon> {

    private final IconBehavior iconBehavior;

    public Icon(String id, IconType type) {
        this(id, Model.of(type));
    }

    public Icon(String id, IModel<IconType> model) {
        super(id, model);
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


    public static class Panel extends IdiomaticText implements IGenericComponent<IconType, Panel> {

        public Panel(String id, IconType type) {
            this(id, Model.of(type));
        }

        public Panel(String id, IModel<IconType> model) {
            super(id, model);
        }

        @Override
        protected void onInitialize() {
            super.onInitialize();

            Icon icon = new Icon(getContentId(), getModelObject());
            add(icon);
        }
    }
}
