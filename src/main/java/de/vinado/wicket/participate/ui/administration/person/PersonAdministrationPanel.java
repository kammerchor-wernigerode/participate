package de.vinado.wicket.participate.ui.administration.person;

import de.vinado.wicket.participate.model.Person;
import lombok.RequiredArgsConstructor;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;

public class PersonAdministrationPanel extends Panel {

    private static final long serialVersionUID = 2120435783576012933L;

    public PersonAdministrationPanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(deletedPersonTable("deleted"));
    }

    private WebMarkupContainer deletedPersonTable(String id) {
        return new EmptyPanel(id);
    }
}
