package de.vinado.wicket.participate.data;

import java.io.Serializable;

/**
 * Class which implement Identifiable, inherits the {@link #getId()} method,
 * so the {@link javax.persistence.EntityManager} can identify the object with its id.
 *
 * @param <T> Serializable data type
 */
public interface Identifiable<T extends Serializable> {

    /**
     * Returns the database id
     *
     * @return ID
     */
    T getId();
}