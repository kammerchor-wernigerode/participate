package de.vinado.wicket.participate.components;

import de.agilecoders.wicket.core.markup.html.bootstrap.image.Icon;
import de.agilecoders.wicket.jquery.util.Json;
import de.vinado.app.participate.wicket.bt5.tooltip.TooltipBehavior;
import de.vinado.app.participate.wicket.bt5.tooltip.TooltipConfig;
import de.vinado.wicket.common.UpdateOnEventBehavior;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.Response;
import org.apache.wicket.util.string.Strings;

import java.io.Serializable;

import static de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType.angle_double_down_s;
import static de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType.angle_double_up_s;

public class ShortenedMultilineLabel extends GenericPanel<String> {

    private final Limit limit;
    private State state;

    public ShortenedMultilineLabel(String id, IModel<String> model, Limit limit) {
        this(id, model, limit, State.COLLAPSED);
    }

    public ShortenedMultilineLabel(String id, IModel<String> model, Limit limit, State initial) {
        super(id, model);
        this.limit = limit;
        this.state = initial;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        setOutputMarkupId(true);

        add(text());
        add(button().add(new Behavior() {
            @Override
            public void afterRender(Component component) {
                Response r = component.getResponse();
                r.write("</p>");
            }
        }));

        add(new UpdateOnEventBehavior<>(Intent.class, this::updateState));
    }

    private MultiLineLabel text() {
        MultiLineLabel label = new MultiLineLabel("text", textModel()) {
            @Override
            public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
                CharSequence body = Strings.toMultilineMarkup(getDefaultModelObjectAsString());
                body = body.subSequence(0, body.length() - 4);
                replaceComponentTagBody(markupStream, openTag, body);
            }
        };
        label.setRenderBodyOnly(true);
        return label;
    }

    private IModel<String> textModel() {
        return getModel().map(text -> state.render(text, limit));
    }

    private ToggleButton button() {
        return new ToggleButton("toggle");
    }

    private void updateState(Intent intent) {
        state = intent.replace(state);
    }


    @FunctionalInterface
    public interface Intent {

        State replace(State state);

        static Intent toggle() {
            return State::toggle;
        }

        static Intent ensure(State state) {
            return current -> state;
        }
    }


    @RequiredArgsConstructor
    public enum State {
        COLLAPSED {
            @Override
            protected String render(String text, Limit limit) {
                return limit.abbreviate(text);
            }

            @Override
            protected State toggle() {
                return EXPANDED;
            }
        },

        EXPANDED {
            @Override
            protected String render(String text, Limit limit) {
                return text;
            }

            @Override
            protected State toggle() {
                return COLLAPSED;
            }
        };

        protected abstract String render(String text, Limit limit);

        protected abstract State toggle();
    }


    public static final class Limit implements Serializable {

        private static final String ABBREVIATION_MARKER = "…";
        private static final int MINIMUM_LIMIT_VALUE = ABBREVIATION_MARKER.length() + 1;

        private final int value;

        public Limit(int value) {
            this.value = valid(value);
        }

        private static int valid(int limit) {
            if (limit < MINIMUM_LIMIT_VALUE) {
                throw new IllegalArgumentException("Limit must be grater than or equal to " + MINIMUM_LIMIT_VALUE);
            }
            return limit;
        }

        public boolean affects(String text) {
            return null != text && text.length() > value;
        }

        public String abbreviate(String text) {
            return StringUtils.abbreviate(text, ABBREVIATION_MARKER, value);
        }
    }


    private final class ToggleButton extends GenericPanel<Intent> {

        public ToggleButton(String id) {
            super(id);
        }

        @Override
        protected void onInitialize() {
            super.onInitialize();

            setRenderBodyOnly(true);

            AjaxLink<Intent> link = link();
            link.setOutputMarkupId(true);
            link.add(icon())
                .add(tooltip());
            add(link);
        }

        private AjaxLink<Intent> link() {
            return new AjaxLink<Intent>("link", new ToggleModel()) {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    send(ShortenedMultilineLabel.this, Broadcast.EXACT, getModelObject());
                }
            };
        }

        private WebMarkupContainer icon() {
            return new Icon("icon", () -> state.equals(State.COLLAPSED) ? angle_double_down_s : angle_double_up_s);
        }

        private Behavior tooltip() {
            return new TooltipBehavior(() -> {
                String stateName = state.name().toLowerCase();
                return getString("event.details.participant.comment.toggle.single." + stateName + ".tooltip");
            }, new TooltipConfig().withBoundary(new Json.RawValue("document.body")));
        }

        @Override
        protected void onConfigure() {
            super.onConfigure();
            setVisible(limit.affects(ShortenedMultilineLabel.this.getModelObject()));
        }


        private final class ToggleModel implements IModel<Intent> {

            @Override
            public Intent getObject() {
                return Intent.toggle();
            }
        }
    }
}
