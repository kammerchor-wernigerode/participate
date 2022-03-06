package de.vinado.wicket.participate.components.panels;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType;
import de.vinado.wicket.participate.components.TextAlign;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.dtos.ParticipantDTO;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * This icon panel has nothing to do with the common bed and breakfast. The name summarises it's purpose perfectly with
 * this class name. Adds two icons. One for bed, one for breakfast.
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class BnBIconPanel extends Panel {

    public BnBIconPanel(final String id, final IModel<Participant> model) {
        super(id, model);

        add(new IconPanel(
            "catering",
            FontAwesome5IconType.utensils_s,
            model.getObject().isCatering() ? IconPanel.Color.SUCCESS : IconPanel.Color.DANGER,
            TextAlign.CENTER).setDisplay(IconPanel.Display.INLINE)
        );

        add(new IconPanel(
            "accommodation",
            FontAwesome5IconType.bed_s,
            model.getObject().isAccommodation() ? IconPanel.Color.SUCCESS : IconPanel.Color.DANGER,
            TextAlign.CENTER).setDisplay(IconPanel.Display.INLINE)
        );

        IModel<ParticipantDTO> dtoModel = model.map(ParticipantDTO::new);
        WebMarkupContainer car = new WebMarkupContainer("car");
        car.add(new CssClassNameAppender(dtoModel.getObject().isCar() ? "badge-success" : "badge-transparent text-muted"));
        add(car);

        car.add(new Label("seats", dtoModel.map(ParticipantDTO::getCarSeatCount)));
    }
}
