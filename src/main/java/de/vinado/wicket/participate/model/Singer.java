package de.vinado.wicket.participate.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Polymorphism;
import org.hibernate.annotations.PolymorphismType;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "singers")
@Polymorphism(type = PolymorphismType.EXPLICIT)
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Singer extends Person implements Hideable {

    @Enumerated
    @Column
    private Voice voice;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    public Singer(String firstName, String lastName, String email, Voice voice) {
        super(firstName, lastName, email);
        this.voice = voice;
        this.active = true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Singer that = (Singer) obj;
        return Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return 11;
    }
}
