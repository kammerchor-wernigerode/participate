package de.vinado.wicket.participate.service;

import de.vinado.wicket.participate.data.Communicatable;
import de.vinado.wicket.participate.data.Communication;
import de.vinado.wicket.participate.data.Identifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Service
public class CommunicationService extends DataService {

    private final static Logger LOGGER = LoggerFactory.getLogger(CommunicationService.class);

    /**
     * {@link DataService}
     *
     * @param entityManager Entity manager
     */
    @PersistenceContext
    public void setEntityManager(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Creates a new communication
     *
     * @param dto {@link Communication}
     * @return Saved communication
     */
    @Transactional
    public Communication createCommunication(final Communication dto) {
        return save(new Communication(dto.getValue(), dto.getCommunicationType()));
    }

    /**
     * Saves the modified communication
     *
     * @param communication {@link Communication}
     * @return Communication with ne values
     */
    @Transactional
    public Communication saveCommunication(final Communication communication) {
        Communication loadedCommunication = load(Communication.class, communication.getId());
        loadedCommunication.setValue(communication.getValue());
        loadedCommunication.setCommunicationType(communication.getCommunicationType());
        return save(loadedCommunication);
    }

    @Transactional
    public void removeCommunicationFromObject(final Communicatable communicatable, final Communication communication) {
        Object o = getCommunicationMapping(communicatable, communication);
        remove((Identifiable) o);
        remove(communication);
    }

    private Object getCommunicationMapping(final Communicatable communicatable, final Communication communication) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery(Object.class);
        final Root root = criteriaQuery.from(communicatable.getCommunicationMappingClass());
        criteriaQuery.where(criteriaBuilder.equal(root.get("communication"), communication));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.warn("Communication={} could not be found.", communication.toString());
            return null;
        }
    }
}
