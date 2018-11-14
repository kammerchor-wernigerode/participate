package de.vinado.wicket.participate.model.dtos;


import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.model.Voice;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * DTO for a {@link Singer}
 *
 * @author Julius Felchow (julius.felchow@gmail.com)
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Getter
@Setter
@RequiredArgsConstructor
public class SingerDTO implements Serializable {

    private Singer singer;
    private Voice voice;
    private String firstName;
    private String lastName;
    private String email;

    public SingerDTO(final Singer singer) {
        this.singer = singer;
        this.voice = singer.getVoice();
        this.email = singer.getFirstName();
        this.firstName = singer.getFirstName();
        this.lastName = singer.getLastName();
        this.email = singer.getEmail();
    }
}
