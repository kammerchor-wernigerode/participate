package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.model.dtos.PersonDTO;
import de.vinado.wicket.participate.model.dtos.SingerDTO;
import de.vinado.wicket.participate.model.filters.SingerFilter;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

import java.util.List;
import java.util.stream.Stream;

/**
 * This service takes care of {@link Singer} and singer related objects.
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public interface PersonService {

    /**
     * Creates a new {@link Person}.
     *
     * @param dto {@link PersonDTO}
     * @return Saved {@link Person}
     */
    Person createPerson(PersonDTO dto);

    /**
     * Saves an existing {@link Person}.
     *
     * @param dto {@link PersonDTO}
     * @return Saved {@link Person}
     */
    Person savePerson(PersonDTO dto);

    /**
     * Creates a new {@link Singer} and creates a new {@link Participant} for each upcoming {@link Event}.
     *
     * @param dto {@link SingerDTO}
     * @return Saved {@link Singer}
     */
    Singer createSinger(SingerDTO dto);

    /**
     * Saves an existing {@link Singer}.
     *
     * @param dto {@link SingerDTO}
     * @return Saved {@link Singer}
     */
    Singer saveSinger(SingerDTO dto);

    /**
     * Sets the {@link Singer} to inactive.
     *
     * @param singer {@link Singer}
     */
    void removeSinger(Singer singer);

    /**
     * Returns whether the {@link Person} exists.
     *
     * @param email {@link Person#email}
     * @return Whether the {@link Person} exists
     */
    boolean hasPerson(String email);

    /**
     * Returns whether the {@link Singer} exists.
     *
     * @param person {@link Person}
     * @return Whether the {@link Singer} exists
     */
    boolean hasSinger(Person person);

    /**
     * Returns whether the {@link Singer} exists.
     *
     * @param email {@link Singer#email}
     * @return Whether the {@link Singer} exists
     */
    boolean hasSinger(String email);

    /**
     * Fetches the {@link Person} by its id.
     *
     * @param id {@link Person#id}
     * @return the {@link Person} by its id
     */
    Person getPerson(Long id);

    /**
     * Fetches the {@link Person} for {@link Person#email}.
     *
     * @param email {@link Person#email}
     * @return {@link Person} for {@link Person#email}
     */
    Person getPerson(String email);

    /**
     * Fetches the {@link Singer} by its id.
     *
     * @param id {@link Singer#id}
     * @return the {@link Singer} by its id
     */
    Singer getSinger(Long id);

    /**
     * Fetches all active {@link Singer}s
     *
     * @return List of {@link Singer}s
     */
    List<Singer> getSingers();

    /**
     * Fetches a {@link Singer} for {@link Person}.
     *
     * @param person {@link Person}
     * @return {@link Singer} for {@link Person}
     */
    Singer getSinger(Person person);

    /**
     * Fetches a {@link Singer} for {@link Singer#email}.
     *
     * @param email {@link Singer#email}
     * @return {@link Singer} for {@link Singer#email}
     */
    Singer getSinger(String email);

    /**
     * Fetches all {@link Person}s by {@link Person#searchName} that matches the filter term.
     *
     * @param term Filter term
     * @return Filtered list of {@link Person}s
     */
    List<Person> findPersons(String term);

    /**
     * Fetches all {@link Singer}s that participating in the {@link Event}.
     *
     * @param event {@link Event}
     * @return Participating list of {@link Singer}s
     */
    List<Singer> getSingers(Event event);

    /**
     * Handles the import of a CSV file with entries of {@link Person}s. The method need Wickets {@link FileUpload}
     * component. The columns has to be separated through comma.
     *
     * @param upload {@link FileUpload}
     */
    void importPersons(FileUpload upload);

    /**
     * Handles the export of all {@link Singer} entries from the database. The resulting CSV is separated by semicolon.
     *
     * @return {@link StringResourceStream}
     */
    IResourceStream exportSingers();

    Stream<Singer> listAllSingers();
}
