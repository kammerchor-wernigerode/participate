package de.vinado.wicket.participate.components.panels;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import de.vinado.wicket.bt4.tooltip.TooltipBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public abstract class BootstrapAjaxLinkPanel extends Panel {

    private BootstrapAjaxLink<Void> ajaxLink;

    private Buttons.Type type;
    private Buttons.Size size;
    private IModel<String> labelModel;
    private IModel<String> titleModel;
    private IconType icon;

    public BootstrapAjaxLinkPanel(String id, Buttons.Type type, IModel<String> labelModel) {
        this(id, type, Buttons.Size.Small, labelModel, null, null);
    }

    public BootstrapAjaxLinkPanel(String id, Buttons.Type type, Buttons.Size size, IModel<String> labelModel) {
        this(id, type, size, labelModel, null, null);
    }

    public BootstrapAjaxLinkPanel(String id, Buttons.Type type, IModel<String> labelModel, IconType icon) {
        this(id, type, Buttons.Size.Small, labelModel, icon, null);
    }

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
        this.labelModel = labelModel;
        this.icon = icon;
        this.titleModel = titleModel;

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

    public IModel<String> getLabelModel() {
        return labelModel;
    }

    public void setLabelModel(IModel<String> labelModel) {
        this.labelModel = labelModel;
    }

    public IModel<String> getTitleModel() {
        return titleModel;
    }

    public void setTitleModel(IModel<String> titleModel) {
        this.titleModel = titleModel;
    }

    public IconType getIcon() {
        return icon;
    }

    public void setIcon(IconType icon) {
        this.icon = icon;
    }
}
