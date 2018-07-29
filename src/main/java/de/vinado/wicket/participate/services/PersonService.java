package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.model.Voice;
import de.vinado.wicket.participate.model.dtos.PersonDTO;
import de.vinado.wicket.participate.model.dtos.SingerDTO;
import de.vinado.wicket.participate.model.filters.SingerFilter;
import de.vinado.wicket.participate.providers.SimpleDataProvider;
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

    @PersistenceContext
    public void setEntityManager(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Creates a new {@link Person}.
     *
     * @param dto {@link PersonDTO}
     * @return Saved {@link Person}
     */
    @Transactional
    public Person createPerson(final PersonDTO dto) {
        return save(new Person(dto.getFirstName(), dto.getLastName(), dto.getEmail()));
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

    /**
     * Saves an existing {@link Person}.
     *
     * @param dto {@link PersonDTO}
     * @return Saved {@link Person}
     */
    @Transactional
    public Person savePerson(final PersonDTO dto) {
        final Person loadedPerson = load(Person.class, dto.getPerson().getId());
        loadedPerson.setFirstName(dto.getFirstName());
        loadedPerson.setLastName(dto.getLastName());
        loadedPerson.setEmail(dto.getEmail());
        return save(loadedPerson);
    }

    /**
     * Creates a new {@link Singer}.
     *
     * @param dto {@link SingerDTO}
     * @return Saved {@link Singer}
     */
    @Transactional
    public Singer createSinger(final SingerDTO dto) {
        return save(new Singer(dto.getFirstName(), dto.getLastName(), dto.getEmail(), dto.getVoice()));
    }

    /**
     * Saves an existing {@link Singer}.
     *
     * @param dto {@link SingerDTO}
     * @return Saved {@link Singer}
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

    /**
     * Sets the {@link Singer} to inactive.
     *
     * @param singer {@link Singer}
     */
    @Transactional
    public void removeSinger(final Singer singer) {
        final Singer loadedSinger = load(Singer.class, singer.getId());
        loadedSinger.setActive(false);
        save(loadedSinger);
    }

    /**
     * Returns whether the {@link Person} exists.
     *
     * @param email {@link Person#email}
     * @return Whether the {@link Person} exists
     */
    public boolean hasPerson(final String email) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<Person> root = criteriaQuery.from(Person.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(criteriaBuilder.lower(root.get("email")), email.toLowerCase()));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * Returns whether the {@link Singer} exists.
     *
     * @param person {@link Person}
     * @return Whether the {@link Singer} exists
     */
    public boolean hasSinger(final Person person) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<Singer> root = criteriaQuery.from(Singer.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(root.get("person"), person));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * Returns whether the {@link Singer} exists.
     *
     * @param email {@link Singer#email}
     * @return Whether the {@link Singer} exists
     */
    public boolean hasSinger(final String email) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<Singer> root = criteriaQuery.from(Singer.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(root.get("email"), email));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * Fetches the {@link Person} for {@link Person#email}.
     *
     * @param email {@link Person#email}
     * @return {@link Person} for {@link Person#email}
     */
    public Person getPerson(final String email) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Person> criteriaQuery = criteriaBuilder.createQuery(Person.class);
        final Root<Person> root = criteriaQuery.from(Person.class);
        final Predicate forEmail = criteriaBuilder.equal(root.get("email"), email);
        criteriaQuery.where(forEmail);
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (NoResultException e) {
            LOGGER.trace("Person with email={} could not be found.", e);
            return null;
        }
    }

    /**
     * Fetches all active {@link Singer}s
     *
     * @return List of {@link Singer}s
     */
    public List<Singer> getSingers() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Singer> criteriaQuery = criteriaBuilder.createQuery(Singer.class);
        final Root<Singer> root = criteriaQuery.from(Singer.class);
        criteriaQuery.where(forActive(criteriaBuilder, root));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Fetches a {@link Singer} for {@link Person}.
     *
     * @param person {@link Person}
     * @return {@link Singer} for {@link Person}
     */
    public Singer getSinger(final Person person) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Singer> criteriaQuery = criteriaBuilder.createQuery(Singer.class);
        final Root<Singer> root = criteriaQuery.from(Singer.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("person"), person));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (NoResultException e) {
            LOGGER.trace("Could not find Singer for person /w id={}", person.getId());
            return null;
        }
    }

    /**
     * Fetches a {@link Singer} for {@link Singer#email}.
     *
     * @param email {@link Singer#email}
     * @return {@link Singer} for {@link Singer#email}
     */
    public Singer getSinger(final String email) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Singer> criteriaQuery = criteriaBuilder.createQuery(Singer.class);
        final Root<Singer> root = criteriaQuery.from(Singer.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("email"), email));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (NoResultException e) {
            LOGGER.trace("Could not find Singer for email=****");
            return null;
        }
    }

    /**
     * Fetches all {@link Person}s by {@link Person#searchName} that matches the filter term.
     *
     * @param term Filter term
     * @return Filtered list of {@link Person}s
     */
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

    /**
     * Fetches all {@link Singer}s that participating in the {@link Event}.
     *
     * @param event {@link Event}
     * @return Participating list of {@link Singer}s
     */
    public List<Singer> getSingers(final Event event) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Singer> criteriaQuery = criteriaBuilder.createQuery(Singer.class);
        final Root<Participant> root = criteriaQuery.from(Participant.class);
        criteriaQuery.select(root.join("singer"));
        criteriaQuery.where(criteriaBuilder.equal(root.get("event"), event));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Fetches all {@link Singer}s by {@link Singer#searchName} that matches the filter term.
     *
     * @param term Filter term
     * @return Filtered list of {@link Person}s
     */
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

    /**
     * Fetches all {@link Singer}s that matches the {@link SingerFilter}.
     *
     * @param singerFilter {@link SingerFilter}
     * @return List of filtered {@link Singer}s
     */
    public List<Singer> getFilteredSingerList(final SingerFilter singerFilter) {
        if (null == singerFilter) {
            return getSingers();
        }

        if (singerFilter.isShowAll()) {
            return getAll(Singer.class);
        }

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Singer> criteriaQuery = criteriaBuilder.createQuery(Singer.class);
        final Root<Singer> root = criteriaQuery.from(Singer.class);

        final List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("active"), true));

        final String searchTerm = singerFilter.getSearchTerm();
        if (!Strings.isEmpty(searchTerm)) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("searchName")), "%" + searchTerm.toLowerCase() + "%"));
        }

        final Voice voice = singerFilter.getVoice();
        if (null != voice) {
            predicates.add(criteriaBuilder.equal(root.get("voice"), voice));
        }

        criteriaQuery.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Handles the import of a CSV file with entries of {@link Person}s. The method need Wickets {@link FileUpload}
     * component. The columns has to be separated through comma.
     *
     * @param upload {@link FileUpload}
     */
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
            LOGGER.error("Could not read from input file", e);
        }
    }

    /**
     * Handles the export of all {@link Singer} entries from the database. The resulting CSV is separated by semicolon.
     *
     * @return {@link StringResourceStream}
     */
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
            LOGGER.error("Could not export data", e);
        }

        return new StringResourceStream(new String(outputStream.toByteArray()));
    }
}
