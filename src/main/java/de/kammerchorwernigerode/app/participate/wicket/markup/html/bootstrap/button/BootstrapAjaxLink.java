package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button.Buttons.Size;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button.Buttons.Variant;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.image.Icon;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.image.IconType;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.IMarkupSourcingStrategy;
import org.apache.wicket.markup.html.panel.PanelMarkupSourcingStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public abstract class BootstrapAjaxLink<T> extends AjaxLink<T> implements BootstrapButton<BootstrapAjaxLink<T>> {

    private final Icon icon;
    private final Label label;
    private final ButtonBehavior buttonBehavior = new ButtonBehavior();

    public BootstrapAjaxLink(String id) {
        this(id, null);
    }

    public BootstrapAjaxLink(String id, IModel<T> model) {
        super(id, model);
        this.icon = new Icon("icon", null);
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
    public BootstrapAjaxLink<T> setVariant(Variant variant) {
        buttonBehavior.setVariant(variant);
        return this;
    }

    @Override
    public BootstrapAjaxLink<T> setSize(Size size) {
        buttonBehavior.setSize(size);
        return this;
    }

    public BootstrapAjaxLink<T> setIcon(IconType icon) {
        this.icon.setType(icon);
        return this;
    }

    @Override
    public BootstrapAjaxLink<T> setBody(IModel<?> bodyModel) {
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
