package de.kammerchorwernigerode.app.participate.wicket.markup.html.basic;

import de.kammerchorwernigerode.app.participate.wicket.behavior.UpdateOnEventBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.basic.CollapsibleTextPanel.Toggle.State;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.icon.Bi;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.link.IconAjaxLink;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

public class CollapsibleTextPanel extends Panel {

    private final Limit limit;

    private State state = State.COLLAPSED;

    public CollapsibleTextPanel(String id, IModel<?> model, Limit limit) {
        super(id, model);
        this.limit = limit;
    }

    public CollapsibleTextPanel setState(State state) {
        this.state = state;
        return this;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        setOutputMarkupId(true);

        IModel<String> contentModel = getDefaultModel().map(this::print);
        Label contentLabel = new Label("content", contentModel);
        add(contentLabel);

        ToggleLink toggleLink = new ToggleLink("toggle", new ToggleModel());
        add(toggleLink);

        add(new UpdateOnEventBehavior<>(Toggle.class, this::updateState));
    }

    private String print(Object obj) {
        String text = getDefaultModelObjectAsString(obj);
        return state.render(text, limit);
    }

    private void updateState(Toggle toggle) {
        state = toggle.replace(state);
    }


    private class ToggleLink extends IconAjaxLink<Toggle> {

        public ToggleLink(String id, IModel<Toggle> model) {
            super(id, model);
        }

        @Override
        protected void onConfigure() {
            super.onConfigure();

            setVisible(limit.affects(CollapsibleTextPanel.this.getDefaultModelObjectAsString()));
            setIcon(State.COLLAPSED.equals(state) ? Bi.arrows_angle_expand : Bi.arrows_angle_contract);
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            send(CollapsibleTextPanel.this, Broadcast.EXACT, getModelObject());
        }
    }


    public static class Limit implements Serializable {

        private static final String ABBREVIATION_MARKER = "…";

        private final int value;

        public Limit(int value) {
            this.value = ensureValid(value);
        }

        private static int ensureValid(int value) {
            int minimum = ABBREVIATION_MARKER.length() + 1;
            if (value < minimum) {
                throw new IllegalArgumentException("Limit must be grater than or equal to " + minimum);
            }
            return value;
        }

        public boolean affects(String text) {
            return null != text && text.length() > value;
        }

        public String abbreviate(String text) {
            int maxWidth = value - 1 - ABBREVIATION_MARKER.length();
            String abbreviate = StringUtils.abbreviate(text, "", maxWidth);
            return abbreviate.trim().replaceAll("\\p{Punct}$", "") + ABBREVIATION_MARKER;
        }
    }

    @FunctionalInterface
    public interface Toggle {

        State replace(State state);

        static Toggle toggle() {
            return State::toggle;
        }

        static Toggle ensure(State state) {
            return ignored -> state;
        }


        enum State {

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
            },
            ;

            protected abstract String render(String text, Limit limit);

            protected abstract State toggle();
        }
    }

    private static class ToggleModel implements IModel<Toggle> {

        @Override
        public Toggle getObject() {
            return Toggle.toggle();
        }
    }
}
