package de.kammerchorwernigerode.app.participate.wicket.bootstrap.button;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.image.Icon;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.image.IconType;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.IMarkupSourcingStrategy;
import org.apache.wicket.markup.html.panel.PanelMarkupSourcingStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class BootstrapBookmarkablePageLink<T> extends BookmarkablePageLink<T> {

    private final Icon icon;
    private final Label label;

    public <P extends Page> BootstrapBookmarkablePageLink(String id, Class<P> pageClass) {
        super(id, pageClass);
        this.icon = new Icon("icon", null);
        this.label = new Label("label", Model.of());
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(icon);

        label.setRenderBodyOnly(true);
        add(label);
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
