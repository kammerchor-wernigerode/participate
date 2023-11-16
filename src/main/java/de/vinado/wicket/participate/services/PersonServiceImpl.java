package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.model.dtos.PersonDTO;
import de.vinado.wicket.participate.model.dtos.SingerDTO;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import static org.apache.commons.codec.CharEncoding.UTF_8;

@Primary
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PersonServiceImpl extends DataService implements PersonService {

    @Override
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Person createPerson(PersonDTO dto) {
        return save(new Person(dto.getFirstName(), dto.getLastName(), dto.getEmail()));
    }

    @Override
    public Person savePerson(PersonDTO dto) {
        Person loadedPerson = load(Person.class, dto.getPerson().getId());
        loadedPerson.setFirstName(dto.getFirstName());
        loadedPerson.setLastName(dto.getLastName());
        loadedPerson.setEmail(dto.getEmail());
        return save(loadedPerson);
    }

    @Override
    public Singer createSinger(SingerDTO dto) {
        Singer singer = save(new Singer(dto.getFirstName(), dto.getLastName(), dto.getEmail(), dto.getVoice()));

        return singer;
    }

    @Override
    public Singer saveSinger(SingerDTO dto) {
        Singer loadedSinger = load(Singer.class, dto.getSinger().getId());
        loadedSinger.setFirstName(dto.getFirstName());
        loadedSinger.setLastName(dto.getLastName());
        loadedSinger.setEmail(dto.getEmail());
        loadedSinger.setVoice(dto.getVoice());
        return save(loadedSinger);
    }

    @Override
    public void removeSinger(Singer singer) {
        Singer loadedSinger = load(Singer.class, singer.getId());
        loadedSinger.setActive(false);
        save(loadedSinger);
    }

    @Override
    public boolean hasPerson(String email) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Person> root = criteriaQuery.from(Person.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(criteriaBuilder.lower(root.get("email")), email.toLowerCase()));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    @Override
    public boolean hasSinger(Person person) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Singer> root = criteriaQuery.from(Singer.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(root.get("id"), person.getId()));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    @Override
    public boolean hasSinger(String email) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Singer> root = criteriaQuery.from(Singer.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(root.get("email"), email));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    @Override
    public Person getPerson(Long id) {
        return load(Person.class, id);
    }

    @Override
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

    @Override
    public Singer getSinger(Long id) {
        return load(Singer.class, id);
    }

    @Override
    public List<Singer> getSingers() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Singer> criteriaQuery = criteriaBuilder.createQuery(Singer.class);
        Root<Singer> root = criteriaQuery.from(Singer.class);
        criteriaQuery.where(forActive(criteriaBuilder, root));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
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

    @Override
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

    @Override
    public List<Person> findPersons(String term) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Person> criteriaQuery = criteriaBuilder.createQuery(Person.class);
        Root<Person> root = criteriaQuery.from(Person.class);
        Predicate forSearchParam = criteriaBuilder.like(
            criteriaBuilder.lower(root.get("searchName")),
            "%" + term.toLowerCase().trim() + "%");
        criteriaQuery.where(forSearchParam);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
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

                            createSinger(singerDTO);
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("Could not read from input file", e);
        }
    }

    @Override
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

    @Override
    public Stream<Singer> listAllSingers() {
        return getAll(Singer.class).stream();
    }
}
