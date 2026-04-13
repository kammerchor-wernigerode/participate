package de.kammerchorwernigerode.app.participate.person.presentation.ui.overview;

import de.kammerchorwernigerode.app.participate.person.presentation.model.PersonEntrySpecification;
import de.kammerchorwernigerode.app.participate.wicket.ParticipatePage;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class PersonsPage extends ParticipatePage {

    @Override
    protected void onInitialize() {
        super.onInitialize();

        PersonEntrySpecification personSpecification = new PersonEntrySpecification();
        IModel<PersonEntrySpecification> specModel = new CompoundPropertyModel<>(personSpecification);
        PersonTablePanel personTablePanel = new PersonTablePanel("persons", specModel);
        add(personTablePanel);
    }
}
