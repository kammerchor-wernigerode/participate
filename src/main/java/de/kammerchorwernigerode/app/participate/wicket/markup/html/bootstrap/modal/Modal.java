package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.modal;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button.BootstrapButton;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button.ButtonBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button.Buttons;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.image.Icon;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.image.IconType;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.ComponentListView;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.IAjaxCallListener;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class Modal extends Panel {

    public static final String CONTENT_WICKET_ID = "content";
    public static final String ACTION_WICKET_ID = "action";

    private final Label title;
    private final WebMarkupContainer header;
    private final WebMarkupContainer body;
    private final WebMarkupContainer footer;
    private final List<Component> actions = new LinkedList<>();

    private boolean staticBackdrop = false;
    private boolean scrollable = false;
    private boolean centered = false;
    private boolean disableAnimation = false;
    private Size size = Size.DEFAULT;
    private Fullscreen fullscreen = Fullscreen.DEFAULT;

    public Modal(String id) {
        super(id);

        this.title = new Label("title", Model.of());
        this.header = new WebMarkupContainer("header");
        this.body = new WebMarkupContainer("body");
        this.footer = new WebMarkupContainer("footer");

        initialize();
    }

    private void initialize() {
        setOutputMarkupPlaceholderTag(true);
        setVisible(false);
    }

    public final String getContentId() {
        return CONTENT_WICKET_ID;
    }

    public final String getActionId() {
        return ACTION_WICKET_ID;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        title.setOutputMarkupId(true);

        header.add(title);
        header.add(new CloseButton("closeButton"));
        footer.add(new ComponentListView("actions", actions));

        WebMarkupContainer dialog;
        add(dialog = new Dialog("dialog"));
        dialog.add(header, body, footer);
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        List<String> cssClassNames = new ArrayList<>();
        cssClassNames.add("modal");
        if (!disableAnimation) {
            cssClassNames.add("fade");
        }

        tag.put("class", String.join(" ", cssClassNames));
        tag.put("tabindex", -1);
        tag.put("aria-labelledby", title.getMarkupId());
        tag.put("aria-hidden", true);

        if (staticBackdrop) {
            tag.put("data-bs-backdrop", "static");
            tag.put("data-bs-keyboard", false);
        }
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

        String markupId = getMarkupId(true);
        String initializationScript = createInitializationScript(markupId);
        response.render(OnDomReadyHeaderItem.forScript(initializationScript));
    }

    public Modal staticBackdrop(boolean staticBackdrop) {
        this.staticBackdrop = staticBackdrop;
        return this;
    }

    public Modal scrollable(boolean scrollable) {
        this.scrollable = scrollable;
        return this;
    }

    public Modal centered(boolean centered) {
        this.centered = centered;
        return this;
    }

    public Modal disableAnimation(boolean disableAnimation) {
        this.disableAnimation = disableAnimation;
        return this;
    }

    public Modal size(Size size) {
        this.size = size;
        return this;
    }

    public Modal fullscreen(Fullscreen fullscreen) {
        this.fullscreen = fullscreen;
        return this;
    }

    public Modal title(IModel<?> title) {
        this.title.setDefaultModel(title);
        return this;
    }

    public Modal content(SerializableFunction<String, Component> constructor) {
        Component component = constructor.apply(CONTENT_WICKET_ID);
        return content(component);
    }

    public Modal content(Component content) {
        if (!CONTENT_WICKET_ID.equals(content.getId())) {
            throw new IllegalArgumentException("Invalid content Wicket ID. Must be '" + CONTENT_WICKET_ID + "'.");
        }

        content.setRenderBodyOnly(true);
        body.addOrReplace(content);
        return this;
    }

    public Modal addSubmitAction(IModel<?> label) {
        return addSubmitAction(label, target -> { });
    }

    public Modal addSubmitAction(IModel<?> label, SerializableConsumer<AjaxRequestTarget> onAfterSubmit) {
        return addAction(id -> new SubmitAction(id, label) {

            @Override
            protected void onAfterSubmit(AjaxRequestTarget target) {
                super.onAfterSubmit(target);
                onAfterSubmit.accept(target);
            }
        });
    }

    public Modal addCloseAction(IModel<?> label) {
        return addAction(id -> new CloseAction(id, label));
    }

    public Modal addAction(SerializableFunction<String, Action> constructor) {
        Action button = constructor.apply(ACTION_WICKET_ID);
        return addAction(button);
    }

    public Modal addAction(Action action) {
        if (!ACTION_WICKET_ID.equals(action.getId())) {
            throw new IllegalArgumentException("Invalid action Wicket ID. Must be '" + ACTION_WICKET_ID + "'.");
        }

        actions.add(action);
        return this;
    }

    public Modal clearActions() {
        actions.clear();
        return this;
    }

    public Modal show(AjaxRequestTarget target) {
        assertContent();
        showModal(target);
        appendShowDialogJavaScript(target);
        return this;
    }

    private void assertContent() {
        if (null == body.get(CONTENT_WICKET_ID)) {
            throw new WicketRuntimeException("Missing modal content. Use modal.content(...).show(...)");
        }
    }

    private void showModal(AjaxRequestTarget target) {
        setVisible(true);
        target.add(this);
    }

    public Modal hide(AjaxRequestTarget target) {
        appendHideDialogJavaScript(target);
        return this;
    }

    protected void appendShowDialogJavaScript(AjaxRequestTarget target) {
        target.appendJavaScript(createActionScript(getMarkupId(true), "show"));
    }

    protected void appendHideDialogJavaScript(AjaxRequestTarget target) {
        target.prependJavaScript(createActionScript(getMarkupId(true), "hide"));
    }

    protected static String createInitializationScript(String markupId) {
        return "new bootstrap.Modal(document.getElementById('" + markupId + "'));";
    }

    protected static String createActionScript(String markupId, String action) {
        return "bootstrap.Modal.getInstance(document.getElementById('" + markupId + "'))." + action + "();";
    }


    private class Dialog extends WebMarkupContainer {

        public Dialog(String id) {
            super(id);
        }

        @Override
        protected void onComponentTag(ComponentTag tag) {
            super.onComponentTag(tag);

            List<String> cssClassNames = new ArrayList<>();
            cssClassNames.add("modal-dialog");
            cssClassNames.add(size.getCssClassName());
            cssClassNames.add(fullscreen.getCssClassName());
            if (centered) {
                cssClassNames.add("modal-dialog-centered");
            }
            if (scrollable) {
                cssClassNames.add("modal-dialog-scrollable");
            }

            tag.put("class", String.join(" ", cssClassNames));
        }
    }


    public abstract static class Action extends WebMarkupContainer implements BootstrapButton<Action> {

        private final Icon icon;
        private final IModel<?> label;
        private final ButtonBehavior buttonBehavior = new ButtonBehavior();

        public Action(String id, IModel<?> label) {
            super(id);
            this.icon = new Icon("icon", null);
            this.label = label;
        }

        @Override
        public Action setVariant(Buttons.Variant variant) {
            buttonBehavior.setVariant(variant);
            return this;
        }

        @Override
        public Action setSize(Buttons.Size size) {
            buttonBehavior.setSize(size);
            return this;
        }

        public Action setIcon(IconType icon) {
            this.icon.setType(icon);
            return this;
        }

        @Override
        protected void onInitialize() {
            super.onInitialize();

            AbstractLink button = createButton("button");
            configure(button);
            button.add(buttonBehavior);
            add(button);

            button.add(icon);

            Label body = new Label("label", label);
            body.setRenderBodyOnly(true);
            button.add(body);
        }

        @Override
        protected void onDetach() {
            super.onDetach();
            label.detach();
        }

        protected abstract AbstractLink createButton(String wicketId);

        protected void configure(AbstractLink button) {
            button.add(new AttributeModifier("type", "button"));
        }
    }

    public static class CloseAction extends Action {

        public CloseAction(String id, IModel<?> label) {
            super(id, label);
        }

        @Override
        protected AbstractLink createButton(String wicketId) {
            return new CloseButton(wicketId);
        }
    }

    public static class SubmitAction extends Action {

        public SubmitAction(String id, IModel<?> label) {
            super(id, label);
            setVariant(Buttons.Variant.PRIMARY);
        }

        @Override
        protected AbstractLink createButton(String wicketId) {
            Form<?> form = form().orElse(null);
            return new AjaxSubmitLink(wicketId, form) {

                @Override
                protected void onAfterSubmit(AjaxRequestTarget target) {
                    SubmitAction.this.onAfterSubmit(target);
                }

                @Override
                protected void onError(AjaxRequestTarget target) {
                    SubmitAction.this.onError(target);
                }
            };
        }

        @Override
        protected void configure(AbstractLink link) {
            super.configure(link);

            form().ifPresent(form -> {
                link.add(new AttributeModifier("type", "submit"));
                link.add(new AttributeModifier("form", form.getMarkupId(true)));
            });
        }

        @SuppressWarnings("rawtypes")
        private Optional<Form> form() {
            Modal modal = findParent(Modal.class);
            return modal.streamChildren()
                .filter(Form.class::isInstance)
                .map(Form.class::cast)
                .findFirst();
        }

        protected void onAfterSubmit(AjaxRequestTarget target) {
            Modal modal = findParent(Modal.class);
            modal.appendHideDialogJavaScript(target);
        }

        protected void onError(AjaxRequestTarget target) {
        }
    }

    public static class CloseButton extends AjaxLink<Void> {

        public CloseButton(String id) {
            super(id);
        }

        @Override
        protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
            super.updateAjaxAttributes(attributes);

            AjaxCallListener listener = createCloseListener();
            List<IAjaxCallListener> listeners = attributes.getAjaxCallListeners();
            listeners.add(listener);
        }

        private AjaxCallListener createCloseListener() {
            Modal modal = findParent(Modal.class);
            String markupId = modal.getMarkupId();
            AjaxCallListener listener = new AjaxCallListener();
            listener.onBeforeSend(createActionScript(markupId, "hide"));
            return listener;
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public enum Size {

        SMALL("modal-sm"),
        DEFAULT(""),
        LARGE("modal-lg"),
        EXTRA_LARGE("modal-xl"),
        ;

        private final String cssClassName;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public enum Fullscreen {

        DEFAULT(""),
        ALWAYS("modal-fullscreen"),
        SM_DOWN("modal-fullscreen-sm-down"),
        MD_DOWN("modal-fullscreen-md-down"),
        LG_DOWN("modal-fullscreen-lg-down"),
        XL_DOWN("modal-fullscreen-xl-down"),
        XXL_DOWN("modal-fullscreen-xxl-down"),
        ;

        private final String cssClassName;
    }
}
