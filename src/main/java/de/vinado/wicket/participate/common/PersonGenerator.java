package de.vinado.wicket.participate.common;

import de.vinado.wicket.participate.data.dto.PersonDTO;
import de.vinado.wicket.participate.service.DataService;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class PersonGenerator extends AbstractEntityGenerator<PersonDTO> {

    private static PersonGenerator instance = new PersonGenerator();

    private String[] firstNames = {""};

    private String[] lastNames = {""};


    private PersonGenerator() {
    }

    public static PersonGenerator getInstance() {
        return instance;
    }

    @Override
    public PersonDTO generate(final DataService dataService) {
        final PersonDTO dto = new PersonDTO();
        dto.setFirstName(randomString(firstNames));
        dto.setLastName(randomString(lastNames));
        dto.setEmail(dto.getFirstName()
                + "."
                + dto.getLastName()
                + "@participate.tld".toLowerCase().replaceAll(" ", ""));
        return dto;
    }
}
