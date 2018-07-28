package de.vinado.wicket.participate.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Polymorphism;
import org.hibernate.annotations.PolymorphismType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 * Entity of a Singer.
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 * @see de.vinado.wicket.participate.services.PersonService
 */
@Entity
@Table(name = "singers")
@Polymorphism(type = PolymorphismType.EXPLICIT)
public class Singer extends Person implements Hideable {

    @Enumerated
    @Column
    private Voice voice;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    protected Singer() {
    } // JPA only

    /**
     * @param lastName  The singers surname
     * @param firstName The singers given name
     * @param email     The singers email address
     * @param voice     The singers voice group
     */
    public Singer(final String lastName, final String firstName, final String email, final Voice voice) {
        super(lastName, firstName, email);
        this.voice = voice;
        this.active = true;
    }

    public Voice getVoice() {
        return voice;
    }

    public void setVoice(final Voice voice) {
        this.voice = voice;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof Singer)) return false;

        final Singer singer = (Singer) o;

        return new EqualsBuilder()
            .appendSuper(super.equals(o))
            .append(active, singer.active)
            .append(voice, singer.voice)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .appendSuper(super.hashCode())
            .append(voice)
            .append(active)
            .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("voice", voice)
            .append("active", active)
            .toString();
    }
}
