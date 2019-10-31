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
import lombok.AccessLevel;
import lombok.Setter;
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
 * This service takes care of persons and person related objects.
 *
 * @author Vincent Nadoll
 */
@Slf4j
@Service
@Setter(value = AccessLevel.PROTECTED, onMethod = @__(@Autowired))
public class PersonService extends DataService {

    @Override
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Creates a new person.
     *
     * @param dto the DTO from which the person is created
     * @return the created person
     */
    @Transactional
    public Person createPerson(PersonDTO dto) {
        return save(new Person(dto.getFirstName(), dto.getLastName(), dto.getEmail()));
    }

    /**
     * Saves an existing person.
     *
     * @param dto the DTO of the person to be updated
     * @return the saved person
     */
    @Transactional
    public Person savePerson(PersonDTO dto) {
        Person loadedPerson = load(Person.class, dto.getPerson().getId());
        loadedPerson.setFirstName(dto.getFirstName());
        loadedPerson.setLastName(dto.getLastName());
        loadedPerson.setEmail(dto.getEmail());
        return save(loadedPerson);
    }

    /**
     * Creates a new singer.
     *
     * @param dto the DTO from which the singer is created
     * @return the created singer
     */
    @Transactional
    public Singer createSinger(SingerDTO dto) {
        return save(new Singer(dto.getFirstName(), dto.getLastName(), dto.getEmail(), dto.getVoice()));
    }

    /**
     * Saves an existing singer.
     *
     * @param dto the DTO of the singer to be updated
     * @return the saved singer
     */
    @Transactional
    public Singer saveSinger(SingerDTO dto) {
        Singer loadedSinger = load(Singer.class, dto.getSinger().getId());
        loadedSinger.setFirstName(dto.getFirstName());
        loadedSinger.setLastName(dto.getLastName());
        loadedSinger.setEmail(dto.getEmail());
        loadedSinger.setVoice(dto.getVoice());
        return save(loadedSinger);
    }

    /**
     * Removes the singer.
     *
     * @param singer the singer to be removed
     */
    @Transactional
    public void removeSinger(Singer singer) {
        Singer loadedSinger = load(Singer.class, singer.getId());
        loadedSinger.setActive(false);
        save(loadedSinger);
    }

    /**
     * @param email the person email to check
     * @return {@code true} if the given email address is assigned to a person; {@code false} otherwise
     */
    public boolean hasPerson(String email) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Person> root = criteriaQuery.from(Person.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(criteriaBuilder.lower(root.get("email")), email.toLowerCase()));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * @param person the person for which the singer should be checked
     * @return {@code true} if a singer is assigned to a person; {@code false} otherwise
     */
    public boolean hasSinger(Person person) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Singer> root = criteriaQuery.from(Singer.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(root.get("id"), person.getId()));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * @param email the singer email to check
     * @return {@code true} if the given email address is assigned to a singer; {@code false} otherwise
     */
    public boolean hasSinger(String email) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Singer> root = criteriaQuery.from(Singer.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(root.get("email"), email));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * Retrieves a person for its ID.
     *
     * @param id the ID of the person to retrieve
     * @return the person with the given ID
     */
    public Person getPerson(Long id) {
        return load(Person.class, id);
    }

    /**
     * Retrieves a person for its email address.
     *
     * @param email the email address of the person to retrieve
     * @return the person with the given email address
     */
    public Person getPerson(String email) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Person> criteriaQuery = criteriaBuilder.createQuery(Person.class);
        Root<Person> root = criteriaQuery.from(Person.class);
        Predicate forEmail = criteriaBuilder.equal(root.get("email"), email);
        criteriaQuery.where(forEmail);
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (NoResultException e) {
            log.trace("Person with email={} could not be found.", e);
            return null;
        }
    }

    /**
     * Retrieves a singer by its ID.
     *
     * @param id the ID of the singer to retrieve
     * @return the singer with the given ID
     */
    public Singer getSinger(Long id) {
        return load(Singer.class, id);
    }

    /**
     * Retrieves all singers.
     *
     * @return list of singers
     */
    public List<Singer> getSingers() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Singer> criteriaQuery = criteriaBuilder.createQuery(Singer.class);
        Root<Singer> root = criteriaQuery.from(Singer.class);
        criteriaQuery.where(forActive(criteriaBuilder, root));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Retrieves a singer for its assigned person.
     *
     * @param person the assigned person of the singer to retrieve
     * @return the singer with the assigned person
     */
    public Singer getSinger(Person person) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Singer> criteriaQuery = criteriaBuilder.createQuery(Singer.class);
        Root<Singer> root = criteriaQuery.from(Singer.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("id"), person.getId()));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (NoResultException e) {
            log.trace("Could not find Singer for person /w id={}", person.getId());
            return null;
        }
    }

    /**
     * Retrieves a singer for its email address.
     *
     * @param email the email address of the singer to retrieve
     * @return the singer with the given email address
     */
    public Singer getSinger(String email) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Singer> criteriaQuery = criteriaBuilder.createQuery(Singer.class);
        Root<Singer> root = criteriaQuery.from(Singer.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("email"), email));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (NoResultException e) {
            log.trace("Could not find Singer for email=****");
            return null;
        }
    }

    /**
     * Retrieves persons where the given substring matches.
     *
     * @param searchNameSubstring the substring of the {@code searchName} to be filtered
     * @return list of filtered persons
     */
    public List<Person> findPersons(String searchNameSubstring) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Person> criteriaQuery = criteriaBuilder.createQuery(Person.class);
        Root<Person> root = criteriaQuery.from(Person.class);
        Predicate forSearchParam = criteriaBuilder.like(
            criteriaBuilder.lower(root.get("searchName")),
            "%" + searchNameSubstring.toLowerCase().trim() + "%");
        criteriaQuery.where(forSearchParam);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Retrieves singers attending the given event.
     *
     * @param event the event on which the singers attend
     * @return list of attending singers
     */
    public List<Singer> getSingers(Event event) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Singer> criteriaQuery = criteriaBuilder.createQuery(Singer.class);
        Root<Participant> root = criteriaQuery.from(Participant.class);
        criteriaQuery.select(root.join("singer"));
        criteriaQuery.where(criteriaBuilder.equal(root.get("event"), event));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Retrieves singers where the given substring matches.
     *
     * @param searchNameSubstring the substring of the {@code searchName} to be filtered
     * @return list of filtered singers
     */
    public List<Singer> findSingers(String searchNameSubstring) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Singer> criteriaQuery = criteriaBuilder.createQuery(Singer.class);
        Root<Singer> root = criteriaQuery.from(Singer.class);
        criteriaQuery.where(criteriaBuilder.like(
            criteriaBuilder.lower(root.get("searchName")),
            "%" + searchNameSubstring.toLowerCase() + "%"
        ));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Fetches all singers that matches the given filter.
     *
     * @param filter the filter to apply to the singers
     * @return list of filtered singers
     */
    public List<Singer> getFilteredSingerList(SingerFilter filter) {
        if (null == filter) {
            return getSingers();
        }

        if (filter.isShowAll()) {
            return getAll(Singer.class);
        }

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Singer> criteriaQuery = criteriaBuilder.createQuery(Singer.class);
        Root<Singer> root = criteriaQuery.from(Singer.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("active"), true));

        String searchTerm = filter.getSearchTerm();
        if (!Strings.isEmpty(searchTerm)) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("searchName")), "%" + searchTerm.toLowerCase() + "%"));
        }

        Voice voice = filter.getVoice();
        if (null != voice) {
            predicates.add(criteriaBuilder.equal(root.get("voice"), voice));
        }

        criteriaQuery.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Handles the import of a CSV file with entries of persons. The method need Wickets {@link FileUpload} component.
     * The columns has to be separated through comma.
     *
     * @param upload the uploaded file
     */
    public void importPersons(FileUpload upload) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(upload.getInputStream(), Charset.forName("UTF-8")));
            String line;
            while (null != (line = bufferedReader.readLine())) {
                String[] personCSV = line.split(",");

                if (personCSV.length == 3) {
                    String firstName = personCSV[1];
                    String lastName = personCSV[0];
                    String email = personCSV[2];

                    if (!hasPerson(email)) {
                        if (!Strings.isEmpty(firstName) && !Strings.isEmpty(lastName) && !Strings.isEmpty(email)) {
                            SingerDTO singerDTO = new SingerDTO();
                            singerDTO.setFirstName(firstName);
                            singerDTO.setLastName(lastName);
                            singerDTO.setEmail(email);

                            Singer singer = createSinger(singerDTO);
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
     * Handles the export of all singer entries from the database. The resulting CSV is separated by semicolon.
     *
     * @return {@link StringResourceStream}
     */
    public IResourceStream exportSingers() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        IDataProvider<Singer> dataProvider = new SimpleDataProvider<Singer, String>(getAll(Singer.class)) {
            @Override
            public String getDefaultSort() {
                return "lastName";
            }
        };

        List<IExportableColumn<Singer, ?>> columns = new ArrayList<>();
        columns.add(new PropertyColumn<>(new ResourceModel("lastName", "Surname"), "lastName"));
        columns.add(new PropertyColumn<>(new ResourceModel("firstName", "Given Name"), "firstName"));
        columns.add(new PropertyColumn<>(new ResourceModel("email", "Email"), "email"));
        columns.add(new PropertyColumn<>(new ResourceModel("voice", "Voice"), "voice"));
        columns.add(new PropertyColumn<>(new ResourceModel("active", "Active"), "active"));

        CSVDataExporter dataExporter = new CSVDataExporter();
        dataExporter.setDelimiter(';');
        dataExporter.setCharacterSet(UTF_8);
        try {
            dataExporter.exportData(dataProvider, columns, outputStream);
        } catch (IOException e) {
            log.error("Could not export data", e);
        }

        return new StringResourceStream(new String(outputStream.toByteArray()));
    }
}
