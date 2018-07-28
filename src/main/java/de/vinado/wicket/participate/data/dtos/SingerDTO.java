package de.vinado.wicket.participate.data.dtos;


import de.vinado.wicket.participate.data.Singer;
import de.vinado.wicket.participate.data.Voice;

import java.io.Serializable;

/**
 * DTO for a {@link Singer}
 *
 * @author Julius Felchow (julius.felchow@gmail.com)
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class SingerDTO implements Serializable {

    private Singer singer;

    private Voice voice;

    private String firstName;

    private String lastName;

    private String email;

    public SingerDTO() {
    }

    public SingerDTO(final Singer singer) {
        this.singer = singer;
        this.voice = singer.getVoice();
        this.email = singer.getFirstName();
        this.firstName = singer.getFirstName();
        this.lastName = singer.getLastName();
        this.email = singer.getEmail();
    }

    public Singer getSinger() {
        return singer;
    }

    public void setSinger(final Singer singer) {
        this.singer = singer;
    }

    public Voice getVoice() {
        return voice;
    }

    public void setVoice(final Voice voice) {
        this.voice = voice;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }
}
