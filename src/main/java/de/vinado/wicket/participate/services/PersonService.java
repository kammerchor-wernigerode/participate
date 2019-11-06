package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.model.Voice;
import de.vinado.wicket.participate.model.dtos.PersonDTO;
import de.vinado.wicket.participate.model.dtos.SingerDTO;
import de.vinado.wicket.participate.model.filters.SingerFilter;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * This service takes care of persons and person related objects.
 *
 * @author Vincent Nadoll
 */
@Slf4j
@Service
@Setter(value = AccessLevel.PROTECTED, onMethod = @__(@Autowired))
public class PersonService {

    @Setter(onMethod = @__(@PersistenceContext))
    private EntityManager entityManager;
    private PersonRepository personRepository;
    private SingerRepository singerRepository;

    /**
     * Creates a new person.
     *
     * @param dto the DTO from which the person is created
     * @return the created person
     */
    @Transactional
    public Person create(PersonDTO dto) {
        Person person = new Person(dto.getFirstName(), dto.getLastName(), dto.getEmail());
        return personRepository.save(person);
    }

    /**
     * Saves an existing person.
     *
     * @param dto the DTO of the person to be updated
     * @return the saved person
     *
     * @throws NoResultException in case the person to be saved could not be found
     */
    @Transactional
    public Person save(PersonDTO dto) throws NoResultException {
        Person loadedPerson = personRepository.findById(dto.getPerson().getId()).orElseThrow(NoResultException::new);
        loadedPerson.setFirstName(dto.getFirstName());
        loadedPerson.setLastName(dto.getLastName());
        loadedPerson.setEmail(dto.getEmail());
        return personRepository.save(loadedPerson);
    }

    /**
     * Creates a new singer.
     *
     * @param dto the DTO from which the singer is created
     * @return the created singer
     */
    @Transactional
    public Singer create(SingerDTO dto) {
        Singer singer = new Singer(dto.getFirstName(), dto.getLastName(), dto.getEmail(), dto.getVoice());
        return singerRepository.save(singer);
    }

    /**
     * Saves an existing singer.
     *
     * @param dto the DTO of the singer to be updated
     * @return the saved singer
     *
     * @throws NoResultException in case the singer to be saved could not be found
     */
    @Transactional
    public Singer save(SingerDTO dto) throws NoResultException {
        Singer loadedSinger = singerRepository.findById(dto.getSinger().getId()).orElseThrow(NoResultException::new);
        loadedSinger.setFirstName(dto.getFirstName());
        loadedSinger.setLastName(dto.getLastName());
        loadedSinger.setEmail(dto.getEmail());
        loadedSinger.setVoice(dto.getVoice());
        return singerRepository.save(loadedSinger);
    }

    /**
     * Removes the singer.
     *
     * @param singer the singer to be removed
     */
    @Transactional
    public void delete(Singer singer) {
        singerRepository.delete(singer);
    }

    /**
     * @param email the person email to check
     * @return {@code true} if the given email address is assigned to a person; {@code false} otherwise
     */
    public boolean personExist(String email) {
        return personRepository.existsByEmail(email);
    }

    /**
     * @param person the person for which the singer should be checked
     * @return {@code true} if a singer is assigned to a person; {@code false} otherwise
     */
    public boolean singerExist(Person person) {
        return singerRepository.existsById(person.getId());
    }

    /**
     * @param email the singer email to check
     * @return {@code true} if the given email address is assigned to a singer; {@code false} otherwise
     */
    public boolean singerExist(String email) {
        return singerRepository.existsByEmail(email);
    }

    /**
     * Retrieves a person for its ID.
     *
     * @param id the ID of the person to retrieve
     * @return the person with the given ID
     *
     * @throws NoResultException in case the person could not be found
     */
    public Person retrievePerson(Long id) throws NoResultException {
        return personRepository.findById(id).orElseThrow(NoResultException::new);
    }

    /**
     * Retrieves a person for its email address.
     *
     * @param email the email address of the person to retrieve
     * @return the person with the given email address
     *
     * @throws NoResultException in case the person could not be found
     */
    public Person retrievePerson(String email) throws NoResultException {
        return personRepository.findByEmail(email).orElseThrow(NoResultException::new);
    }

    /**
     * Retrieves a singer by its ID.
     *
     * @param id the ID of the singer to retrieve
     * @return the singer with the given ID
     *
     * @throws NoResultException in case the singer could not be found
     */
    public Singer retrieveSinger(Long id) throws NoResultException {
        return singerRepository.findById(id).orElseThrow(NoResultException::new);
    }

    /**
     * Retrieves all singers.
     *
     * @return list of singers
     */
    public List<Singer> list() {
        return singerRepository.findAll();
    }

    /**
     * Retrieves a singer for its assigned person.
     *
     * @param person the assigned person of the singer to retrieve
     * @return the singer with the assigned person
     *
     * @throws NoResultException in case the singer could not be found
     */
    public Singer retrieveSinger(Person person) throws NoResultException {
        return singerRepository.findById(person.getId()).orElseThrow(NoResultException::new);
    }

    /**
     * Retrieves a singer for its email address.
     *
     * @param email the email address of the singer to retrieve
     * @return the singer with the given email address
     *
     * @throws NoResultException in case the singer could not be found
     */
    public Singer retrieveSinger(String email) throws NoResultException {
        return singerRepository.findByEmail(email).orElseThrow(NoResultException::new);
    }

    /**
     * Retrieves persons where the given substring matches.
     *
     * @param searchNameSubstring the substring of the {@code searchName} to be filtered
     * @return list of filtered persons
     */
    public List<Person> findPersons(String searchNameSubstring) {
        return personRepository.findAllBySearchNameLikeIgnoreCase(searchNameSubstring);
    }

    /**
     * Retrieves singers where the given substring matches.
     *
     * @param searchNameSubstring the substring of the {@code searchName} to be filtered
     * @return list of filtered singers
     */
    public List<Singer> findSingers(String searchNameSubstring) {
        return singerRepository.findAllBySearchNameLikeIgnoreCase(searchNameSubstring);
    }

    /**
     * Fetches all singers that matches the given filter.
     *
     * @param filter the filter to apply to the singers
     * @return list of filtered singers
     */
    public List<Singer> list(SingerFilter filter) {
        if (null == filter) {
            return list();
        }

        if (filter.isShowAll()) {
            return singerRepository.findAll();
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
     * Handles the import of a CSV file with entries of persons. Example: Nadoll,Vincent,me@vinado.de\n
     *
     * @param input the CSV as input stream
     */
    @Transactional
    public Stream<Singer> importPersons(InputStream input) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
            return buffer.lines()
                .map(line -> line.split(","))
                .filter(columns -> columns.length == 3)
                .filter(columns -> !personExist(columns[2]))
                .filter(this::nonBlank)
                .map(this::newSingerDto)
                .map(this::create);
        }
    }

    /**
     * Creates a new singer DTO from a stream array.
     *
     * @param columns the singer data as array: 0 = lastName, 1 = firstName, 2 = email address
     * @return new singer DTO
     */
    private SingerDTO newSingerDto(final String[] columns) {
        SingerDTO singerDTO = new SingerDTO();
        singerDTO.setLastName(columns[0]);
        singerDTO.setFirstName(columns[1]);
        singerDTO.setEmail(columns[2]);
        return singerDTO;
    }

    /**
     * Ensures non array entry is {@literal null} or blank.
     *
     * @param strings the array to be checked
     * @return {@code true} if non entry is blank; {@code false} otherwise
     */
    private boolean nonBlank(final String[] strings) {
        for (String string : strings) {
            if (StringUtils.isBlank(string)) {
                return false;
            }
        }

        return true;
    }
}
