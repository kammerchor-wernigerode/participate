package de.vinado.wicket.participate.components.panels;

import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.panel.PanelBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.panel.PanelType;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.AbstractItem;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.Response;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class BootstrapPanel<T> extends GenericPanel<T> {

    private static final String _PANEL_TITLE_ID = "panelTitle";
    private static final String _PANEL_BODY_ID = "panelBody";
    private static final String _PANEL_FOOTER_ID = "panelFooter";
    private static final String _DEFAULT_BUTTON_ID = "defaultBtn";
    private static final String _BUTTON_GROUP_ID = "btnGroup";
    private static final String _DROP_DOWN_MENU_ID = "dropDownMenu";
    private static final String _DEFAULT_BUTTON_ICON_ID = "icon";

    private PanelBehavior panelBehavior;
    private final IModel<String> titleModel;

    private final Panel panelBody;
    private AbstractLink defaultBtn;
    private IconType defaultBtnIcon;
    private IModel<String> defaultBtnLabelModel;


    public BootstrapPanel(final String id) {
        this(id, null, null);
    }

    public BootstrapPanel(final String id, final IModel<T> model, final IModel<String> titleModel) {
        super(id, model);

        this.titleModel = titleModel;

        add(this.panelBehavior = new PanelBehavior(PanelType.Default));

        final Label panelTitle = newTitleLabel(_PANEL_TITLE_ID, getModel(), getTitleModel());
        final WebMarkupContainer btnGroup = newBtnGroup(_BUTTON_GROUP_ID, getModel());
        defaultBtn = newDefaultBtn(_DEFAULT_BUTTON_ID, getModel());
        final RepeatingView dropDownMenu = newDropDownMenu(_DROP_DOWN_MENU_ID, getModel());
        panelBody = newBodyPanel(_PANEL_BODY_ID, getModel());
        final Panel panelFooter = newFooterPanel(_PANEL_FOOTER_ID, getModel());

        add(panelTitle);
        add(btnGroup);
        if (null == defaultBtn) {
            defaultBtn = new ExternalLink(_DEFAULT_BUTTON_ID, "#");
        } else {
            final String defaultBtnLabel = null != getDefaultBtnLabelModel() ? getDefaultBtnLabelModel().getObject() : "Label not set!";
            Panel iconPanel;
            if (null != getDefaultBtnIcon()) {
                iconPanel = new IconPanel(_DEFAULT_BUTTON_ICON_ID, getDefaultBtnIcon());
            } else {
                iconPanel = new EmptyPanel(_DEFAULT_BUTTON_ICON_ID);
            }
            iconPanel.add(new Behavior() {
                @Override
                public void afterRender(final Component component) {
                    final Response r = component.getResponse();
                    r.write(defaultBtnLabel);
                }
            });
            defaultBtn.add(iconPanel);
        }
        btnGroup.add(defaultBtn);
        btnGroup.add(dropDownMenu);
        add(panelBody);
        add(panelFooter);
    }

    public PanelType getPanelType() {
        return this.panelBehavior.getType();
    }

    public BootstrapPanel<T> setPanelType(final PanelType panelType) {
        this.panelBehavior.setType(panelType);
        return this;
    }

    public IModel<String> getTitleModel() {
        return titleModel;
    }

    public IconType getDefaultBtnIcon() {
        return defaultBtnIcon;
    }

    public void setDefaultBtnIcon(final IconType defaultBtnIcon) {
        this.defaultBtnIcon = defaultBtnIcon;
    }

    public IModel<String> getDefaultBtnLabelModel() {
        return defaultBtnLabelModel;
    }

    public void setDefaultBtnLabelModel(final IModel<String> defaultBtnLabelModel) {
        this.defaultBtnLabelModel = defaultBtnLabelModel;
    }

    protected Label newTitleLabel(final String id, final IModel<T> model, final IModel<String> titleModel) {
        return new Label(id, titleModel);
    }

    protected AbstractLink newDefaultBtn(final String id, final IModel<T> model) {
        return null;
    }

    private WebMarkupContainer newBtnGroup(final String id, final IModel<T> model) {
        final WebMarkupContainer btnGroup = new WebMarkupContainer(id, model);
        btnGroup.setOutputMarkupPlaceholderTag(true);
        btnGroup.setVisible(null != newDefaultBtn(_DEFAULT_BUTTON_ID, model));
        return btnGroup;
    }

    protected RepeatingView newDropDownMenu(final String id, final IModel<T> model) {
        final RepeatingView dropDownMenu = new RepeatingView(id, model) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                if (size() <= 0) {
                    defaultBtn.add(new AttributeModifier("style",
                            "border-bottom-right-radius: 3px; border-top-right-radius: 3px;"));
                    setVisible(false);
                }
            }
        };
        dropDownMenu.setOutputMarkupPlaceholderTag(true);
        return dropDownMenu;
    }

    protected Panel newBodyPanel(final String id, final IModel<T> model) {
        final Panel emptyPanel = new EmptyPanel(id);
        emptyPanel.setDefaultModel(null);
        return emptyPanel;
    }

    public Panel getPanelBody() {
        return panelBody;
    }

    protected Panel newFooterPanel(final String id, final IModel<T> model) {
        final Panel emptyPanel = new EmptyPanel(id);
        emptyPanel.setDefaultModel(null);
        emptyPanel.setVisible(false);
        return emptyPanel;
    }

    protected abstract class DropDownItem extends AbstractItem {

        private static final String _LINK_ID = "link";
        private static final String _LABEL_ID = "label";
        private static final String _ICON_ID = "icon";

        public DropDownItem(final String id, final IModel<String> labelModel, final IconType icon) {
            super(id);

            add(new AjaxLink(_LINK_ID) {
                @Override
                public void onClick(final AjaxRequestTarget target) {
                    DropDownItem.this.onClick(target);
                }
            }
                    .add(new Label(_LABEL_ID, labelModel))
                    .add(null != icon ? new IconPanel(_ICON_ID, icon) : new EmptyPanel(_ICON_ID)));
        }

        protected abstract void onClick(final AjaxRequestTarget target);
    }
}
