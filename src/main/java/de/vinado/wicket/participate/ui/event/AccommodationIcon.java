package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.vinado.wicket.participate.model.Accommodation;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.io.Serializable;

public class AccommodationIcon extends GenericPanel<AccommodationIcon.ViewModel> {

    public AccommodationIcon(String id, IModel<Accommodation> model) {
        super(id, model.map(AccommodationIcon::translate));
    }

    private static ViewModel translate(Accommodation accommodation) {
        ViewModelFactory factory = new ViewModelFactory();
        return factory.create(accommodation);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new CssClassNameAppender("badge"));
        add(new CssClassNameAppender(getModel().map(ViewModel::getColor)));

        add(icon("icon"));
        add(beds("beds"));
    }

    protected Component icon(String wicketId) {
        return getModel().getObject().getIcon().apply(wicketId);
    }

    protected Component beds(String wicketId) {
        IModel<Integer> model = getModel().map(ViewModel::getBeds);
        return new Label(wicketId, model);
    }


    @Value
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ViewModel implements Serializable {

        String color;
        SerializableFunction<String, Component> icon;
        int beds;
    }

    public static class ViewModelFactory {

        public ViewModel create(@NonNull Accommodation model) {
            Accommodation.Status status = model.getStatus();
            switch (status) {
                case NO_NEED:
                    return new ViewModel("badge-transparent text-muted", this::empty, beds(model));
                case SEARCHING:
                    return new ViewModel("badge-warning", this::searching, beds(model));
                case OFFERING:
                    return new ViewModel("badge-info", this::offering, beds(model));
                default:
                    throw new IllegalArgumentException();
            }
        }

        private Component empty(String wicketId) {
            TransparentWebMarkupContainer component = new TransparentWebMarkupContainer(wicketId);
            component.setVisible(true);
            return component;
        }

        private Component searching(String wicketId) {
            TransparentWebMarkupContainer component = new TransparentWebMarkupContainer(wicketId);
            component.add(new CssClassNameAppender("fas fa-search"));
            component.add(new AttributeAppender("data-fa-transform", "shrink-7 up-4 right-7"));
            return component;
        }

        private Component offering(String wicketId) {
            TransparentWebMarkupContainer component = new TransparentWebMarkupContainer(wicketId);
            component.add(new CssClassNameAppender("fas fa-hand-holding fa-flip-horizontal"));
            component.add(new AttributeAppender("data-fa-transform", "shrink-5 up-7 right-7"));
            return component;
        }

        private static int beds(Accommodation accommodation) {
            return null == accommodation.getBeds()
                ? 0
                : accommodation.getBeds();
        }
    }
}
