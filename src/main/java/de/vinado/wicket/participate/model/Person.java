package de.vinado.wicket.participate.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "persons")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Person implements Identifiable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    private Date creationDate;

    @UpdateTimestamp
    @Setter(AccessLevel.NONE)
    private Date lastModified;

    @Formula("CONCAT(first_name, ' ', last_name)")
    @Setter(AccessLevel.NONE)
    private String displayName;

    @Formula("CONCAT(last_name, ', ', first_name)")
    @Setter(AccessLevel.NONE)
    private String sortName;

    @Formula("CONCAT(first_name, ' ', last_name, ' (', COALESCE(email, 'Email ist nicht hinterlegt'), ')')")
    @Setter(AccessLevel.NONE)
    private String searchName;

    public Person(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person that = (Person) obj;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return 37;
    }
}
