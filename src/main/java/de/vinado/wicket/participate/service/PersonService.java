package de.vinado.wicket.participate.service;

import de.vinado.wicket.participate.data.AddressToPerson;
import de.vinado.wicket.participate.data.Attribute;
import de.vinado.wicket.participate.data.AttributeToPerson;
import de.vinado.wicket.participate.data.Event;
import de.vinado.wicket.participate.data.Group;
import de.vinado.wicket.participate.data.Member;
import de.vinado.wicket.participate.data.MemberToEvent;
import de.vinado.wicket.participate.data.MemberToGroup;
import de.vinado.wicket.participate.data.Person;
import de.vinado.wicket.participate.data.Voice;
import de.vinado.wicket.participate.data.dto.GroupDTO;
import de.vinado.wicket.participate.data.dto.MemberDTO;
import de.vinado.wicket.participate.data.dto.MemberToGroupDTO;
import de.vinado.wicket.participate.data.dto.PersonDTO;
import de.vinado.wicket.participate.data.filter.MemberFilter;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Provides interaction with the database. This service takes care of {@link Member} and member related objects.
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Service
public class PersonService extends DataService {

    private final static Logger LOGGER = LoggerFactory.getLogger(PersonService.class);

    @Autowired
    private EventService eventService;

    /**
     * {@link DataService}
     *
     * @param entityManager {@link EntityManager}
     */
    @PersistenceContext
    public void setEntityManager(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public Person createPerson(final PersonDTO dto) {
        Person person = new Person(dto.getFirstName(), dto.getLastName(), dto.getEmail());
        person = save(person);
        return person;
    }

    @Transactional
    public Person getOrCreatePerson(final Person person) {
        final String email = person.getEmail();
        if (hasPerson(email)) {
            return getPerson(email);
        } else {
            return save(new Person(person.getFirstName(), person.getLastName(), email));
        }
    }

    @Transactional
    public Person savePerson(final PersonDTO dto) {
        final Person loadedPerson = load(Person.class, dto.getPerson().getId());
        loadedPerson.setFirstName(dto.getFirstName());
        loadedPerson.setLastName(dto.getLastName());
        loadedPerson.setEmail(dto.getEmail());
        return save(loadedPerson);
    }

    /**
     * Creates a new {@link Member} to the database.
     *
     * @param dto {@link MemberDTO}
     * @return Returns the fresh created Member
     */
    @Transactional
    public Member createMember(final MemberDTO dto) {
        Person person = new Person(dto.getFirstName(), dto.getLastName(), dto.getEmail());
        person = getOrCreatePerson(person);

        Member member = new Member(person, dto.getVoice());
        member = save(member);

        dto.getGroups().add(load(Group.class, 1L));
        for (Group group : dto.getGroups()) {
            assignMemberToGroup(new MemberToGroupDTO(group, Collections.singletonList(member)));
        }

        return member;
    }

    /**
     * Saves an existing {@link Member} into the database.
     *
     * @param dto {@link MemberDTO}
     * @return Returns the saved Member
     */
    @Transactional
    public Member saveMember(final MemberDTO dto) {
        final Member loadedMember = load(Member.class, dto.getMember().getId());
        loadedMember.setVoice(dto.getVoice());

        final Person loadedPerson = load(Person.class, dto.getPerson().getId());
        loadedPerson.setFirstName(dto.getFirstName());
        loadedPerson.setLastName(dto.getLastName());
        loadedPerson.setEmail(dto.getEmail());

        if (!dto.getGroups().isEmpty()) {
            for (Group group : dto.getGroups()) {
                assignMemberToGroup(new MemberToGroupDTO(group, Collections.singletonList(loadedMember)));
            }
        }

        return save(loadedMember);
    }

    @Transactional
    public void removeMember(final Member member) {
        final Member loadedMember = load(Member.class, member.getId());

        for (MemberToGroup memberToGroup : getMemberToGroupList(loadedMember)) {
            remove(load(MemberToGroup.class, memberToGroup.getId()));
        }

        loadedMember.setActive(false);
        save(loadedMember);
    }

    @Transactional
    public Attribute createAttribute(final Attribute attribute) {
        return (new Attribute(attribute.getValue(), attribute.getAttributeType()));
    }

    @Transactional
    public Attribute saveAttribute(final Attribute attribute) {
        final Attribute loadedAttribute = load(Attribute.class, attribute.getId());
        loadedAttribute.setValue(attribute.getValue());
        loadedAttribute.setAttributeType(attribute.getAttributeType());
        return save(loadedAttribute);
    }

    @Transactional
    public void removeAttributeFromPerson(final Person person, final Attribute attribute) {
        final AttributeToPerson attributeToPerson = getAttributeToPerson(person, attribute);
        remove(attributeToPerson);
        remove(attribute);
    }

    @Transactional
    public Group createGroup(final GroupDTO dto) {
        return save(new Group(dto.getName(), dto.getDescription(), dto.getValidUntil()));
    }

    @Transactional
    public Group saveGroup(final GroupDTO dto) {
        final Group loadedGroup = load(Group.class, dto.getGroup().getId());
        loadedGroup.setName(dto.getName());
        loadedGroup.setDescription(dto.getDescription());
        loadedGroup.setValidUntil(dto.getValidUntil());
        return save(loadedGroup);
    }

    @Transactional
    public boolean removeGroup(final Group group) {
        final Date date = new Date();
        final Group loadedGroup = load(Group.class, group.getId());

        if (!loadedGroup.isEditable()) {
            return false;
        }

        for (Event event : eventService.getEventList(loadedGroup)) {
            if (date.before(event.getEndDate())) {
                return false;
            }
        }

        loadedGroup.setActive(false);
        save(loadedGroup);
        return true;
    }

    @Transactional
    public List<MemberToGroup> assignMemberToGroup(final MemberToGroupDTO dto) {
        final Group loadedGroup = load(Group.class, dto.getGroup().getId());
        final List<Member> filteredMemberList = new ArrayList<>();
        final List<MemberToGroup> memberToGroupList = new ArrayList<>();

        for (Member member : dto.getMembers()) {
            if (!hasMemberToGroup(member, loadedGroup)) {
                filteredMemberList.add(member);
            }
        }

        for (Member member : filteredMemberList) {
            memberToGroupList.add(save(new MemberToGroup(member, loadedGroup)));

            for (Event event : eventService.getUpcomingEventList(loadedGroup)) {
                if (!eventService.hasMemberToEvent(member, event)) {
                    eventService.createMemberToEvent(event, member);
                }
            }
        }

        return memberToGroupList;
    }

    @Transactional
    public void dissociateMemberToGroup(final Member member, final Group group) {
        for (Event event : eventService.getUpcomingEventList(group)) {
            if (eventService.hasMemberToEvent(member, event)) {
                remove(eventService.getMemberToEvent(member, event));
            }
        }
        remove(getMemberToGroup(member, group));
    }

    @Transactional
    public void dissociateMemberToGroup(final MemberToGroup memberToGroup) {
        dissociateMemberToGroup(memberToGroup.getMember(), memberToGroup.getGroup());
    }

    public boolean hasPerson(final String email) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<Person> root = criteriaQuery.from(Person.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(criteriaBuilder.lower(root.get("email")), email.toLowerCase()));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    public boolean hasMember(final Person person) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<Member> root = criteriaQuery.from(Member.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(root.get("person"), person));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    public AddressToPerson getAddressToEvent(final Person person) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<AddressToPerson> criteriaQuery = criteriaBuilder.createQuery(AddressToPerson.class);
        final Root<AddressToPerson> root = criteriaQuery.from(AddressToPerson.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("person"), person));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException | NonUniqueResultException e) {
            LOGGER.warn("Zero or more than one addresses where found for person={}", person);
            return null;
        }
    }

    private AttributeToPerson getAttributeToPerson(final Person person, final Attribute attribute) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<AttributeToPerson> criteriaQuery = criteriaBuilder.createQuery(AttributeToPerson.class);
        final Root<AttributeToPerson> root = criteriaQuery.from(AttributeToPerson.class);
        final Predicate forPerson = criteriaBuilder.equal(root.get("person"), person);
        final Predicate forAttribute = criteriaBuilder.equal(root.get("attribute"), attribute);
        criteriaQuery.where(forAttribute, forPerson);
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.warn("Mapping between person={} and attribute={} could not be found.", person.getDisplayName(), attribute.getValue());
            return null;
        }
    }

    public Person getPerson(final Long id) {
        return load(Person.class, id);
    }

    public Person getPerson(final String email) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Person> criteriaQuery = criteriaBuilder.createQuery(Person.class);
        final Root<Person> root = criteriaQuery.from(Person.class);
        final Predicate forEmail = criteriaBuilder.equal(root.get("email"), email);
        criteriaQuery.where(forEmail);
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.warn("Person with email={} could not be found.", e);
            return null;
        }
    }

    public List<Member> getMemberList() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Member> criteriaQuery = criteriaBuilder.createQuery(Member.class);
        final Root<Member> root = criteriaQuery.from(Member.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("active"), true));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public Member getMember(final Person person) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Member> criteriaQuery = criteriaBuilder.createQuery(Member.class);
        final Root<Member> root = criteriaQuery.from(Member.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("person"), person));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.warn("Person {} with, is not an (active) member.", person.getDisplayName());
            return null;
        }
    }

    public Member getMember(final Long memberId) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Member> criteriaQuery = criteriaBuilder.createQuery(Member.class);
        final Root<Member> root = criteriaQuery.from(Member.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("id"), memberId));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.warn("Member with id={} could not be found.", memberId);
            return null;
        }
    }

    /**
     * Returns a List of all {@link MemberToEvent} mappings for the given {@link Member}.
     *
     * @param member Member
     * @return List of EventToMember
     */
    public List<MemberToEvent> getMemberToEventList(final Member member) {
        try {
            final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            final CriteriaQuery<MemberToEvent> criteriaQuery = criteriaBuilder.createQuery(MemberToEvent.class);
            final Root<MemberToEvent> root = criteriaQuery.from(MemberToEvent.class);
            criteriaQuery.where(criteriaBuilder.equal(root.<Member>get("member"), member));
            return entityManager.createQuery(criteriaQuery).getResultList();
        } catch (final Exception e) {
            LOGGER.info("Mapping between Event and Member does not exist.", e.getMessage());
            return new ArrayList<>();
        }
    }

    public Member getMember(final String email) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Member> criteriaQuery = criteriaBuilder.createQuery(Member.class);
        final Root<Member> root = criteriaQuery.from(Member.class);
        final Predicate forEmail = criteriaBuilder.equal(root.get("email"), email);
        criteriaQuery.where(forEmail);
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.error("No members with the email address " + email + " found.");
        }
        return null;
    }

    public List<Person> findPersons(final String term) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Person> criteriaQuery = criteriaBuilder.createQuery(Person.class);
        final Root<Person> root = criteriaQuery.from(Person.class);
        final Predicate forSearchParam = criteriaBuilder.like(
                criteriaBuilder.lower(root.get("searchName")),
                "%" + term.toLowerCase().trim() + "%");
        criteriaQuery.where(forSearchParam);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public List<Member> findMembers(final String term) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Member> criteriaQuery = criteriaBuilder.createQuery(Member.class);
        final Root<Member> root = criteriaQuery.from(Member.class);
        final Join<Member, Person> personJoin = root.join("person");
        criteriaQuery.where(criteriaBuilder.like(
                criteriaBuilder.lower(personJoin.get("searchName")),
                "%" + term.toLowerCase() + "%"
        ));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public List<Group> findGroups(final String term) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Group> criteriaQuery = criteriaBuilder.createQuery(Group.class);
        final Root<Group> root = criteriaQuery.from(Group.class);
        final Predicate forActive = criteriaBuilder.equal(root.get("active"), true);
        final Predicate forEditable = criteriaBuilder.equal(root.get("editable"), true);
        final Predicate forValidDate = criteriaBuilder.greaterThanOrEqualTo(root.get("validUntil"), DateUtils.truncate(new Date(), Calendar.DATE));
        final Predicate forValidNull = criteriaBuilder.isNull(root.get("validUntil"));
        final Predicate forSearchTerm = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + term.toLowerCase() + "%");
        criteriaQuery.where(forActive, forEditable, forSearchTerm, criteriaBuilder.or(forValidDate, forValidNull));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public List<Group> getGroupList() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Group> criteriaQuery = criteriaBuilder.createQuery(Group.class);
        final Root<Group> root = criteriaQuery.from(Group.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("active"), true));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public List<Group> getVisibleGroupList() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Group> criteriaQuery = criteriaBuilder.createQuery(Group.class);
        final Root<Group> root = criteriaQuery.from(Group.class);
        final Predicate forActive = criteriaBuilder.equal(root.get("active"), true);
        final Predicate forEditable = criteriaBuilder.equal(root.get("editable"), true);
        final Predicate forValidDate = criteriaBuilder.greaterThanOrEqualTo(root.get("validUntil"), DateUtils.truncate(new Date(), Calendar.DATE));
        final Predicate forValidNull = criteriaBuilder.isNull(root.get("validUntil"));
        criteriaQuery.where(criteriaBuilder.and(forActive, forEditable, criteriaBuilder.or(forValidDate, forValidNull)));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public List<Group> getGroupList(final Member member) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Group> criteriaQuery = criteriaBuilder.createQuery(Group.class);
        final Root<MemberToGroup> root = criteriaQuery.from(MemberToGroup.class);
        criteriaQuery.select(root.get("group"));
        criteriaQuery.where(criteriaBuilder.equal(root.get("member"), member));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public List<MemberToGroup> getMemberToGroupList(final Group group) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<MemberToGroup> criteriaQuery = criteriaBuilder.createQuery(MemberToGroup.class);
        final Root<MemberToGroup> root = criteriaQuery.from(MemberToGroup.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("group"), group));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public List<MemberToGroup> getMemberToGroupList(final Member member) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<MemberToGroup> criteriaQuery = criteriaBuilder.createQuery(MemberToGroup.class);
        final Root<MemberToGroup> root = criteriaQuery.from(MemberToGroup.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("member"), member));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public List<Member> getGroupMemberList(final Group group) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Member> criteriaQuery = criteriaBuilder.createQuery(Member.class);
        final Root<MemberToGroup> root = criteriaQuery.from(MemberToGroup.class);
        criteriaQuery.select(root.get("member"));
        criteriaQuery.where(criteriaBuilder.equal(root.get("group"), group));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public MemberToGroup getMemberToGroup(final Member member, final Group group) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<MemberToGroup> criteriaQuery = criteriaBuilder.createQuery(MemberToGroup.class);
        final Root<MemberToGroup> root = criteriaQuery.from(MemberToGroup.class);
        final Predicate forMember = criteriaBuilder.equal(root.get("member"), member);
        final Predicate forGroup = criteriaBuilder.equal(root.get("group"), group);
        criteriaQuery.where(forGroup, forMember);
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.warn("MemberToGroup could not be found for member={} and group={}", member.getPerson().getDisplayName(), group.getName());
            return null;
        }
    }

    public boolean hasMemberToGroup(final Member member, final Group group) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<MemberToGroup> root = criteriaQuery.from(MemberToGroup.class);
        final Predicate forMember = criteriaBuilder.equal(root.get("member"), member);
        final Predicate forGroup = criteriaBuilder.equal(root.get("group"), group);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(forMember, forGroup);
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    @Transactional
    public void importPersons(final FileUpload upload) {
        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(upload.getInputStream(), Charset.forName("UTF-8")));
            String line;
            while (null != (line = bufferedReader.readLine())) {
                final String[] personCSV = line.split(",");

                if (personCSV.length == 3) {
                    final String firstName = personCSV[1];
                    final String lastName = personCSV[0];
                    final String email = personCSV[2];

                    if (!hasPerson(email)) {
                        if (!Strings.isEmpty(firstName) && !Strings.isEmpty(lastName) && !Strings.isEmpty(email)) {
                            final PersonDTO personDTO = new PersonDTO();
                            personDTO.setFirstName(firstName);
                            personDTO.setLastName(lastName);
                            personDTO.setEmail(email);
                            final Person person = createPerson(personDTO);

                            final MemberDTO memberDTO = new MemberDTO();
                            memberDTO.setPerson(person);
                            createMember(memberDTO);
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.warn("Could not read input file.");
        }
    }

    public List<Member> getFilteredMemberList(final MemberFilter filter) {
        if (null == filter) {
            return getMemberList();
        }

        if (filter.isShowAll()) {
            return getAll(Member.class);
        }

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Member> criteriaQuery = criteriaBuilder.createQuery(Member.class);
        final Root<Member> root = criteriaQuery.from(Member.class);
        final Join<Member, Person> personJoin = root.join("person");

        final List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("active"), true));

        final String searchTerm = filter.getSearchTerm();
        if (!Strings.isEmpty(searchTerm)) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(personJoin.get("searchName")), "%" + searchTerm.toLowerCase() + "%"));
        }

        final Voice voice = filter.getVoice();
        if (null != voice) {
            predicates.add(criteriaBuilder.equal(root.get("voice"), voice));
        }

        criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public boolean hasMember(final String email) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<Member> root = criteriaQuery.from(Member.class);
        final Join<Member, Person> personJoin = root.join("person", JoinType.LEFT);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(personJoin.get("email"), email));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }
}
