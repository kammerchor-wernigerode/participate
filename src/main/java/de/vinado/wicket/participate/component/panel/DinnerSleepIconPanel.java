package de.vinado.wicket.participate.component.panel;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.component.TextAlign;
import de.vinado.wicket.participate.data.MemberToEvent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class DinnerSleepIconPanel extends Panel {

    public DinnerSleepIconPanel(final String id, final IModel<MemberToEvent> model) {
        super(id, model);

        add(new IconPanel(
            "needsDinner",
            FontAwesomeIconType.cutlery,
            model.getObject().isNeedsDinner() ? IconPanel.Color.SUCCESS : IconPanel.Color.DANGER,
            TextAlign.CENTER).setDisplay(IconPanel.Display.INLINE)
        );

        add(new IconPanel(
            "needsPlaceToSleep",
            FontAwesomeIconType.bed,
            model.getObject().isNeedsPlaceToSleep() ? IconPanel.Color.SUCCESS : IconPanel.Color.DANGER,
            TextAlign.CENTER).setDisplay(IconPanel.Display.INLINE)
        );
    }
}
