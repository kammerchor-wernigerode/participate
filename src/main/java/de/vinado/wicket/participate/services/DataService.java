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

public abstract class DataService {

    protected EntityManager entityManager;

    public abstract void setEntityManager(EntityManager entityManager);

    public <T extends Identifiable> List<T> getAll(Class<T> type) {
        return entityManager.createQuery("FROM " + type.getSimpleName(), type).getResultList();
    }

    @Transactional
    public <T extends Identifiable> T save(T item) {
        entityManager.persist(item);
        item = merge(item);
        return item;
    }

    public <T extends Identifiable> T load(Class<T> type, Serializable id) {
        return entityManager.find(type, id);
    }

    public <T extends Identifiable> T merge(T object) {
        return entityManager.merge(object);
    }

    @Transactional
    public <T extends Identifiable> void remove(T object) {
        entityManager.remove(merge(object));
    }

    public boolean exists(Identifiable identifiable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Identifiable> root = criteriaQuery.from(Identifiable.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(root.get("id"), identifiable.getId()));
        return entityManager.createQuery(criteriaQuery).getSingleResult() > 0;
    }

    protected static Predicate forActive(CriteriaBuilder criteriaBuilder, Path<? extends Hideable> path) {
        return criteriaBuilder.equal(path.get("active"), true);
    }
}
