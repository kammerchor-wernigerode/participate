package de.kammerchorwernigerode.app.participate.event.presentation.components.attendee;

import de.kammerchorwernigerode.app.participate.event.model.Accommodation;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.badge.BadgeBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.components.TooltipBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.helper.Texts;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

public class AccommodationBadge extends GenericPanel<Accommodation> {

    public AccommodationBadge(String id, IModel<Accommodation> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        IModel<Accommodation> model = getModel();

        AccommodationIcon accommodationIcon = new AccommodationIcon("icon", model);
        add(accommodationIcon);

        Label bedsLabel = new Label("beds", model.map(Accommodation::beds).orElse(0));
        add(bedsLabel);

        add(new BadgeBehavior(model.map(this::backgroundColor), Model.of(false)));
        add(ClassAttributeModifier.append("class", model.map(this::textColor)));

        IModel<String> titleModel = new StringResourceModel("AccommodationBadge.${status}", this, model);
        add(new TooltipBehavior(titleModel));
    }

    private Texts.BackgroundColor backgroundColor(Accommodation accommodation) {
        return switch (accommodation.status()) {
            case Accommodation.Status.SEARCHING -> Texts.BackgroundColor.WARNING;
            case Accommodation.Status.OFFERING -> Texts.BackgroundColor.INFO;
            case Accommodation.Status.NO_NEED -> Texts.BackgroundColor.NONE;
        };
    }

    private String textColor(Accommodation accommodation) {
        Accommodation.Status status = accommodation.status();
        return Accommodation.Status.NO_NEED.equals(status) ? "text-muted" : null;
    }
}
