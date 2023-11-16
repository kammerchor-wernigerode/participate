package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.model.dtos.PersonDTO;
import de.vinado.wicket.participate.model.dtos.SingerDTO;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.util.resource.IResourceStream;

import java.util.List;
import java.util.stream.Stream;

public interface PersonService {

    Person createPerson(PersonDTO dto);

    Person savePerson(PersonDTO dto);

    Singer createSinger(SingerDTO dto);

    Singer saveSinger(SingerDTO dto);

    void removeSinger(Singer singer);

    boolean hasPerson(String email);

    boolean hasSinger(Person person);

    boolean hasSinger(String email);

    Person getPerson(Long id);

    Person getPerson(String email);

    Singer getSinger(Long id);

    List<Singer> getSingers();

    Singer getSinger(Person person);

    Singer getSinger(String email);

    List<Person> findPersons(String term);

    void importPersons(FileUpload upload);

    IResourceStream exportSingers();

    Stream<Singer> listAllSingers();
}
