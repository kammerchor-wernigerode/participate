package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.toast;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.DurationSerializer;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.util.string.CssUtils;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

import java.io.Serializable;
import java.time.Duration;

import lombok.Data;
import lombok.SneakyThrows;

public class Toast extends GenericPanel<FeedbackMessage> {

    private Options options = new Options();

    public Toast(String id, IModel<FeedbackMessage> model) {
        super(id, model);
    }

    public Toast setOptions(Options options) {
        this.options = options;
        return this;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        IModel<FeedbackMessage> model = getModel();

        Label title = new Label("title", new StringResourceModel("Toast.title.${level}", model));
        add(title);

        WebMarkupContainer closeButton = new WebMarkupContainer("closeButton");
        closeButton.add(ClassAttributeModifier.append("class", model.map(this::getButtonCssClassName)));
        add(closeButton);

        Label message = new Label("body", model.map(FeedbackMessage::getMessage));
        add(message);

        add(new ToastHiddenBehavior());
    }

    private String getButtonCssClassName(FeedbackMessage feedbackMessage) {
        String key = CssUtils.key(Toast.class, feedbackMessage.getLevelAsString());
        return getString(key);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        if (!isVisibleInHierarchy()) {
            return;
        }

        String markupId = getMarkupId();
        String initializationScript = createInitializationScript(markupId, options);
        response.render(OnDomReadyHeaderItem.forScript("%s.show()".formatted(initializationScript)));
    }

    private String createInitializationScript(String markupId, Options options) {
        String jsonOptions = "{}";
        if (null != options) {
            jsonOptions = serialize(options);
        }

        return "new bootstrap.Toast(document.getElementById('" + markupId + "'), " + jsonOptions + ")";
    }

    @SneakyThrows
    private String serialize(Options options) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(options);
    }


    private class ToastHiddenBehavior extends AjaxEventBehavior {

        public ToastHiddenBehavior() {
            super("hidden.bs.toast");
        }

        @Override
        protected void onEvent(AjaxRequestTarget target) {
            if (isVisibleInHierarchy()) {
                discardToast(target);
            }
        }

        private void discardToast(AjaxRequestTarget target) {
            Toast toast = Toast.this;
            toast.setVisible(false);
            target.add(toast);
        }
    }


    @Data
    public static class Options implements Serializable {

        private boolean autohide = true;

        @JsonSerialize(using = DurationSerializer.class)
        @JsonFormat(shape = Shape.NUMBER_INT, without = Feature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
        private Duration delay = Duration.ofSeconds(5);
    }
}
