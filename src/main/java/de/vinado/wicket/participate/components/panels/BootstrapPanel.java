package de.vinado.wicket.participate.components.panels;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.Icon;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.list.AbstractItem;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import org.danekja.java.util.function.serializable.SerializableFunction;

public class BootstrapPanel<T> extends GenericPanel<T> {


    private int dropdownFallbackActions = 0;
    private int dropdownActions = 0;

    private RepeatingView quickAccessActionMenu;
    private RepeatingView dropdownActionMenu;
    private RepeatingView dropdownFallbackActionMenu;

    public BootstrapPanel(String id) {
        super(id);
    }

    public BootstrapPanel(String id, IModel<T> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new CssClassNameAppender("card"));

        WebMarkupContainer header;
        add(header = cardHeader("header"));
        header.setOutputMarkupId(true);

        header.add(new Label("heading", titleModel()));
        header.add(quickAccessActionMenu = new RepeatingView("actions"));

        setupResponsiveActions(header);
    }

    protected WebMarkupContainer cardHeader(String id) {
        return new WebMarkupContainer(id);
    }

    protected IModel<?> titleModel() {
        return Model.of();
    }

    private void setupResponsiveActions(WebMarkupContainer header) {
        Component collapseButton;
        header.add(dropdownActionMenu()
            .add(collapseButton = new WebMarkupContainer("dropdown-button").setOutputMarkupId(true))
            .add(dropdownActionContainer(collapseButton)
                .add(dropdownFallbackActionMenu = new RepeatingView("dropdown-fallback"))
                .add(dropdownActionMenu = new RepeatingView("dropdown-menu"))
            ));
    }

    private WebMarkupContainer dropdownActionMenu() {
        return new WebMarkupContainer("menu-container") {
            @Override
            protected void onInitialize() {
                super.onInitialize();
                setOutputMarkupPlaceholderTag(true);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(hasAnyDropdownItems());
                hideResponsively();
            }

            private void hideResponsively() {
                if (hasNoDropdownOnlyItems()) {
                    add(new CssClassNameAppender("d-inline-block d-sm-none"));
                }
            }

            private boolean hasAnyDropdownItems() {
                return !hasNoDropdownOnlyItems() || dropdownFallbackActions != 0;
            }

            private boolean hasNoDropdownOnlyItems() {
                return dropdownActions == 0;
            }
        };
    }

    private WebMarkupContainer dropdownActionContainer(Component collapseButton) {
        return new WebMarkupContainer("dropdown-container") {
            @Override
            protected void onInitialize() {
                super.onInitialize();
                add(AttributeAppender.append("aria-labelledby", collapseButton.getMarkupId()));
            }
        };
    }

    protected void addQuickAccessAction(SerializableFunction<String, AbstractAction> constructor) {
        AbstractAction action = constructor.apply(nextQuickAccessActionId());
        quickAccessActionMenu.add(action);
        dropdownFallbackActions++;
        dropdownFallbackActionMenu.add(constructor.apply(nextDropdownFallbackActionId()));
    }

    private String nextQuickAccessActionId() {
        return quickAccessActionMenu.newChildId();
    }

    private String nextDropdownFallbackActionId() {
        return dropdownFallbackActionMenu.newChildId();
    }

    protected void addDropdownAction(SerializableFunction<String, AbstractAction> constructor) {
        AbstractAction action = constructor.apply(nextDropdownActionId());
        dropdownActions++;
        dropdownActionMenu.add(action);
    }

    private String nextDropdownActionId() {
        return dropdownActionMenu.newChildId();
    }


    public static abstract class AbstractAction extends AbstractItem {

        private final IconType icon;

        public AbstractAction(String id, IModel<String> labelModel, IconType icon) {
            super(id, labelModel);
            this.icon = icon;
        }

        @Override
        protected void onInitialize() {
            super.onInitialize();

            add(link("link")
                .add(icon())
                .add(label()));
        }

        protected abstract AbstractLink link(String id);

        private Component icon() {
            return new Icon("icon", icon);
        }

        private Component label() {
            return new Label("label", getDefaultModel())
                .setRenderBodyOnly(true);
        }
    }

    public static abstract class AjaxAction extends AbstractAction {

        public AjaxAction(String id, IModel<String> labelModel, IconType icon) {
            super(id, labelModel, icon);
        }

        public static SerializableFunction<String, AbstractAction> create(IModel<String> labelModel, IconType icon,
                                                                          SerializableConsumer<AjaxRequestTarget> clickHandler) {
            return id -> new AjaxAction(id, labelModel, icon) {
                @Override
                protected void onClick(AjaxRequestTarget target) {
                    clickHandler.accept(target);
                }
            };
        }

        protected AbstractLink link(String id) {
            return new AjaxLink<Void>(id) {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    AjaxAction.this.onClick(target);
                }
            };
        }

        protected abstract void onClick(AjaxRequestTarget target);
    }
}
