package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.link;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.image.Icon;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.image.IconType;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.IMarkupSourcingStrategy;
import org.apache.wicket.markup.html.panel.PanelMarkupSourcingStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public abstract class IconAjaxLink<T> extends AjaxLink<T> {

    private final Icon icon;
    private final Label preLabel;
    private final Label postLabel;
    private final IconLinkBehavior iconLinkBehavior = new IconLinkBehavior();

    private BodyPosition bodyPosition = BodyPosition.AFTER_ICON;

    public IconAjaxLink(String id) {
        this(id, null);
    }

    public IconAjaxLink(String id, IModel<T> model) {
        super(id, model);
        this.icon = new Icon("icon", null);
        this.preLabel = new Label("preLabel", Model.of());
        this.postLabel = new Label("postLabel", Model.of());
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(icon);

        if (BodyPosition.AFTER_ICON.equals(bodyPosition)) {
            preLabel.setVisible(false);
        }
        preLabel.setRenderBodyOnly(true);
        add(preLabel);

        if (BodyPosition.BEFORE_ICON.equals(bodyPosition)) {
            postLabel.setVisible(false);
        }
        postLabel.setRenderBodyOnly(true);
        add(postLabel);

        add(iconLinkBehavior);
    }

    public IconAjaxLink<T> setIcon(IconType icon) {
        this.icon.setType(new BiDecorator(icon.cssClassName()));
        return this;
    }

    public IconAjaxLink<T> setBodyPosition(BodyPosition bodyPosition) {
        this.bodyPosition = bodyPosition;
        return this;
    }

    @Override
    public IconAjaxLink<T> setBody(IModel<?> bodyModel) {
        IModel<?> model = wrap(bodyModel);
        preLabel.setDefaultModel(model);
        postLabel.setDefaultModel(model);
        return this;
    }

    @Override
    public IModel<?> getBody() {
        return bodyPosition == BodyPosition.BEFORE_ICON
            ? preLabel.getDefaultModel()
            : postLabel.getDefaultModel();
    }

    @Override
    protected IMarkupSourcingStrategy newMarkupSourcingStrategy() {
        return new PanelMarkupSourcingStrategy(true);
    }


    public enum BodyPosition {

        BEFORE_ICON,
        AFTER_ICON,
        ;
    }

    private static class BiDecorator extends IconType {

        protected BiDecorator(String cssClassName) {
            super(cssClassName);
        }

        @Override
        public String cssClassName() {
            return getCssClassName().replaceFirst("bi\\s", "");
        }
    }
}
