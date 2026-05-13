package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button.Buttons.Size;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button.Buttons.Variant;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.image.Icon;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.image.IconType;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.IMarkupSourcingStrategy;
import org.apache.wicket.markup.html.panel.PanelMarkupSourcingStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class BootstrapBookmarkablePageLink<T> extends BookmarkablePageLink<T>
    implements BootstrapButton<BootstrapBookmarkablePageLink<T>> {

    private final Icon icon;
    private final Label label;
    private final ButtonBehavior buttonBehavior = new ButtonBehavior();

    public <P extends Page> BootstrapBookmarkablePageLink(String id, Class<P> pageClass) {
        this(id, pageClass, null);
    }

    public <P extends Page> BootstrapBookmarkablePageLink(String id, Class<P> pageClass, PageParameters parameters) {
        super(id, pageClass, parameters);
        this.icon = new Icon("icon", (IconType) null);
        this.label = new Label("label", Model.of());
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(icon);

        label.setRenderBodyOnly(true);
        add(label);

        add(buttonBehavior);
    }

    @Override
    public BootstrapBookmarkablePageLink<T> setVariant(Variant variant) {
        buttonBehavior.setVariant(variant);
        return this;
    }

    @Override
    public BootstrapBookmarkablePageLink<T> setSize(Size size) {
        buttonBehavior.setSize(size);
        return this;
    }

    public BootstrapBookmarkablePageLink<T> setIcon(IconType icon) {
        this.icon.setType(icon);
        return this;
    }

    @Override
    public BootstrapBookmarkablePageLink<T> setBody(IModel<?> bodyModel) {
        IModel<?> model = wrap(bodyModel);
        label.setDefaultModel(model);
        return this;
    }

    @Override
    public IModel<?> getBody() {
        return label.getDefaultModel();
    }

    @Override
    protected IMarkupSourcingStrategy newMarkupSourcingStrategy() {
        return new PanelMarkupSourcingStrategy(true);
    }
}
