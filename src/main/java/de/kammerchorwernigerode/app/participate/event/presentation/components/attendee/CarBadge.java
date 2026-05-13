package de.kammerchorwernigerode.app.participate.event.presentation.components.attendee;

import de.kammerchorwernigerode.app.participate.event.presentation.model.Car;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.badge.BadgeBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.components.TooltipBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.helper.Texts;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.icon.Fa.Solid;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.icon.FontAwesomeSolidJsResourceReference;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.image.Icon;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import static de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.icon.FaBuilder.fa;

public class CarBadge extends GenericPanel<Car> {

    public CarBadge(String id, IModel<Car> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        IModel<Car> model = getModel();

        Icon carIcon = new Icon("icon", fa(Solid.car).build());
        add(carIcon);

        Label seatsLabel = new Label("seats", model.map(Car::seats));
        add(seatsLabel);

        add(new BadgeBehavior(model.map(this::backgroundColor), Model.of(false)));
        add(ClassAttributeModifier.append("class", model.map(this::textColor)));

        IModel<String> titleModel = new StringResourceModel("CarBadge.${available}", this, model);
        add(new TooltipBehavior(titleModel));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(FontAwesomeSolidJsResourceReference.asHeaderItem());
    }

    private Texts.BackgroundColor backgroundColor(Car car) {
        return car.available()
            ? Texts.BackgroundColor.SUCCESS
            : Texts.BackgroundColor.NONE;
    }

    private String textColor(Car car) {
        return car.available()
            ? null
            : "text-muted";
    }
}
