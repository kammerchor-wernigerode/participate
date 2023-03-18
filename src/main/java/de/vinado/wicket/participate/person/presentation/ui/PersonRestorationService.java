package de.vinado.wicket.participate.person.presentation.ui;

import de.vinado.wicket.participate.model.Person;
import lombok.NonNull;

public interface PersonRestorationService {

    void restore(@NonNull Person person);
}
