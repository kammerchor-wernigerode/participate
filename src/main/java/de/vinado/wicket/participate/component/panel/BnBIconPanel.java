package de.vinado.wicket.participate.component.panel;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.component.TextAlign;
import de.vinado.wicket.participate.data.Participant;
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
            FontAwesomeIconType.cutlery,
            model.getObject().isCatering() ? IconPanel.Color.SUCCESS : IconPanel.Color.DANGER,
            TextAlign.CENTER).setDisplay(IconPanel.Display.INLINE)
        );

        add(new IconPanel(
            "accommodation",
            FontAwesomeIconType.bed,
            model.getObject().isAccommodation() ? IconPanel.Color.SUCCESS : IconPanel.Color.DANGER,
            TextAlign.CENTER).setDisplay(IconPanel.Display.INLINE)
        );
    }
}
