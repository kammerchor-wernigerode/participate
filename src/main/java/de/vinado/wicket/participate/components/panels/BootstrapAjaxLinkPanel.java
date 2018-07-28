package de.vinado.wicket.participate.components.panels;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipConfig;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.time.Duration;

/**
 * Bootstrap Ajax Button Panel. Use it in {@link org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable
 * DataTabels} and provide awesome click action to the cell.
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class BootstrapAjaxLinkPanel extends Panel {

    private BootstrapAjaxLink ajaxLink;

    private Buttons.Type type;
    private Buttons.Size size;
    private IModel<String> labelModel;
    private IModel<String> titleModel;
    private IconType icon;

    /**
     * Construct.
     *
     * @param id         Wicket ID
     * @param type       {@link Buttons.Type}
     * @param labelModel Label
     */
    public BootstrapAjaxLinkPanel(final String id, final Buttons.Type type, final IModel<String> labelModel) {
        this(id, type, Buttons.Size.Small, labelModel, null, null);
    }

    /**
     * Construct.
     *
     * @param id         Wicket ID
     * @param type       {@link Buttons.Type}
     * @param size       {@link Buttons.Size}
     * @param labelModel Label
     */
    public BootstrapAjaxLinkPanel(final String id, final Buttons.Type type, final Buttons.Size size, final IModel<String> labelModel) {
        this(id, type, size, labelModel, null, null);
    }

    /**
     * Construct.
     *
     * @param id         Wicket ID
     * @param type       {@link Buttons.Type}
     * @param labelModel Label
     * @param icon       {@link IconType}
     */
    public BootstrapAjaxLinkPanel(final String id, final Buttons.Type type, final IModel<String> labelModel, final IconType icon) {
        this(id, type, Buttons.Size.Small, labelModel, icon, null);
    }

    /**
     * Construct.
     *
     * @param id   Wicket ID
     * @param type {@link Buttons.Type}
     * @param icon {@link IconType}
     */
    public BootstrapAjaxLinkPanel(final String id, final Buttons.Type type, final IconType icon) {
        this(id, type, Buttons.Size.Mini, null, icon, null);
    }

    /**
     * Construct.
     *
     * @param id         Wicket ID
     * @param type       {@link Buttons.Type}
     * @param icon       {@link IconType}
     * @param titleModel Label
     */
    public BootstrapAjaxLinkPanel(final String id, final Buttons.Type type, final IconType icon, final IModel<String> titleModel) {
        this(id, type, Buttons.Size.Mini, null, icon, titleModel);
    }

    /**
     * Construct.
     *
     * @param id         Wicket ID
     * @param type       {@link Buttons.Type}
     * @param size       {@link Buttons.Size}
     * @param labelModel Label
     * @param icon       {@link IconType}
     * @param titleModel Title for {@link TooltipBehavior Tooltip}
     */
    public BootstrapAjaxLinkPanel(final String id, final Buttons.Type type, final Buttons.Size size, final IModel<String> labelModel, final IconType icon, final IModel<String> titleModel) {
        super(id, labelModel);
        this.type = type;
        this.size = size;
        this.labelModel = labelModel;
        this.icon = icon;
        this.titleModel = titleModel;

        ajaxLink = new BootstrapAjaxLink("link", type) {
            @Override
            public void onClick(final AjaxRequestTarget target) {
                BootstrapAjaxLinkPanel.this.onClick(target);
            }
        };

        ajaxLink.setSize(size);
        if (null != icon) ajaxLink.setIconType(icon);
        if (null != labelModel) ajaxLink.setLabel(labelModel);
        if (null != titleModel) {
            ajaxLink.add(new TooltipBehavior(titleModel, new TooltipConfig().withDelay(Duration.milliseconds(300L))));
        }

        add(ajaxLink);
    }

    /**
     * What happens, when you click the button.
     *
     * @param target {@link AjaxRequestTarget}
     */
    public abstract void onClick(final AjaxRequestTarget target);

    public BootstrapAjaxLink getLink() {
        return ajaxLink;
    }

    public Buttons.Type getType() {
        return type;
    }

    public void setType(final Buttons.Type type) {
        this.type = type;
    }

    public Buttons.Size getSize() {
        return size;
    }

    public void setSize(final Buttons.Size size) {
        this.size = size;
    }

    public IModel<String> getLabelModel() {
        return labelModel;
    }

    public void setLabelModel(final IModel<String> labelModel) {
        this.labelModel = labelModel;
    }

    public IModel<String> getTitleModel() {
        return titleModel;
    }

    public void setTitleModel(final IModel<String> titleModel) {
        this.titleModel = titleModel;
    }

    public IconType getIcon() {
        return icon;
    }

    public void setIcon(final IconType icon) {
        this.icon = icon;
    }
}
