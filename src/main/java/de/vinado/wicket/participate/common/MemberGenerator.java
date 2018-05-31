package de.vinado.wicket.participate.common;

import de.vinado.wicket.participate.data.Person;
import de.vinado.wicket.participate.data.Voice;
import de.vinado.wicket.participate.data.dto.MemberDTO;
import de.vinado.wicket.participate.service.DataService;

import java.text.Normalizer;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class MemberGenerator extends AbstractEntityGenerator<MemberDTO> {

    private static MemberGenerator instance = new MemberGenerator();

    private String[] firstNames = {""};

    private String[] lastNames = {""};


    private MemberGenerator() {
    }

    public static MemberGenerator getInstance() {
        return instance;
    }

    @Override
    public MemberDTO generate(final DataService dataService) {
        final MemberDTO dto = new MemberDTO();
        dto.setPerson(new Person());
        dto.getPerson().setFirstName(randomString(firstNames));
        dto.getPerson().setLastName(randomString(lastNames));
        dto.getPerson().setEmail(Normalizer.normalize((dto.getPerson().getFirstName() + "." + dto.getPerson().getLastName())
                .toLowerCase(), Normalizer.Form.NFD).replaceAll("\\s+", "-").replaceAll("[^-a-zA-Z0-9.]", "").concat("@participate.tld"));
        dto.setVoice(dataService.load(Voice.class, (long) rint(1, 4)));

        return dto;
    }
}
