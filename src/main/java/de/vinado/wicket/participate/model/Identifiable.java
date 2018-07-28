package de.vinado.wicket.participate.model;

import java.io.Serializable;

/**
 * Class which implement Identifiable, inherits the {@link #getId()} method,
 * so the {@link javax.persistence.EntityManager} can identify the object with its id.
 *
 * @param <T> Serializable data type
 */
public interface Identifiable<T extends Serializable> extends Serializable {

    /**
     * Returns the database id
     *
     * @return ID
     */
    T getId();
}
