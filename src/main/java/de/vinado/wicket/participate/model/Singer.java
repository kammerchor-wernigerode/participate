package de.vinado.wicket.participate.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Polymorphism;
import org.hibernate.annotations.PolymorphismType;

import java.util.Objects;

@Entity
@Table(name = "singers")
@Polymorphism(type = PolymorphismType.EXPLICIT)
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Singer extends Person implements Hideable {

    @Enumerated
    @Column(columnDefinition = "integer")
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
