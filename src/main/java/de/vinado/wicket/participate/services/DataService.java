package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.data.Identifiable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;

/**
 * Database server
 */
public abstract class DataService {

    /**
     * {@link EntityManager}
     */
    protected EntityManager entityManager;

    /**
     * Sets the {@link EntityManager}
     *
     * @param entityManager EntityManager
     */
    public abstract void setEntityManager(final EntityManager entityManager);

    /**
     * Loads all items of the provided type.
     *
     * @param type Type of entity to load.
     * @param <T>  Object, which is implementing {@link de.vinado.wicket.participate.data.Identifiable}.
     * @return List of entities.
     */
    public <T extends Identifiable> List<T> getAll(final Class<T> type) {
        return entityManager.createQuery("FROM " + type.getSimpleName(), type).getResultList();
    }

    /**
     * Saves an item.
     *
     * @param item Item to save.
     * @param <T>  Object, which is implementing {@link de.vinado.wicket.participate.data.Identifiable}.
     * @return Saved object.
     */
    @Transactional
    public <T extends Identifiable> T save(T item) {
        entityManager.persist(item);
        item = merge(item);
        return item;
    }

    /**
     * Loads an object by its type and ID.
     *
     * @param type Type of the object.
     * @param id   ID of the object.
     * @param <T>  Object, which is implementing {@link de.vinado.wicket.participate.data.Identifiable}.
     * @return Loaded object.
     */
    public <T extends Identifiable> T load(final Class<T> type, final Serializable id) {
        return entityManager.find(type, id);
    }

    /**
     * Merges an object with its database entry.
     *
     * @param object Object to merge.
     * @param <T>    Type of the object.
     * @return Merged Object.
     */
    public <T extends Identifiable> T merge(final T object) {
        return entityManager.merge(object);
    }

    /**
     * Deletes an object from the database.
     *
     * @param object Object ro remove.
     * @param <T>    Type of the object.
     */
    @Transactional
    public <T extends Identifiable> void remove(final T object) {
        entityManager.remove(merge(object));
    }

    /**
     * Return whether the {@link Identifiable} exists.
     *
     * @param identifiable The {@link Identifiable}
     * @return Whether the {@link Identifiable} exists.
     */
    public boolean exists(final Identifiable identifiable) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<Identifiable> root = criteriaQuery.from(Identifiable.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(root.get("id"), identifiable.getId()));
        return entityManager.createQuery(criteriaQuery).getSingleResult() > 0;
    }
}
