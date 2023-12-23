package de.vinado.wicket.participate.components.panels;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import de.vinado.app.participate.wicket.bt5.tooltip.TooltipBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public abstract class BootstrapAjaxLinkPanel extends Panel {

    private BootstrapAjaxLink<Void> ajaxLink;

    private Buttons.Type type;
    private Buttons.Size size;
    private IconType icon;

    public BootstrapAjaxLinkPanel(String id, Buttons.Type type, IconType icon) {
        this(id, type, Buttons.Size.Small, null, icon, null);
    }

    public BootstrapAjaxLinkPanel(String id, Buttons.Type type, IconType icon, IModel<String> titleModel) {
        this(id, type, Buttons.Size.Small, null, icon, titleModel);
    }

    public BootstrapAjaxLinkPanel(String id, Buttons.Type type, Buttons.Size size, IModel<String> labelModel, IconType icon, IModel<String> titleModel) {
        super(id, labelModel);
        this.type = type;
        this.size = size;
        this.icon = icon;

        ajaxLink = new BootstrapAjaxLink<>("link", type) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                BootstrapAjaxLinkPanel.this.onClick(target);
            }
        };

        ajaxLink.setSize(size);
        if (null != icon) ajaxLink.setIconType(icon);
        if (null != labelModel) ajaxLink.setLabel(labelModel);
        if (null != titleModel) {
            ajaxLink.add(new TooltipBehavior(titleModel));
        }

        add(ajaxLink);
    }

    public abstract void onClick(AjaxRequestTarget target);

    public BootstrapAjaxLink getLink() {
        return ajaxLink;
    }

    public Buttons.Type getType() {
        return type;
    }

    public void setType(Buttons.Type type) {
        this.type = type;
    }

    public Buttons.Size getSize() {
        return size;
    }

    public void setSize(Buttons.Size size) {
        this.size = size;
    }

    public IconType getIcon() {
        return icon;
    }

    public void setIcon(IconType icon) {
        this.icon = icon;
    }
}
