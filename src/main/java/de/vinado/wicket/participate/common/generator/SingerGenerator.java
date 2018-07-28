package de.vinado.wicket.participate.common.generator;

import de.vinado.wicket.participate.model.Voice;
import de.vinado.wicket.participate.model.dtos.SingerDTO;
import de.vinado.wicket.participate.services.DataService;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class SingerGenerator extends EntityGenerator<SingerDTO> {

    private static SingerGenerator instance = new SingerGenerator();

    private String[] firstNames = {""};

    private String[] lastNames = {""};


    private SingerGenerator() {
    }

    public static SingerGenerator getInstance() {
        return instance;
    }

    @Override
    public SingerDTO generate(final DataService dataService) {
        final List<Voice> voiceList = Collections.unmodifiableList(Arrays.asList(Voice.values()));
        final SingerDTO dto = new SingerDTO();
        dto.setFirstName(randomString(firstNames));
        dto.setLastName(randomString(lastNames));
        dto.setEmail(Normalizer.normalize((dto.getFirstName() + "." + dto.getLastName())
            .toLowerCase(), Normalizer.Form.NFD).replaceAll("\\s+", "-").replaceAll("[^-a-zA-Z0-9.]", "").concat("@participate.tld"));
        dto.setVoice(voiceList.get(new Random().nextInt(voiceList.size())));

        return dto;
    }
}
