package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.model.Hideable;
import de.vinado.wicket.participate.model.Identifiable;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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
     * @param <T>  Object, which is implementing {@link de.vinado.wicket.participate.model.Identifiable}.
     * @return List of entities.
     */
    public <T extends Identifiable> List<T> getAll(final Class<T> type) {
        return entityManager.createQuery("FROM " + type.getSimpleName(), type).getResultList();
    }

    /**
     * Saves an item.
     *
     * @param item Item to save.
     * @param <T>  Object, which is implementing {@link de.vinado.wicket.participate.model.Identifiable}.
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
     * @param <T>  Object, which is implementing {@link de.vinado.wicket.participate.model.Identifiable}.
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

    /**
     * Returns an predefined {@link Predicate} that checks if {@link Hideable} is active.
     *
     * @param criteriaBuilder {@link CriteriaBuilder}
     * @param path            {@link Path} to {@link Hideable}
     * @return {@link Predicate}
     */
    protected static Predicate forActive(final CriteriaBuilder criteriaBuilder, final Path<? extends Hideable> path) {
        return criteriaBuilder.equal(path.get("active"), true);
    }
}
