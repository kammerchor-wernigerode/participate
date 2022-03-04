package de.vinado.wicket.participate.components;

import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.Icon;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import de.vinado.wicket.participate.behavoirs.UpdateOnEventBehavior;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

import static de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType.angle_double_down;
import static de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType.angle_double_up;

/**
 * @author Vincent Nadoll
 */
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
        add(button());

        add(new UpdateOnEventBehavior<>(Intent.class, this::updateState));
    }

    private MultiLineLabel text() {
        MultiLineLabel label = new MultiLineLabel("text", textModel());
        label.setRenderBodyOnly(true);
        return label;
    }

    private IModel<String> textModel() {
        return Models.map(getModel(), text -> state.render(text, limit));
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

        public static final Limit NONE = new Limit(Integer.MAX_VALUE);

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
            return new Icon("icon", new AbstractReadOnlyModel<IconType>() {
                @Override
                public IconType getObject() {
                    return state.equals(State.COLLAPSED) ? angle_double_down : angle_double_up;
                }
            });
        }

        private Behavior tooltip() {
            return new TooltipBehavior(new AbstractReadOnlyModel<String>() {
                @Override
                public String getObject() {
                    String stateName = state.name().toLowerCase();
                    return getString("event.details.participant.comment.toggle.single." + stateName + ".tooltip");
                }
            });
        }

        @Override
        protected void onConfigure() {
            super.onConfigure();
            setVisible(limit.affects(ShortenedMultilineLabel.this.getModelObject()));
        }


        private final class ToggleModel extends AbstractReadOnlyModel<Intent> {

            @Override
            public Intent getObject() {
                return Intent.toggle();
            }
        }
    }
}