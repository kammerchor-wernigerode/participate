package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.vinado.app.participate.wicket.bt5.tooltip.TooltipBehavior;
import de.vinado.wicket.participate.model.Accommodation;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

import java.io.Serializable;
import java.util.Objects;

import static de.vinado.wicket.participate.model.Accommodation.Status.NO_NEED;

public class AccommodationBadge extends GenericPanel<AccommodationBadge.ViewModel> {

    public AccommodationBadge(String id, IModel<Accommodation> model) {
        super(id, model.map(AccommodationBadge::translate));
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

    protected Component icon(String wicketId) {
        IModel<Accommodation> model = getModel().map(ViewModel::getModel);
        return new AccommodationIcon(wicketId, model);
    }

    protected Component beds(String wicketId) {
        IModel<Integer> model = getModel().map(ViewModel::getBeds);
        return new Label(wicketId, model);
    }


    @Value
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ViewModel implements Serializable {

        Accommodation model;

        String color;
        int beds;

        String tooltipKey;
    }

    public static class ViewModelFactory {

        public ViewModel create(@NonNull Accommodation model) {
            Accommodation.Status status = Objects.requireNonNullElse(model.getStatus(), NO_NEED);
            String tooltipNamespace = "badge.tooltip.event.participant.accommodation.";
            switch (status) {
                case NO_NEED:
                    String noNeed = "no-need";
                    String noNeedTooltipKey = tooltipNamespace + noNeed;
                    return new ViewModel(model, "badge-transparent text-muted", beds(model), noNeedTooltipKey);
                case SEARCHING:
                    String searching = "searching";
                    String searchingTooltipKey = tooltipNamespace + searching;
                    return new ViewModel(model, "badge-warning", beds(model), searchingTooltipKey);
                case OFFERING:
                    String offering = "offering";
                    String offeringTooltipKey = tooltipNamespace + offering;
                    return new ViewModel(model, "badge-info", beds(model), offeringTooltipKey);
                default:
                    throw new IllegalArgumentException();
            }
        }

        private static int beds(Accommodation accommodation) {
            return null == accommodation.getBeds()
                ? 0
                : accommodation.getBeds();
        }
    }
}
