package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.vinado.wicket.bt4.tooltip.TooltipBehavior;
import de.vinado.wicket.participate.model.Accommodation;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
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
        add(tooltip());

        add(label("bed"));
        add(icon("icon"));
        add(beds("beds"));
    }

    private Behavior tooltip() {
        ViewModel viewModel = getModel().getObject();
        String key = viewModel.getTooltipKey();
        int beds = viewModel.getBeds();
        StringResourceModel label = new StringResourceModel(key).setParameters(beds);

        return new TooltipBehavior(label);
    }

    private Component label(String wicketId) {
        ViewModel viewModel = getModel().getObject();
        String key = viewModel.getLabelKey();
        int beds = viewModel.getBeds();
        StringResourceModel title = new StringResourceModel(key).setParameters(beds);

        TransparentWebMarkupContainer icon = new TransparentWebMarkupContainer(wicketId);
        icon.add(new AttributeModifier("title", title));
        return icon;
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

        String labelKey;
        String tooltipKey;
    }

    public static class ViewModelFactory {

        public ViewModel create(@NonNull Accommodation model) {
            Accommodation.Status status = model.getStatus();
            String labelNamespace = "icon.label.event.participant.accommodation.";
            String tooltipNamespace = "icon.tooltip.event.participant.accommodation.";
            switch (status) {
                case NO_NEED:
                    String noNeed = "no-need";
                    String noNeedLabelKey = labelNamespace + noNeed;
                    String noNeedTooltipKey = tooltipNamespace + noNeed;
                    return new ViewModel("badge-transparent text-muted", this::empty, beds(model), noNeedLabelKey, noNeedTooltipKey);
                case SEARCHING:
                    String searching = "searching";
                    String searchingLabelKey = labelNamespace + searching;
                    String searchingTooltipKey = tooltipNamespace + searching;
                    return new ViewModel("badge-warning", this::searching, beds(model), searchingLabelKey, searchingTooltipKey);
                case OFFERING:
                    String offering = "offering";
                    String offeringLabelKey = labelNamespace + offering;
                    String offeringTooltipKey = tooltipNamespace + offering;
                    return new ViewModel("badge-info", this::offering, beds(model), offeringLabelKey, offeringTooltipKey);
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
