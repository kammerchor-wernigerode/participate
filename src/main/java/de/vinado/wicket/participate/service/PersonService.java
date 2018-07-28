package de.vinado.wicket.participate.service;

import de.vinado.wicket.participate.providers.SimpleDataProvider;
import de.vinado.wicket.participate.data.Participant;
import de.vinado.wicket.participate.data.Person;
import de.vinado.wicket.participate.data.Singer;
import de.vinado.wicket.participate.data.Voice;
import de.vinado.wicket.participate.data.dto.PersonDTO;
import de.vinado.wicket.participate.data.dto.SingerDTO;
import de.vinado.wicket.participate.data.filter.SingerFilter;
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
 * Provides interaction with the database. This service takes care of {@link Singer} and singer related objects.
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
     * Creates a new {@link Singer} to the database.
     *
     * @param dto {@link SingerDTO}
     * @return Returns the fresh created Singer
     */
    @Transactional
    public Singer createSinger(final SingerDTO dto) {
        return save(new Singer(dto.getFirstName(), dto.getLastName(), dto.getEmail(), dto.getVoice()));
    }

    /**
     * Saves an existing {@link Singer} into the database.
     *
     * @param dto {@link SingerDTO}
     * @return Returns the saved Singer
     */
    @Transactional
    public Singer saveSinger(final SingerDTO dto) {
        final Singer loadedSinger = load(Singer.class, dto.getSinger().getId());
        loadedSinger.setFirstName(dto.getFirstName());
        loadedSinger.setLastName(dto.getLastName());
        loadedSinger.setEmail(dto.getEmail());
        loadedSinger.setVoice(dto.getVoice());
        return save(loadedSinger);
    }

    @Transactional
    public void removeSinger(final Singer singer) {
        final Singer loadedSinger = load(Singer.class, singer.getId());
        loadedSinger.setActive(false);
        save(loadedSinger);
    }

    public boolean hasPerson(final String email) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<Person> root = criteriaQuery.from(Person.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(criteriaBuilder.lower(root.get("email")), email.toLowerCase()));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    public boolean hasSinger(final Person person) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<Singer> root = criteriaQuery.from(Singer.class);
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

    public List<Singer> getSingers() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Singer> criteriaQuery = criteriaBuilder.createQuery(Singer.class);
        final Root<Singer> root = criteriaQuery.from(Singer.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("active"), true));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public Singer getSinger(final Person person) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Singer> criteriaQuery = criteriaBuilder.createQuery(Singer.class);
        final Root<Singer> root = criteriaQuery.from(Singer.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("person"), person));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.warn("Person {} with, is not an (active) singer.", person.getDisplayName());
            return null;
        }
    }

    public Singer getSinger(final Long singerId) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Singer> criteriaQuery = criteriaBuilder.createQuery(Singer.class);
        final Root<Singer> root = criteriaQuery.from(Singer.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("id"), singerId));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.warn("Singer with id={} could not be found.", singerId);
            return null;
        }
    }

    /**
     * Returns a List of all {@link Participant} mappings for the given {@link Singer}.
     *
     * @param singer Singer
     * @return List of {@link Participant}s
     */
    public List<Participant> getParticipants(final Singer singer) {
        try {
            final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            final CriteriaQuery<Participant> criteriaQuery = criteriaBuilder.createQuery(Participant.class);
            final Root<Participant> root = criteriaQuery.from(Participant.class);
            criteriaQuery.where(criteriaBuilder.equal(root.<Singer>get("singer"), singer));
            return entityManager.createQuery(criteriaQuery).getResultList();
        } catch (final Exception e) {
            LOGGER.info("Mapping between Event and Singer does not exist.", e.getMessage());
            return new ArrayList<>();
        }
    }

    public Singer getSinger(final String email) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Singer> criteriaQuery = criteriaBuilder.createQuery(Singer.class);
        final Root<Singer> root = criteriaQuery.from(Singer.class);
        final Predicate forEmail = criteriaBuilder.equal(root.get("email"), email);
        criteriaQuery.where(forEmail);
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.error("No singers with the email address " + email + " found.");
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

    public List<Singer> findSingers(final String term) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Singer> criteriaQuery = criteriaBuilder.createQuery(Singer.class);
        final Root<Singer> root = criteriaQuery.from(Singer.class);
        criteriaQuery.where(criteriaBuilder.like(
            criteriaBuilder.lower(root.get("searchName")),
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
                            final SingerDTO singerDTO = new SingerDTO();
                            singerDTO.setFirstName(firstName);
                            singerDTO.setLastName(lastName);
                            singerDTO.setEmail(email);
                            createSinger(singerDTO);
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.warn("Could not read input file.");
        }
    }

    public List<Singer> getFilteredSingerList(final SingerFilter filter) {
        if (null == filter) {
            return getSingers();
        }

        if (filter.isShowAll()) {
            return getAll(Singer.class);
        }

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Singer> criteriaQuery = criteriaBuilder.createQuery(Singer.class);
        final Root<Singer> root = criteriaQuery.from(Singer.class);

        final List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("active"), true));

        final String searchTerm = filter.getSearchTerm();
        if (!Strings.isEmpty(searchTerm)) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("searchName")), "%" + searchTerm.toLowerCase() + "%"));
        }

        final Voice voice = filter.getVoice();
        if (null != voice) {
            predicates.add(criteriaBuilder.equal(root.get("voice"), voice));
        }

        criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public boolean hasSinger(final String email) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<Singer> root = criteriaQuery.from(Singer.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(root.get("email"), email));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    public IResourceStream exportSingers() {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        final IDataProvider<Singer> dataProvider = new SimpleDataProvider<Singer, String>(getAll(Singer.class)) {
            @Override
            public String getDefaultSort() {
                return "lastName";
            }
        };

        final List<IExportableColumn<Singer, ?>> columns = new ArrayList<>();
        columns.add(new PropertyColumn<>(new ResourceModel("lastName", "Surname"), "lastName"));
        columns.add(new PropertyColumn<>(new ResourceModel("firstName", "Given Name"), "firstName"));
        columns.add(new PropertyColumn<>(new ResourceModel("email", "Email"), "email"));
        columns.add(new PropertyColumn<>(new ResourceModel("voice", "Voice"), "voice"));
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
