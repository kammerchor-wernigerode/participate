package de.vinado.wicket.participate.model.dtos;


import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.model.Voice;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@RequiredArgsConstructor
public class SingerDTO implements Serializable {

    private Singer singer;
    private Voice voice;
    private String firstName;
    private String lastName;
    private String email;

    public SingerDTO(Singer singer) {
        this.singer = singer;
        this.voice = singer.getVoice();
        this.firstName = singer.getFirstName();
        this.lastName = singer.getLastName();
        this.email = singer.getEmail();
    }
}
