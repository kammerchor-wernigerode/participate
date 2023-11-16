package de.vinado.wicket.bt4.modal;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.BootstrapResourcesBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.ButtonBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal.Size;
import de.agilecoders.wicket.core.util.Attributes;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.list.AbstractItem;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.Optional;
import java.util.function.Consumer;

import static de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal.Size.Extra_large;
import static de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal.Size.Large;
import static de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal.Size.Small;
import static java.util.function.Predicate.not;

public class Modal<T> extends GenericPanel<T> {

    private final ModalAnchor anchor;
    private final MarkupContainer header;
    private final MarkupContainer footer;

    private final RepeatingView actions;
    private Component title;
    private Size size = Size.Default;

    public Modal(ModalAnchor anchor, IModel<T> model) {
        super(anchor.getModalId(), model);

        setOutputMarkupPlaceholderTag(true);

        this.anchor = anchor;
        this.header = createHeader("header");
        this.footer = createFooter("footer");

        footer.add(actions = new RepeatingView("actions"));
    }

    protected MarkupContainer createHeader(String id) {
        return new WebMarkupContainer(id);
    }

    protected MarkupContainer createFooter(String id) {
        return new WebMarkupContainer(id);
    }

    public Modal<T> size(Size size) {
        this.size = size;
        return this;
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        checkComponentTag(tag, "div");
        Attributes.addClass(tag, "modal", "fade");
        Attributes.set(tag, "tabindex", "-1");

        Attributes.set(tag, "role", "dialog");
        Attributes.set(tag, "aria-labelledby", getTitle().getMarkupId());
        Attributes.set(tag, "aria-hidden", "true");
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        header.add(getTitle());
        title.setOutputMarkupId(true);
        header.add(createHeaderCloseButton("close")
            .setOutputMarkupId(true));

        add(createDialog("dialog")
            .add(header, footer));

        BootstrapResourcesBehavior.addTo(this);
    }

    private Component getTitle() {
        if (title == null) {
            title = createTitle("title", "");
        }
        return title;
    }

    protected Component createTitle(String id, String label) {
        return new Label(id, label);
    }

    private Component createHeaderCloseButton(String id) {
        return new WebMarkupContainer(id);
    }

    private WebMarkupContainer createDialog(String id) {
        return new TransparentWebMarkupContainer(id) {

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);

                Attributes.removeClass(tag, Large.cssClassName(), Small.cssClassName());

                switch (size) {
                    case Large:
                        Attributes.addClass(tag, Large.cssClassName());
                        break;
                    case Small:
                        Attributes.addClass(tag, Small.cssClassName());
                        break;
                    case Extra_large:
                        Attributes.addClass(tag, Extra_large.cssClassName());
                        break;
                    default:
                }
            }
        };
    }

    public Modal<T> title(IModel<String> label) {
        getTitle().setDefaultModel(label);
        return this;
    }

    public Modal<T> addCloseButton(IModel<String> label) {
        return addAction(id -> new CloseAction(id, label));
    }

    public Modal<T> addAction(SerializableFunction<String, AbstractAction> constructor) {
        AbstractAction action = constructor.apply(actions.newChildId());
        actions.add(action);
        return this;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        if (Strings.isEmpty(getTitle().getDefaultModelObjectAsString())) {
            title.setDefaultModelObject("&nbsp;")
                .setEscapeModelStrings(false);
        }

        footer.setVisible(actions.size() > 0);
    }


    public static abstract class AbstractAction extends AbstractItem {

        public AbstractAction(String id, IModel<String> labelModel) {
            super(id, labelModel);
        }

        @Override
        protected void onInitialize() {
            super.onInitialize();

            add(link("link")
                .add(label()));
        }

        protected abstract AbstractLink link(String id);

        protected Component label() {
            return new Label("label", getDefaultModel())
                .setRenderBodyOnly(true);
        }
    }

    public static abstract class AjaxAction extends AbstractAction {

        @Getter(AccessLevel.PROTECTED)
        private final ButtonBehavior buttonBehavior;

        public AjaxAction(String id, IModel<String> labelModel) {
            super(id, labelModel);
            this.buttonBehavior = new ButtonBehavior(Type.Secondary);
        }

        public static SerializableFunction<String, AbstractAction> create(IModel<String> labelModel,
                                                                          SerializableConsumer<AjaxRequestTarget> clickHandler) {
            return create(labelModel, Type.Secondary, clickHandler);
        }

        public static SerializableFunction<String, AbstractAction> create(IModel<String> labelModel, Type type,
                                                                          SerializableConsumer<AjaxRequestTarget> clickHandler) {
            return id -> new AjaxAction(id, labelModel) {

                @Override
                protected void onClick(AjaxRequestTarget target) {
                    clickHandler.accept(target);
                }
            }.type(type);
        }

        public AjaxAction type(Type type) {
            buttonBehavior.setType(type);
            return this;
        }

        protected AbstractLink link(String id) {
            return new AjaxLink<Void>(id) {

                @Override
                protected void onInitialize() {
                    super.onInitialize();
                    add(buttonBehavior);
                }

                @Override
                public void onClick(AjaxRequestTarget target) {
                    AjaxAction.this.onClick(target);
                }
            };
        }

        protected abstract void onClick(AjaxRequestTarget target);
    }

    public final class CloseAction extends AjaxAction {

        public CloseAction(String id, IModel<String> labelModel) {
            super(id, labelModel);
        }

        @Override
        protected AbstractLink link(String id) {
            return new AjaxLink<String>(id) {

                @Override
                protected void onInitialize() {
                    super.onInitialize();
                    add(new AttributeModifier("data-dismiss", "modal"));
                    add(getButtonBehavior());
                }

                @Override
                public void onClick(AjaxRequestTarget target) {
                    CloseAction.this.onClick(target);
                }

                @Override
                protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                    super.updateAjaxAttributes(attributes);

                    Optional.ofNullable(Modal.this.anchor)
                        .or(() -> Optional.ofNullable(findParent(ModalAnchor.class)))
                        .map(Component::getMarkupId)
                        .filter(not(Strings::isEmpty))
                        .ifPresent(addCloseScript(attributes));
                }

                private Consumer<String> addCloseScript(AjaxRequestAttributes attributes) {
                    return anchorId -> {
                        AjaxCallListener listener = new AjaxCallListener();
                        listener.onBeforeSend(String.format("document.location.hash='%s';", anchorId));
                        listener.onBeforeSend(String.format("$('#%s').modal('hide');", anchorId));
                        attributes.getAjaxCallListeners().add(listener);
                    };
                }
            };
        }

        @Override
        protected void onClick(AjaxRequestTarget target) {
        }
    }
}
