package de.vinado.wicket.participate.service;

import de.vinado.wicket.participate.data.Address;
import de.vinado.wicket.participate.data.Addressable;
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
public class AddressService extends DataService {

    private final static Logger LOGGER = LoggerFactory.getLogger(AddressService.class);

    /**
     * {@link de.vinado.wicket.participate.service.DataService}
     *
     * @param entityManager Entity manager
     */
    @Override
    @PersistenceContext
    public void setEntityManager(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Saves the modified address
     *
     * @param address {@link de.vinado.wicket.participate.data.Address}
     * @return Address with ne values
     */
    @Transactional
    public Address saveAddress(final Address address) {
        Address loadedAddress = load(Address.class, address.getId());
        loadedAddress.setStreetAddress(address.getStreetAddress());
        loadedAddress.setPostalCode(address.getPostalCode());
        loadedAddress.setLocality(address.getLocality());
        loadedAddress.setCountry(address.getCountry());
        return save(loadedAddress);
    }

    @Transactional
    public void removeAddressFromObject(final Addressable addressable, final Address address) {
        Object o = getAddressMapping(addressable, address);
        remove((Identifiable) o);
        remove(address);
    }

    public Object getAddressMapping(final Addressable addressable, final Address address) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery(Object.class);
        final Root root = criteriaQuery.from(addressable.getAddressMappingClass());
        criteriaQuery.select(root);
        criteriaQuery.where(criteriaBuilder.equal(root.get("address"), address));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.info("Address={} could not be found.", address.toString());
            return null;
        }
    }
}
