package de.vinado.wicket.participate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Polymorphism;
import org.hibernate.annotations.PolymorphismType;
import org.hibernate.annotations.Where;

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
@Where(clause = "is_active = true")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public class Singer extends Person implements Hideable {

    @Enumerated
    @Column
    private Voice voice;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    /**
     * @param firstName The singers given name
     * @param lastName  The singers surname
     * @param email     The singers email address
     * @param voice     The singers voice group
     */
    public Singer(final String firstName, final String lastName, final String email, final Voice voice) {
        super(firstName, lastName, email);
        this.voice = voice;
        this.active = true;
    }
}
