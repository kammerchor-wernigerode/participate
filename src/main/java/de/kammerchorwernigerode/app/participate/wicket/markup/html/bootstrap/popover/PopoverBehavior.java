package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.popover;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.popover.PopoverBehavior.Options.Placement;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.core.util.string.ComponentRenderer;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.io.IClusterable;
import org.danekja.java.util.function.serializable.SerializableFunction;
import org.springframework.util.StringUtils;

import java.util.Optional;

import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;

public class PopoverBehavior extends Behavior {

    public static final String CONTENT_WICKET_ID = "content";

    private final Options options = new Options();

    @Getter
    private Component component;
    private Component content;
    private boolean show;

    @Override
    public void bind(Component component) {
        this.component = component;
        component.setOutputMarkupId(true);
        component.add(new PopoverInsertedBehavior());
        component.add(new PopoverHiddenBehavior());

        if (null != content && null == content.getParent()) {
            component.getPage().add(content);
        }
    }

    @Override
    public void unbind(Component component) {
        this.component = null;
        this.show = false;
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);

        String initializationScript = createInitializationScript(component, options);
        response.render(OnDomReadyHeaderItem.forScript(initializationScript));

        this.show = false;
    }

    protected String createInitializationScript(Component component, Options options) {
        String markupId = component.getMarkupId();
        String jsonOptions = serialize(options);
        return "new bootstrap.Popover(document.getElementById('" + markupId + "'), " + jsonOptions + ")";
    }

    public PopoverBehavior setTitle(IModel<String> title) {
        options.setTitle(title);
        return this;
    }

    public PopoverBehavior setContainer(Component component) {
        options.setContainer("document.getElementById('" + component.getMarkupId(true) + "')");
        return this;
    }

    public PopoverBehavior setPlacement(Placement placement) {
        options.setPlacement(placement);
        return this;
    }

    public PopoverBehavior setContent(SerializableFunction<String, Component> constructor) {
        Component content = constructor.apply(CONTENT_WICKET_ID);
        return setContent(content);
    }

    public PopoverBehavior setContent(Component content) {
        if (!CONTENT_WICKET_ID.equals(content.getId())) {
            throw new IllegalArgumentException("Invalid content Wicket ID. Must be '" + CONTENT_WICKET_ID + "'.");
        }

        this.content = content;
        content.setRenderBodyOnly(true);
        content.setVisible(false);
        return this;
    }

    public PopoverBehavior toggle(AjaxRequestTarget target) {
        PopoverBehavior behavior;
        if (show) {
            behavior = hide(target);
        } else {
            behavior = show(target);
        }
        return behavior;
    }

    public PopoverBehavior show(AjaxRequestTarget target) {
        showContent();
        appendShowPopoverJavaScript(target);
        return this;
    }

    private void showContent() {
        content.setVisible(true);
    }

    protected void appendShowPopoverJavaScript(AjaxRequestTarget target) {
        String markupId = component.getMarkupId();
        String header = options.getTitle()
            .filter(StringUtils::hasText)
            .map(html -> "'.popover-header': `" + html + "`,")
            .orElse("")
            .getObject();
        String body = Optional.ofNullable(content)
            .map(ComponentRenderer::renderComponent)
            .map(CharSequence::toString)
            .map(String::trim)
            .map(html -> "'.popover-body': `" + html + "`,")
            .orElse("");

        String javaScript = """
            const popover_%s = bootstrap.Popover.getInstance(document.getElementById('%s'));
            popover_%s.setContent({
              %s
              %s
            });
            popover_%s.show();
            """.formatted(markupId, markupId, markupId, header, body, markupId);
        target.appendJavaScript(javaScript);
    }

    public PopoverBehavior hide(AjaxRequestTarget target) {
        appendHidePopoverJavaScript(target);
        return this;
    }

    protected void appendHidePopoverJavaScript(AjaxRequestTarget target) {
        String markupId = component.getMarkupId();
        target.appendJavaScript("bootstrap.Popover.getInstance(document.getElementById('" + markupId + "')).hide()");
    }

    @SneakyThrows
    private String serialize(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }


    private class PopoverInsertedBehavior extends AjaxEventBehavior {

        public PopoverInsertedBehavior() {
            super("inserted.bs.popover");
        }

        @Override
        protected void onEvent(AjaxRequestTarget target) {
            PopoverBehavior.this.show = true;
        }
    }

    private class PopoverHiddenBehavior extends AjaxEventBehavior {

        public PopoverHiddenBehavior() {
            super("hidden.bs.popover");
        }

        @Override
        protected void onEvent(AjaxRequestTarget target) {
            if (!component.isVisibleInHierarchy()) {
                return;
            }

            PopoverBehavior.this.content = null;
            PopoverBehavior.this.show = false;
        }
    }


    @Data
    public static class Options implements IClusterable {

        @JsonRawValue
        private String container = "false";

        private boolean html = true;

        private Placement placement = Placement.auto;

        private boolean sanitize;

        @JsonIgnore
        private IModel<String> title = Model.of();

        private final String trigger = "manual";

        @JsonIgnore
        public IModel<String> getTitle() {
            return title;
        }

        @JsonGetter("title")
        protected String serializeTitle() {
            return title.filter(StringUtils::hasText).orElse("&nbsp;").getObject();
        }


        public enum Placement {

            auto,
            top,
            right,
            bottom,
            left,
            ;
        }
    }
}
