package de.vinado.app.participate.wicket.bt5.modal;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.BootstrapResourcesBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.ICssClassNameProvider;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.ButtonBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.util.Attributes;
import de.agilecoders.wicket.jquery.util.Strings2;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ComponentModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class Modal extends Panel {

    public static final String CONTENT_MARKUP_ID = "content";
    public static final String ACTION_MARKUP_ID = "action";

    private final Component title;
    private final MarkupContainer header;
    private final MarkupContainer body;
    private final MarkupContainer footer;
    private final List<Component> actions = new LinkedList<>();

    private Size size = Size.DEFAULT;

    public Modal(String id) {
        this(id, new ComponentModel<>());
    }

    protected <T extends Serializable> Modal(String id, IModel<T> model) {
        super(id, model);

        this.title = title("title");
        this.header = header("header");
        this.body = body("body");
        this.footer = footer("footer");
    }

    public String getContentId() {
        return CONTENT_MARKUP_ID;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        setOutputMarkupPlaceholderTag(true);
        setVisible(false);

        header.add(title);
        title.setOutputMarkupId(true);

        Component times;
        header.add(times = times("times"));
        times.add(new CssClassNameAppender("nobusy"));
        times.setOutputMarkupId(true);

        footer.add(actions("actions"));

        WebMarkupContainer dialog;
        add(dialog = dialog("dialog"));
        dialog.add(header, body, footer);

        BootstrapResourcesBehavior.addTo(this);
        add(new ModalCloseBehavior());
    }

    protected Component title(String wicketId) {
        IModel<?> model = titleText();
        return new Label(wicketId, model);
    }

    protected IModel<?> titleText() {
        return Model.of();
    }

    protected MarkupContainer header(String wicketId) {
        return new WebMarkupContainer(wicketId);
    }

    protected MarkupContainer body(String wicketId) {
        return new WebMarkupContainer(wicketId);
    }

    protected MarkupContainer footer(String wicketId) {
        return new WebMarkupContainer(wicketId);
    }

    protected ListView<Component> actions(String wicketId) {
        return new ListView<>(wicketId, actions) {

            @Override
            protected void populateItem(ListItem<Component> item) {
                item.add(item.getModelObject());
            }
        };
    }

    protected WebMarkupContainer dialog(String id) {
        return new TransparentWebMarkupContainer(id) {

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);

                Arrays.stream(Size.values())
                    .map(Size::cssClassName)
                    .forEach(removeCssClassName(tag));

                switch (size) {
                    case LARGE:
                        Attributes.addClass(tag, Size.LARGE.cssClassName());
                        break;
                    case SMALL:
                        Attributes.addClass(tag, Size.SMALL.cssClassName());
                        break;
                    case EXTRA_LARGE:
                        Attributes.addClass(tag, Size.EXTRA_LARGE.cssClassName());
                        break;
                    default:
                }
            }

            private Consumer<String> removeCssClassName(ComponentTag tag) {
                return cssClassName -> Attributes.removeClass(tag, cssClassName);
            }
        };
    }

    protected Component times(String wicketId) {
        return new WebMarkupContainer(wicketId);
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        checkComponentTag(tag, "div");
        Attributes.addClass(tag, "modal");
        Attributes.addClass(tag, "fade");
        Attributes.set(tag, "data-bs-keyboard", "true");
        Attributes.set(tag, "data-bs-focus", "true");
        Attributes.set(tag, "data-bs-backdrop", "true");

        Attributes.set(tag, "role", "dialog");
        Attributes.set(tag, "aria-labelledby", title.getMarkupId());
        Attributes.set(tag, "aria-hidden", "true");
    }

    public Modal setHeaderVisible(boolean headerVisible) {
        this.header.setVisible(headerVisible);
        return this;
    }

    public Modal size(Size size) {
        this.size = size;
        return this;
    }

    public Modal title(IModel<String> label) {
        title.setDefaultModel(label);
        return this;
    }

    public <C extends Component> Modal content(SerializableFunction<String, C> constructor) {
        Component component = constructor.apply(CONTENT_MARKUP_ID);
        return content(component);
    }

    public Modal content(Component component) {
        if (!CONTENT_MARKUP_ID.equals(component.getId())) {
            throw new IllegalArgumentException("Invalid content markup id. Must be '" + CONTENT_MARKUP_ID + "'.");
        }

        replaceBodyWith(component);
        return this;
    }

    protected void replaceBodyWith(Component component) {
        component.setRenderBodyOnly(true);
        body.addOrReplace(component);
    }

    private void handleCloseEvent(IPartialPageRequestHandler target) {
        if (isVisible()) {
            close(target);
        }
    }

    public Modal close(IPartialPageRequestHandler target) {
        resetComponents();
        hideDialog(target);
        return appendCloseDialogJavaScript(target);
    }

    private void resetComponents() {
        actions.clear();
    }

    private void hideDialog(IPartialPageRequestHandler target) {
        setVisible(false);
        target.add(this);
    }

    private Modal appendCloseDialogJavaScript(IPartialPageRequestHandler target) {
        target.prependJavaScript(createActionScript(getMarkupId(true), "hide"));
        return this;
    }

    public Modal show(IPartialPageRequestHandler target) {
        assertContent();
        showModal(target);
        return appendShowDialogJavaScript(target);
    }

    private void assertContent() {
        if (null == body.get(CONTENT_MARKUP_ID)) {
            throw new WicketRuntimeException("Missing modal content; use modal.content(...).show(...)");
        }
    }

    private void showModal(IPartialPageRequestHandler target) {
        setVisible(true);
        target.add(this);
    }

    private Modal appendShowDialogJavaScript(IPartialPageRequestHandler target) {
        target.appendJavaScript(createActionScript(getMarkupId(true), "show"));
        return this;
    }

    private String createActionScript(String markupId, String action) {
        return "bootstrap.Modal.getInstance(document.getElementById('" + Strings2.escapeMarkupId(markupId) + "'))." + action + "();";
    }

    public Modal addSubmitAction(IModel<?> label, Form<?> form) {
        return addSubmitAction(label, form, target -> {
        });
    }

    public Modal addSubmitAction(IModel<?> label, Form<?> form, SerializableConsumer<AjaxRequestTarget> onAfterSubmit) {
        return addAction(id -> new SubmitButton(id, form, label) {

            @Override
            protected void onAfterSubmit(AjaxRequestTarget target) {
                handleCloseEvent(target);
                onAfterSubmit.accept(target);
            }
        });
    }

    public Modal addCloseAction(IModel<String> label) {
        return addAction(id -> {
            CloseButton button = new CloseButton(id, label) {

                @Override
                public void onClick(AjaxRequestTarget target) {
                    super.onClick(target);

                    handleCloseEvent(target);
                }
            };
            button.setAnchor(this);
            return button;
        });
    }

    public Modal addAction(SerializableFunction<String, AbstractLink> constructor) {
        AbstractLink button = constructor.apply(ACTION_MARKUP_ID);
        return addAction(button);
    }

    public Modal addAction(AbstractLink action) {
        if (!ACTION_MARKUP_ID.equals(action.getId())) {
            throw new IllegalArgumentException("Invalid action markup id. Must be '" + ACTION_MARKUP_ID + "'.");
        }

        actions.add(action);
        return this;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        if (Strings.isEmpty(title.getDefaultModelObjectAsString())) {
            title.setDefaultModelObject("&nbsp;");
            title.setEscapeModelStrings(false);
        }

        footer.setVisible(!actions.isEmpty());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(OnDomReadyHeaderItem.forScript(createBasicInitializerScript(getMarkupId(true))));
    }

    protected String createBasicInitializerScript(String markupId) {
        return "new bootstrap.Modal(document.getElementById('" + markupId + "'));";
    }


    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    public enum Size implements ICssClassNameProvider {

        SMALL("sm"),
        DEFAULT(""),
        LARGE("lg"),
        EXTRA_LARGE("xl"),
        ;

        private final String cssClassName;

        @Override
        public String cssClassName() {
            return "modal-" + cssClassName;
        }
    }

    @Setter
    @Accessors(chain = true)
    public static class CloseButton extends AjaxLink<String> {

        private final ButtonBehavior buttonBehavior = new ButtonBehavior(Buttons.Type.Secondary);

        private Modal anchor;

        public CloseButton(String id, IModel<String> label) {
            super(id, label);

            setBody(getDefaultModel());
        }

        @Override
        protected void onInitialize() {
            super.onInitialize();

            add(new AttributeModifier("data-bs-dismiss", "modal"));
            add(buttonBehavior);
        }

        @Override
        protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
            super.updateAjaxAttributes(attributes);

            Component _anchor = this.anchor;
            if (_anchor == null) {
                _anchor = findParent(Modal.class);
            }
            if (_anchor != null) {
                String anchorMarkupId = _anchor.getMarkupId();
                if (!Strings.isEmpty(anchorMarkupId)) {
                    AjaxCallListener listener = new AjaxCallListener();
                    listener.onBeforeSend("bootstrap.Modal.getInstance(document.getElementById('"
                        + anchorMarkupId + "')).hide();");
                    attributes.getAjaxCallListeners().add(listener);
                }
            }
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
        }
    }

    public static class SubmitButton extends AjaxSubmitLink {

        private final ButtonBehavior buttonBehavior = new ButtonBehavior(Buttons.Type.Primary);

        public SubmitButton(String id, Form<?> form, IModel<?> label) {
            super(id, form);

            setBody(label);
        }

        @Override
        protected void onInitialize() {
            super.onInitialize();

            add(buttonBehavior);
        }
    }

    private class ModalCloseBehavior extends AjaxEventBehavior {

        public ModalCloseBehavior() {
            super("hidden.bs.modal");
        }

        @Override
        protected void onEvent(AjaxRequestTarget target) {
            handleCloseEvent(target);
        }

        @Override
        protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
            super.updateAjaxAttributes(attributes);

            attributes.setEventPropagation(AjaxRequestAttributes.EventPropagation.BUBBLE);
        }
    }
}
