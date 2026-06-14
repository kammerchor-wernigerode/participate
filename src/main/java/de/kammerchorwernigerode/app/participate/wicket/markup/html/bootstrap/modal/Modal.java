package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.modal;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.IAjaxCallListener;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class Modal extends Panel {

    public static final String CONTENT_WICKET_ID = "content";

    private final Dialog dialog;

    private boolean staticBackdrop = false;
    private boolean scrollable = false;
    private boolean centered = false;
    private boolean disableAnimation = false;
    private Size size = Size.DEFAULT;
    private Fullscreen fullscreen = Fullscreen.DEFAULT;
    private Component label;

    public Modal(String id) {
        super(id);

        this.dialog = new Dialog("dialog");

        initialize();
    }

    private void initialize() {
        setOutputMarkupPlaceholderTag(true);
        setVisible(false);
    }

    public final String getContentId() {
        return CONTENT_WICKET_ID;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(dialog);
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
        if (null != label) {
            tag.put("aria-labelledby", label.getMarkupId());
        }
        tag.put("aria-hidden", true);

        if (staticBackdrop) {
            tag.put("data-bs-backdrop", "static");
            tag.put("data-bs-keyboard", false);
        }
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

    public Modal labelledby(Component component) {
        this.label = component;
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

        if (content instanceof ModalBody body) {
            labelledby(body.getDialogContent().getLabel());
        }

        content.setRenderBodyOnly(true);
        this.dialog.addOrReplace(content);
        return this;
    }

    public Modal show(AjaxRequestTarget target) {
        assertContent();
        showModal(target);
        appendShowDialogJavaScript(target);
        return this;
    }

    private void assertContent() {
        if (null == dialog.get(CONTENT_WICKET_ID)) {
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

    public static String createInitializationScript(String markupId) {
        return "new bootstrap.Modal(document.getElementById('" + markupId + "'));";
    }

    public static String createActionScript(String markupId, String action) {
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
