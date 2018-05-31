package de.vinado.wicket.participate.service;

import de.vinado.wicket.participate.data.Configurable;
import de.vinado.wicket.participate.data.Identifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Service
public class ConfigurationService extends DataService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ConfigurationService.class);

    /**
     * {@link DataService}
     *
     * @param entityManager Entity manager
     */
    @PersistenceContext
    public void setEntityManager(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public void saveConfigurables(final List<Configurable> configurables) {
        for (Configurable configurable : configurables) {
            Configurable loadedConfigurable = (Configurable) merge((Identifiable) configurable);
            save(loadedConfigurable);
        }
    }

    @Transactional
    public void removeConfigurable(final Configurable configurable) {
        remove(load(configurable.getClass(), configurable.getId()));
    }

    public List<Configurable> getConfigurables(final Class<? extends Configurable> clazz, final boolean activeOnly) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Configurable> criteriaQuery = criteriaBuilder.createQuery(Configurable.class);
        final Root<? extends Configurable> root = criteriaQuery.from(clazz);
        if (activeOnly) {
            criteriaQuery.where(criteriaBuilder.equal(root.get("active"), true));
        }
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("sortOrder")));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public Long getNextSortOrder(final Class<? extends Configurable> clazz) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<? extends Configurable> root = criteriaQuery.from(clazz);
        criteriaQuery.select(criteriaBuilder.max(root.get("sortOrder")));
        final TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
        try {
            if (null == typedQuery.getSingleResult()) {
                return 0L;
            }
            return typedQuery.getSingleResult() + 1;
        } catch (NoResultException e) {
            return 0L;
        }
    }
}
