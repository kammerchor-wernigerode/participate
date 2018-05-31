package de.vinado.wicket.participate.service;

import de.vinado.wicket.participate.data.Configurable;
import de.vinado.wicket.participate.data.Identifiable;
import de.vinado.wicket.participate.data.ListOfValue;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.TransformerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Provides interaction with the database. The service takes care of the {@link ListOfValue} and other Lov related
 * objects.
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 * @see DataService
 */
@Service
public class ListOfValueService extends DataService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ListOfValueService.class);

    @Override
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public void saveListOfValue(final Configurable configurable) {
        final Configurable mergedConfigurable = (Configurable) merge((Identifiable) configurable);
        save((Identifiable) mergedConfigurable);
    }

    @Transactional
    public void saveListOfValueList(final List<Configurable> configurableList) {
        for (Configurable configurable : configurableList) {
            final Configurable mergedConfigurable = (Configurable) merge((Identifiable) configurable);
            save((Identifiable) mergedConfigurable);
        }
    }

    @Transactional
    public void removeConfigurable(final Configurable configurable) throws DataIntegrityViolationException {
        final Configurable loadedConfigurable = load(configurable.getClass(), configurable.getId());
        loadedConfigurable.setActive(false);
        save(loadedConfigurable);
    }

    public Long getNextSortOrder(final Class<? extends Configurable> clazz) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<? extends Configurable> root = criteriaQuery.from(clazz);
        criteriaQuery.select(root.get("sortOrder"));
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("sortOrder")));
        return 1 + entityManager.createQuery(criteriaQuery).setMaxResults(1).getSingleResult();
    }

    public List<String> convertToNameList(final Collection<Configurable> lovList) {
        return new ArrayList<>(CollectionUtils.collect(lovList, TransformerUtils.invokerTransformer("getName")));
    }

    public List<String> convertToIdentifierList(final Collection<Configurable> lovList) {
        return new ArrayList<>(CollectionUtils.collect(lovList, TransformerUtils.invokerTransformer("getIdentifier")));
    }

    public Configurable getDefaultFromConfigurable(final Class<? extends Configurable> clazz) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Configurable> criteriaQuery = criteriaBuilder.createQuery(Configurable.class);
        final Root<? extends Configurable> root = criteriaQuery.from(clazz);
        final Predicate forActive = criteriaBuilder.equal(root.get("active"), true);
        final Predicate forDefault = criteriaBuilder.equal(root.get("isDefault"), true);
        criteriaQuery.select(root);
        criteriaQuery.where(forActive, forDefault);
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (NoResultException e) {
            LOGGER.warn("{}, default value could not be found.", clazz.getSimpleName());
            return null;
        } catch (NonUniqueResultException e) {
            LOGGER.warn("{}, has more than one default entry.", clazz.getSimpleName());
            return null;
        }
    }

    public List<Configurable> getConfigurableList(final Class<? extends Configurable> clazz) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Configurable> criteriaQuery = criteriaBuilder.createQuery(Configurable.class);
        final Root<? extends Configurable> root = criteriaQuery.from(clazz);
        criteriaQuery.select(root);
        criteriaQuery.where(criteriaBuilder.equal(root.get("active"), true));
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("sortOrder")));
        try {
            return entityManager.createQuery(criteriaQuery).getResultList();
        } catch (NoResultException e) {
            LOGGER.warn("{}, default value could not be found.", clazz.getSimpleName());
            return null;
        }
    }

    public Configurable getConfigurable(final Class<? extends Configurable> clazz, final String identifier) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Configurable> criteriaQuery = criteriaBuilder.createQuery(Configurable.class);
        final Root<? extends Configurable> root = criteriaQuery.from(clazz);
        final Predicate forActive = criteriaBuilder.equal(root.get("active"), true);
        final Predicate forIdentifier = criteriaBuilder.equal(root.get("identifier"), identifier);
        criteriaQuery.select(root);
        criteriaQuery.where(forActive, forIdentifier);
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.info("{} with identifier={} could not be found.", clazz.getSimpleName(), identifier);
            return null;
        }
    }
}
