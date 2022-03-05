package de.vinado.wicket.participate.components;

import de.vinado.wicket.participate.model.Person;
import org.springframework.lang.Nullable;

import java.io.Serializable;

/**
 * @author Vincent Nadoll
 */
@FunctionalInterface
public interface PersonContext extends Serializable {

    @Nullable
    Person get();
}
