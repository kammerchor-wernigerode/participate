package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.model.dtos.PersonDTO;
import de.vinado.wicket.participate.model.dtos.SingerDTO;
import de.vinado.wicket.participate.person.model.PersonRepository;
import de.vinado.wicket.participate.providers.SimpleDataProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.context.annotation.Primary;
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
import java.util.stream.Stream;

import static java.util.function.Predicate.not;
import static org.apache.commons.codec.CharEncoding.UTF_8;

/**
 * Provides interaction with the database. This service takes care of {@link Singer} and singer related objects.
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Primary
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PersonServiceImpl extends DataService implements PersonService, PersonRepository {

    @Override
    @PersistenceContext
    public void setEntityManager(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Person createPerson(final PersonDTO dto) {
        return save(new Person(dto.getFirstName(), dto.getLastName(), dto.getEmail()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Person savePerson(final PersonDTO dto) {
        final Person loadedPerson = load(Person.class, dto.getPerson().getId());
        loadedPerson.setFirstName(dto.getFirstName());
        loadedPerson.setLastName(dto.getLastName());
        loadedPerson.setEmail(dto.getEmail());
        return save(loadedPerson);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Singer createSinger(final SingerDTO dto) {
        final Singer singer = save(new Singer(dto.getFirstName(), dto.getLastName(), dto.getEmail(), dto.getVoice()));

        return singer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Singer saveSinger(final SingerDTO dto) {
        final Singer loadedSinger = load(Singer.class, dto.getSinger().getId());
        loadedSinger.setFirstName(dto.getFirstName());
        loadedSinger.setLastName(dto.getLastName());
        loadedSinger.setEmail(dto.getEmail());
        loadedSinger.setVoice(dto.getVoice());
        return save(loadedSinger);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeSinger(final Singer singer) {
        final Singer loadedSinger = load(Singer.class, singer.getId());
        loadedSinger.setActive(false);
        save(loadedSinger);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPerson(final String email) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<Person> root = criteriaQuery.from(Person.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(criteriaBuilder.lower(root.get("email")), email.toLowerCase()));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasSinger(final Person person) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<Singer> root = criteriaQuery.from(Singer.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(root.get("id"), person.getId()));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasSinger(final String email) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<Singer> root = criteriaQuery.from(Singer.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(root.get("email"), email));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Person getPerson(final Long id) {
        return load(Person.class, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Person getPerson(final String email) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Person> criteriaQuery = criteriaBuilder.createQuery(Person.class);
        final Root<Person> root = criteriaQuery.from(Person.class);
        final Predicate forEmail = criteriaBuilder.equal(root.get("email"), email);
        criteriaQuery.where(forEmail);
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (NoResultException e) {
            log.trace("Person with email={} could not be found.", e);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Singer getSinger(final Long id) {
        return load(Singer.class, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Singer> getSingers() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Singer> criteriaQuery = criteriaBuilder.createQuery(Singer.class);
        final Root<Singer> root = criteriaQuery.from(Singer.class);
        criteriaQuery.where(forActive(criteriaBuilder, root));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Singer getSinger(final Person person) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Singer> criteriaQuery = criteriaBuilder.createQuery(Singer.class);
        final Root<Singer> root = criteriaQuery.from(Singer.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("id"), person.getId()));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (NoResultException e) {
            log.trace("Could not find Singer for person /w id={}", person.getId());
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Singer getSinger(final String email) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Singer> criteriaQuery = criteriaBuilder.createQuery(Singer.class);
        final Root<Singer> root = criteriaQuery.from(Singer.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("email"), email));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (NoResultException e) {
            log.trace("Could not find Singer for email=****");
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
    public List<Singer> getSingers(final Event event) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Singer> criteriaQuery = criteriaBuilder.createQuery(Singer.class);
        final Root<Participant> root = criteriaQuery.from(Participant.class);
        criteriaQuery.select(root.join("singer"));
        criteriaQuery.where(criteriaBuilder.equal(root.get("event"), event));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
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

                            final Singer singer = createSinger(singerDTO);
//                            eventService.getUpcomingEvents().forEach(event -> eventService.createParticipant(event, singer));
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("Could not read from input file", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
            log.error("Could not export data", e);
        }

        return new StringResourceStream(new String(outputStream.toByteArray()));
    }

    @Override
    public Stream<Singer> listAllSingers() {
        return getAll(Singer.class).stream();
    }

    @Override
    public Stream<Person> listInactivePersons() {
        return listAllSingers()
            .filter(not(Singer::isActive))
            .map(Person.class::cast);
    }
}
