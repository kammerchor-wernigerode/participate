package de.vinado.wicket.participate.service;

import de.vinado.wicket.participate.component.provider.SimpleDataProvider;
import de.vinado.wicket.participate.data.Member;
import de.vinado.wicket.participate.data.Participant;
import de.vinado.wicket.participate.data.Person;
import de.vinado.wicket.participate.data.Voice;
import de.vinado.wicket.participate.data.dto.MemberDTO;
import de.vinado.wicket.participate.data.dto.PersonDTO;
import de.vinado.wicket.participate.data.filter.MemberFilter;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.export.CSVDataExporter;
import org.apache.wicket.extensions.markup.html.repeater.data.table.export.IExportableColumn;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.io.ByteArrayOutputStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.util.string.Strings;
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
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.codec.CharEncoding.UTF_8;

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

        return save(loadedMember);
    }

    @Transactional
    public void removeMember(final Member member) {
        final Member loadedMember = load(Member.class, member.getId());
        loadedMember.setActive(false);
        save(loadedMember);
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
     * Returns a List of all {@link Participant} mappings for the given {@link Member}.
     *
     * @param member Member
     * @return List of EventToMember
     */
    public List<Participant> getMemberToEventList(final Member member) {
        try {
            final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            final CriteriaQuery<Participant> criteriaQuery = criteriaBuilder.createQuery(Participant.class);
            final Root<Participant> root = criteriaQuery.from(Participant.class);
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

    public IResourceStream exportMembers() {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        final IDataProvider<Member> dataProvider = new SimpleDataProvider<Member, String>(getAll(Member.class)) {
            @Override
            public String getDefaultSort() {
                return "person.lastName";
            }
        };

        final List<IExportableColumn<Member, ?>> columns = new ArrayList<>();
        columns.add(new PropertyColumn<>(new ResourceModel("lastName", "Surname"), "person.lastName"));
        columns.add(new PropertyColumn<>(new ResourceModel("firstName", "Given Name"), "person.firstName"));
        columns.add(new PropertyColumn<>(new ResourceModel("email", "Email"), "person.email"));
        columns.add(new PropertyColumn<>(new ResourceModel("voice", "Voice"), "voice.name"));
        columns.add(new PropertyColumn<>(new ResourceModel("active", "Active"), "active"));

        final CSVDataExporter dataExporter = new CSVDataExporter();
        dataExporter.setDelimiter(';');
        dataExporter.setCharacterSet(UTF_8);
        try {
            dataExporter.exportData(dataProvider, columns, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new StringResourceStream(new String(outputStream.toByteArray()));
    }
}
