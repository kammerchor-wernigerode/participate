package de.vinado.wicket.participate.service;

import de.vinado.wicket.participate.data.ListOfValue;
import de.vinado.wicket.participate.data.Permission;
import de.vinado.wicket.participate.data.Person;
import de.vinado.wicket.participate.data.dto.EditRolePermissionDTO;
import de.vinado.wicket.participate.data.dto.RoleDTO;
import de.vinado.wicket.participate.data.permission.PersonToRole;
import de.vinado.wicket.participate.data.permission.Role;
import de.vinado.wicket.participate.data.permission.RoleToPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.text.Normalizer;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Service
public class RoleService extends DataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleService.class);

    @Autowired
    private ListOfValueService listOfValueService;

    @Override
    @PersistenceContext
    public void setEntityManager(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public Role createRole(final RoleDTO dto) {
        final String identifier = Normalizer.normalize(dto.getName(), Normalizer.Form.NFD)
                .replaceAll("[^A-Za-z0-9]", "")
                .toUpperCase();
        final Role role = new Role(identifier, dto.getName(), dto.getDescription());
        return save(role);
    }

    @Transactional
    public Role saveRole(final RoleDTO dto) {
        final Role loadedRole = load(Role.class, dto.getRole().getId());
        loadedRole.setName(dto.getName());
        loadedRole.setDescription(dto.getDescription());
        return save(loadedRole);
    }

    @Transactional
    public void removeRole(final Role role) {
        final Role loadedRole = load(Role.class, role.getId());
        final List<RoleToPermission> loadedRtPList = getRoleToPermission4Role(loadedRole);
        final List<PersonToRole> loadedPtRList = getPersonToRole4Role(loadedRole);

        for (RoleToPermission rtp : loadedRtPList) {
            remove(rtp);
        }
        for (PersonToRole ptr : loadedPtRList) {
            remove(ptr);
        }
        remove(loadedRole);
    }

    @Transactional
    public void addPersonToRole(final Collection<Person> persons, final Role role) {
        final List<Person> personList = getPersons4Role(role);

        for (Person person : persons) {
            if (!personList.contains(person)) {
                save(new PersonToRole(person, role));
            }
        }

        final Role loadedRole = load(Role.class, role.getId());
        save(loadedRole);
    }

    @Transactional
    public void removePersonFromRole(final PersonToRole personToRole) {
        final PersonToRole loadedPersonToRole = load(PersonToRole.class, personToRole.getId());
        final Role loadedRole = loadedPersonToRole.getRole();

        remove(loadedPersonToRole);
        save(loadedRole);
    }

    @Transactional
    public void savePermissions(final EditRolePermissionDTO dto) {
        final List<RoleToPermission> permissionList = dto.getPermissions();
        final Role role = dto.getRole();
        final List<RoleToPermission> loadedPermissionList = getRoleToPermission4Role(role);

        for (RoleToPermission oldRtP : loadedPermissionList) {
            remove(oldRtP);
        }

        for (RoleToPermission newRtP : permissionList) {
            save(new RoleToPermission(newRtP.getPermission(), dto.getRole()));
        }
    }

    public List<Role> getRoles() {
        return getAll(Role.class);
    }

    public List<Person> getPersons4Role(final Role role) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Person> criteriaQuery = criteriaBuilder.createQuery(Person.class);
        final Root<PersonToRole> root = criteriaQuery.from(PersonToRole.class);
        criteriaQuery.where(criteriaBuilder.equal(root.<Role>get("role"), role));
        criteriaQuery.select(root.get("person"));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public List<Role> getRoles4Person(final Person person) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Role> criteriaQuery = criteriaBuilder.createQuery(Role.class);
        final Root<PersonToRole> root = criteriaQuery.from(PersonToRole.class);
        criteriaQuery.where(criteriaBuilder.equal(root.<Person>get("person"), person));
        criteriaQuery.select(root.get("role"));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public List<RoleToPermission> getRoleToPermission4Role(final Role role) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<RoleToPermission> criteriaQuery = criteriaBuilder.createQuery(RoleToPermission.class);
        final Root<RoleToPermission> root = criteriaQuery.from(RoleToPermission.class);
        criteriaQuery.where(criteriaBuilder.equal(root.<Role>get("role"), role));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public List<RoleToPermission> getRoleToPermission4Person(final Person person) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<RoleToPermission> criteriaQuery = criteriaBuilder.createQuery(RoleToPermission.class);
        final Root<RoleToPermission> root = criteriaQuery.from(RoleToPermission.class);
        criteriaQuery.where(criteriaBuilder.equal(root.<Role>get("person"), person));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public List<PersonToRole> getPersonToRole4Role(final Role role) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<PersonToRole> criteriaQuery = criteriaBuilder.createQuery(PersonToRole.class);
        final Root<PersonToRole> root = criteriaQuery.from(PersonToRole.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("role"), role));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public Set<String> getPermissions(final Person person) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
        final Root<RoleToPermission> root = criteriaQuery.from(RoleToPermission.class);
        final Join<RoleToPermission, ListOfValue> permissionJoin = root.join("permission");
        criteriaQuery.select(permissionJoin.get("identifier"));

        final Subquery<Role> roleSubquery = criteriaQuery.subquery(Role.class);
        final Root<PersonToRole> roleRoot = roleSubquery.from(PersonToRole.class);
        final Predicate forRole = criteriaBuilder.equal(roleRoot.get("role"), root.get("role"));
        final Predicate forPerson = criteriaBuilder.equal(roleRoot.get("person"), person);
        roleSubquery.select(roleRoot.get("role"));
        roleSubquery.where(forRole, forPerson);

        criteriaQuery.where(criteriaBuilder.exists(roleSubquery));
        return new HashSet<>(entityManager.createQuery(criteriaQuery).getResultList());
    }

    public Set<String> getAllPermissions() {
        return new HashSet<>(listOfValueService.convertToIdentifierList(listOfValueService.getConfigurableList(Permission.class)));
    }

    public Role getRole4Identifier(final String identifier) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Role> criteriaQuery = criteriaBuilder.createQuery(Role.class);
        final Root<Role> root = criteriaQuery.from(Role.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("identifier"), identifier));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.warn("Role for identifier={} could not be found.", identifier);
            return null;
        }
    }

    public Role getAdministratorRole() {
        return getRole4Identifier("ADMIN");
    }

    public Role getDefaultRole() {
        return getRole4Identifier("DEFAULT");
    }

    public boolean roleExist(final String identifierAsName) {
        final String identifier = Normalizer.normalize(identifierAsName, Normalizer.Form.NFD)
                .replaceAll("[^A-Za-z0-9]", "")
                .toUpperCase();
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<Role> root = criteriaQuery.from(Role.class);
        criteriaQuery.where(criteriaBuilder.equal(root.<String>get("identifier"), identifier));
        criteriaQuery.select(criteriaBuilder.count(root));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }
}
